package ltd.jellyfish.security.web.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.jellyfish.security.config.Scanner;
import ltd.jellyfish.security.filter.TokenFilter;
import ltd.jellyfish.security.handler.AuthEntryPointHandler;
import ltd.jellyfish.security.handler.FailHandler;
import ltd.jellyfish.security.handler.SuccessHandler;
import ltd.jellyfish.security.web.adapter.ManageAuth;
import ltd.jellyfish.security.web.model.BaseRole;
import ltd.jellyfish.security.web.model.BaseRoleToUser;
import ltd.jellyfish.security.web.model.BaseUser;
import ltd.jellyfish.security.web.service.UserDetailsServiceImpl;
import ltd.jellyfish.utils.TokenUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.HashMap;
import java.util.Map;

public class WebSecureConfig<
        USER extends BaseUser,
        ROLE_TO_USER extends BaseRoleToUser,
        ROLE extends BaseRole,
        USER_MAPPER extends BaseMapper<USER>,
        ROLE_MAPPER extends BaseMapper<ROLE>,
        ROLE_TO_USER_MAPPER extends BaseMapper<ROLE_TO_USER>> {

    private String basePackage;

    private TokenUtils tokenUtils;

    private UserDetailsServiceImpl<
            ROLE,
            USER,
            ROLE_TO_USER,
            ROLE_MAPPER,
            USER_MAPPER,
            ROLE_TO_USER_MAPPER> userDetailsService;

    private ManageAuth<USER, ROLE_TO_USER, ROLE, USER_MAPPER, ROLE_MAPPER, ROLE_TO_USER_MAPPER> manageAuth;

    private Map<String, Object> tokenParams;


    public WebSecureConfig(String basePackage,
                           TokenUtils tokenUtils,
                           UserDetailsServiceImpl<
                                   ROLE,
                                   USER,
                                   ROLE_TO_USER,
                                   ROLE_MAPPER,
                                   USER_MAPPER,
                                   ROLE_TO_USER_MAPPER
                                   > userDetailsService,
                           ManageAuth<
                                   USER,
                                   ROLE_TO_USER,
                                   ROLE,
                                   USER_MAPPER,
                                   ROLE_MAPPER,
                                   ROLE_TO_USER_MAPPER
                                   > manageAuth) {
        this.basePackage = basePackage;
        this.tokenUtils = tokenUtils;
        this.userDetailsService = userDetailsService;
        this.manageAuth = manageAuth;
    }

    public WebSecureConfig(String basePackage,
                           TokenUtils tokenUtils,
                           UserDetailsServiceImpl<
                                   ROLE,
                                   USER,
                                   ROLE_TO_USER,
                                   ROLE_MAPPER,
                                   USER_MAPPER,
                                   ROLE_TO_USER_MAPPER
                                   > userDetailsService,
                           ManageAuth<
                                   USER, ROLE_TO_USER,
                                   ROLE, USER_MAPPER,
                                   ROLE_MAPPER,
                                   ROLE_TO_USER_MAPPER
                                   > manageAuth,
                           Map<String, Object> tokenParams) {
        this.basePackage = basePackage;
        this.tokenUtils = tokenUtils;
        this.userDetailsService = userDetailsService;
        this.manageAuth = manageAuth;
        this.tokenParams = tokenParams;
    }

    public SecurityFilterChain matchers(HttpSecurity http, String loginUrl) throws Exception {
        http.authorizeHttpRequests((request) -> {
            request.requestMatchers(loginUrl).permitAll();
            Map<String, String[]> roleToUrls = new Scanner().getUrlToRole(basePackage);
            for (Map.Entry<String, String[]> entry : roleToUrls.entrySet()) {
                if (entry.getValue()[0].equals("ALL")) {
                    request.requestMatchers(entry.getKey()).permitAll();
                } else {
                    request.requestMatchers(entry.getKey()).hasAnyRole(entry.getValue());
                }
            }
        });

        http.cors((cx) -> {
            cx.disable();
        });
        http.csrf((c) -> {
            c.disable();
        });

        http.formLogin((form) -> {
            form.loginProcessingUrl(loginUrl);
            form.successHandler(new SuccessHandler(tokenUtils, tokenParams));
            form.failureHandler(new FailHandler());
        });
        http.addFilterBefore(
                new TokenFilter<>(basePackage, tokenUtils, loginUrl, userDetailsService),
                UsernamePasswordAuthenticationFilter.class
        ).exceptionHandling((exception) -> {
            exception.authenticationEntryPoint(new AuthEntryPointHandler());
        });
        return http.build();
    }

    public SecurityFilterChain matchers(HttpSecurity http) throws Exception {
        tokenParams = new HashMap<>();
        http.authorizeHttpRequests((request) -> {
            Map<String, String[]> roleToUrls = new Scanner().getUrlToRole(basePackage);
            for (Map.Entry<String, String[]> entry : roleToUrls.entrySet()) {
                if (entry.getValue()[0].equals("ALL")) {
                    request.requestMatchers(entry.getKey()).permitAll();
                } else {
                    request.requestMatchers(entry.getKey()).hasAnyRole(entry.getValue());
                }
            }
        });
        http.formLogin((form) -> {
            System.out.println("OK, ALL");
            System.out.println(2);
            form.loginProcessingUrl("/login");
            form.successHandler(new SuccessHandler(tokenUtils, tokenParams));
            form.failureHandler(new FailHandler());
        });

        http.addFilterBefore(new TokenFilter<>(basePackage, tokenUtils, "/login", userDetailsService), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exception) -> {
                    exception.authenticationEntryPoint(new AuthEntryPointHandler());
                });
        return http.build();
    }

    public SecurityFilterChain matchers(HttpSecurity http, String loginUrl, String successUrl, String failUrl) throws Exception {
        http.authorizeHttpRequests((request) -> {
            request.requestMatchers(loginUrl).permitAll();
            Map<String, String[]> roleToUrls = new Scanner().getUrlToRole(basePackage);
            for (Map.Entry<String, String[]> entry : roleToUrls.entrySet()) {
                if (entry.getValue()[0].equals("ALL")) {
                    request.requestMatchers(entry.getKey()).permitAll();
                } else {
                    request.requestMatchers(entry.getKey()).hasAnyRole(entry.getValue());
                }
            }
        });
        http.formLogin((form) -> {
            System.out.println("forms");
            System.out.println(3);
            form.loginProcessingUrl(loginUrl);
            form.successForwardUrl(successUrl).failureForwardUrl(failUrl);
            form.successHandler(new SuccessHandler(tokenUtils, tokenParams));
            form.failureHandler(new FailHandler());
        });

        http.addFilterBefore(new TokenFilter<>(basePackage, tokenUtils, loginUrl, userDetailsService), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exception) -> {
                    exception.authenticationEntryPoint(new AuthEntryPointHandler());
                });
        return http.build();
    }


    public SecurityFilterChain matchers(HttpSecurity http, String loginUrl, OncePerRequestFilter oncePerRequestFilter) throws Exception {
        http.authorizeHttpRequests((request) -> {
            request.requestMatchers(loginUrl).permitAll();
            Map<String, String[]> roleToUrls = new Scanner().getUrlToRole(basePackage);
            for (Map.Entry<String, String[]> entry : roleToUrls.entrySet()) {
                if (entry.getValue()[0].equals("ALL")) {
                    request.requestMatchers(entry.getKey()).permitAll();
                } else {
                    request.requestMatchers(entry.getKey()).hasAnyRole(entry.getValue());
                }
            }
        });

        http.cors((cx) -> {
            cx.disable();
        });
        http.csrf((c) -> {
            c.disable();
        });

        http.formLogin((form) -> {
            form.loginProcessingUrl(loginUrl);
            form.successHandler(new SuccessHandler(tokenUtils, tokenParams));
            form.failureHandler(new FailHandler());
        });
        http.addFilterBefore(
                oncePerRequestFilter,
                UsernamePasswordAuthenticationFilter.class
        ).exceptionHandling((exception) -> {
            exception.authenticationEntryPoint(new AuthEntryPointHandler());
        });
        return http.build();
    }

    public AuthenticationManager authenticationManager() {
        return manageAuth;
    }

}
