package ltd.jellyfish.bean;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.jellyfish.security.web.adapter.ManageAuth;
import ltd.jellyfish.security.web.config.WebSecureConfig;
import ltd.jellyfish.security.web.model.BaseRole;
import ltd.jellyfish.security.web.model.BaseRoleToUser;
import ltd.jellyfish.security.web.model.BaseUser;
import ltd.jellyfish.security.web.service.UserDetailsServiceImpl;
import ltd.jellyfish.utils.TokenUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

public class ConfigBean<USER extends BaseUser, ROLE extends BaseRole, ROLE_TO_USER extends BaseRoleToUser,
        USER_MAPPER extends BaseMapper<USER>, ROLE_MAPPER extends BaseMapper<ROLE>, ROLE_TO_USER_MAPPER extends BaseMapper<ROLE_TO_USER>> {

    private USER_MAPPER userMapper;

    private ROLE_MAPPER roleMapper;

    private ROLE_TO_USER_MAPPER roleToUserMapper;

    private PasswordEncoder passwordEncoder;

    private TokenUtils tokenUtils;

    private String basePackage;

    private Map<String, Object> tokenParams;

    public ConfigBean(USER_MAPPER userMapper, ROLE_MAPPER roleMapper, ROLE_TO_USER_MAPPER roleToUserMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.roleToUserMapper = roleToUserMapper;
    }

    public ConfigBean(USER_MAPPER userMapper, ROLE_MAPPER roleMapper, ROLE_TO_USER_MAPPER roleToUserMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.roleToUserMapper = roleToUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public ConfigBean(USER_MAPPER userMapper, ROLE_MAPPER roleMapper, ROLE_TO_USER_MAPPER roleToUserMapper, PasswordEncoder passwordEncoder, TokenUtils tokenUtils, String basePackage) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.roleToUserMapper = roleToUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenUtils = tokenUtils;
        this.basePackage = basePackage;
    }

    public ConfigBean(USER_MAPPER userMapper, ROLE_MAPPER roleMapper, ROLE_TO_USER_MAPPER roleToUserMapper, PasswordEncoder passwordEncoder, TokenUtils tokenUtils, String basePackage, Map<String, Object> tokenParams) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.roleToUserMapper = roleToUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenUtils = tokenUtils;
        this.basePackage = basePackage;
        this.tokenParams = tokenParams;
    }

    public Map<String, Object> getTokenParams() {
        return tokenParams;
    }

    public ConfigBean<USER, ROLE, ROLE_TO_USER, USER_MAPPER, ROLE_MAPPER, ROLE_TO_USER_MAPPER> setTokenParams(Map<String, Object> tokenParams) {
        this.tokenParams = tokenParams;
        return this;
    }

    public USER_MAPPER getUserMapper() {
        return userMapper;
    }

    public ConfigBean<USER, ROLE, ROLE_TO_USER, USER_MAPPER, ROLE_MAPPER, ROLE_TO_USER_MAPPER> setUserMapper(USER_MAPPER userMapper) {
        this.userMapper = userMapper;
        return this;
    }

    public ROLE_MAPPER getRoleMapper() {
        return roleMapper;
    }

    public ConfigBean<USER, ROLE, ROLE_TO_USER, USER_MAPPER, ROLE_MAPPER, ROLE_TO_USER_MAPPER> setRoleMapper(ROLE_MAPPER roleMapper) {
        this.roleMapper = roleMapper;
        return this;
    }

    public ROLE_TO_USER_MAPPER getRoleToUserMapper() {
        return roleToUserMapper;
    }

    public ConfigBean<USER, ROLE, ROLE_TO_USER, USER_MAPPER, ROLE_MAPPER, ROLE_TO_USER_MAPPER> setRoleToUserMapper(ROLE_TO_USER_MAPPER roleToUserMapper) {
        this.roleToUserMapper = roleToUserMapper;
        return this;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public ConfigBean<USER, ROLE, ROLE_TO_USER, USER_MAPPER, ROLE_MAPPER, ROLE_TO_USER_MAPPER> setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        return this;
    }

    public TokenUtils getTokenUtils() {
        return tokenUtils;
    }

    public ConfigBean<USER, ROLE, ROLE_TO_USER, USER_MAPPER, ROLE_MAPPER, ROLE_TO_USER_MAPPER> setTokenUtils(TokenUtils tokenUtils) {
        this.tokenUtils = tokenUtils;
        return this;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public ConfigBean<USER, ROLE, ROLE_TO_USER, USER_MAPPER, ROLE_MAPPER, ROLE_TO_USER_MAPPER> setBasePackage(String basePackage) {
        this.basePackage = basePackage;
        return this;
    }

    public UserDetailsServiceImpl userDetailsService(){
        return new UserDetailsServiceImpl(roleMapper, userMapper, roleToUserMapper);
    }

    public ManageAuth manageAuth(){
        return new ManageAuth(userMapper, passwordEncoder, userDetailsService());
    }

    public WebSecureConfig webSecureConfig(){
        return new WebSecureConfig(basePackage, tokenUtils, userDetailsService(), manageAuth(), tokenParams);
    }


}
