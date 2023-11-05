package ltd.jellyfish.security.web.adapter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.jellyfish.security.web.model.BaseRole;
import ltd.jellyfish.security.web.model.BaseRoleToUser;
import ltd.jellyfish.security.web.model.BaseUser;
import ltd.jellyfish.security.web.service.UserDetailsServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ManageAuth<
        USER extends BaseUser,
        ROLE_TO_USER extends BaseRoleToUser,
        ROLE extends BaseRole,
        USER_MAPPER extends BaseMapper<USER>,
        ROLE_MAPPER extends BaseMapper<ROLE>,
        ROLE_TO_USER_MAPPER extends  BaseMapper<ROLE_TO_USER>> implements AuthenticationManager {

    private USER_MAPPER baseUserMapper;

    private PasswordEncoder passwordEncoder;

    private UserDetailsServiceImpl<
            ROLE,
            USER,
            ROLE_TO_USER,
            ROLE_MAPPER,
            USER_MAPPER,
            ROLE_TO_USER_MAPPER> userDetailsService;

    public ManageAuth(USER_MAPPER baseUserMapper, PasswordEncoder passwordEncoder,  UserDetailsServiceImpl<
            ROLE,
            USER,
            ROLE_TO_USER,
            ROLE_MAPPER,
            USER_MAPPER,
            ROLE_TO_USER_MAPPER> userDetailsService) {
        this.baseUserMapper = baseUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        QueryWrapper<USER> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username);
        USER user = baseUserMapper.selectOne(userQueryWrapper);
        String serverPassword = user.getPassword();
        UserDetails userDetails =  userDetailsService.loadUserByUsername(username);
        if (!passwordEncoder.matches(serverPassword, password)){
            throw new BadCredentialsException("PASSWORD ERROR");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }
}
