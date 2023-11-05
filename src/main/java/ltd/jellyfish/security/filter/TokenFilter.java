package ltd.jellyfish.security.filter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ltd.jellyfish.security.config.TokenScanner;
import ltd.jellyfish.security.web.model.BaseRole;
import ltd.jellyfish.security.web.model.BaseRoleToUser;
import ltd.jellyfish.security.web.model.BaseUser;
import ltd.jellyfish.security.web.service.UserDetailsServiceImpl;
import ltd.jellyfish.utils.TokenUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.Map;

public class TokenFilter<
        USER extends BaseUser,
        ROLE_TO_USER extends BaseRoleToUser,
        ROLE extends BaseRole,
        USER_MAPPER extends BaseMapper<USER>,
        ROLE_MAPPER extends BaseMapper<ROLE>,
        ROLE_TO_USER_MAPPER extends  BaseMapper<ROLE_TO_USER>> extends OncePerRequestFilter {

    private String basePackage;

    private TokenUtils tokenUtils;

    private String loginUrl;

    private UserDetailsServiceImpl<
            ROLE,
            USER,
            ROLE_TO_USER,
            ROLE_MAPPER,
            USER_MAPPER,
            ROLE_TO_USER_MAPPER> userDetailsService;

    public TokenFilter(String basePackage, TokenUtils tokenUtils, String loginUrl, UserDetailsServiceImpl<ROLE, USER, ROLE_TO_USER, ROLE_MAPPER, USER_MAPPER, ROLE_TO_USER_MAPPER> userDetailsService) {
        this.basePackage = basePackage;
        this.tokenUtils = tokenUtils;
        this.loginUrl = loginUrl;
        this.userDetailsService = userDetailsService;
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        TokenScanner tokenScanner = new TokenScanner();
        Map<String, Boolean> urlToToken = tokenScanner.getTokenFilterUrl(basePackage);
        String url = request.getRequestURI();
        String token = request.getHeader("token");

        boolean res = getRightUri(urlToToken, url, token);

        if (!url.equals(loginUrl)) {
            if (res) {
                if (!token.equals("")) {
                    String username = tokenUtils.getTokenValue("username", token, String.class);
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        if (tokenUtils.isTokenExpired(token)){
                            throw new AuthenticationException("TOKEN EXPIRED");
                        }else {
                            auth(userDetails, request);
                        }
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    public void auth(UserDetails userDetails, HttpServletRequest request){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private boolean getRightUri(Map<String, Boolean> urlToToken, String uri, String token){
        boolean reply = false;
        for (Map.Entry<String, Boolean> entry : urlToToken.entrySet()){
            if (entry.getValue() && entry.getKey().equals(uri)){
                if (token != null && !token.isEmpty()){
                    reply = true;
                }
            }
        }
        return reply;
    }
}