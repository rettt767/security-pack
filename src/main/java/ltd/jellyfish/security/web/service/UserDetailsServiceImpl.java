package ltd.jellyfish.security.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.jellyfish.security.web.model.BaseRoleToUser;
import ltd.jellyfish.security.web.model.BaseUser;
import ltd.jellyfish.security.web.model.BaseRole;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl<
        ROLE extends BaseRole,
        USER extends BaseUser,
        ROLE_TO_USER extends BaseRoleToUser,
        ROLE_MAPPER extends BaseMapper<ROLE>,
        USER_MAPPER extends BaseMapper<USER>,
        ROLE_TO_USER_MAPPER extends BaseMapper<ROLE_TO_USER>> implements UserDetailsService {


    private ROLE_MAPPER baseRoleMapper;

    private USER_MAPPER baseUserMapper;

    private ROLE_TO_USER_MAPPER baseRoleToUserMapper;

    public UserDetailsServiceImpl(ROLE_MAPPER baseRoleMapper, USER_MAPPER baseUserMapper, ROLE_TO_USER_MAPPER baseRoleToUserMapper) {
        this.baseRoleMapper = baseRoleMapper;
        this.baseUserMapper = baseUserMapper;
        this.baseRoleToUserMapper = baseRoleToUserMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<USER> baseUserWrapper = new QueryWrapper<>();
        baseUserWrapper.eq("username", username);
        USER user = baseUserMapper.selectOne(baseUserWrapper);
        if (user == null){
            throw new UsernameNotFoundException("USER NOT FOUND");
        }
        return getDetailsUser(user);
    }

    private User getDetailsUser(USER user){
        QueryWrapper<ROLE> rolesQueryWrapper = new QueryWrapper<>();
        QueryWrapper<ROLE_TO_USER> roleToUserQueryWrapper = new QueryWrapper<>();
        roleToUserQueryWrapper.eq("user_id", user.getId());
        List<ROLE_TO_USER> roleToUserList = baseRoleToUserMapper.selectList(roleToUserQueryWrapper);
        List<ROLE> roles = new ArrayList<>();
        for (ROLE_TO_USER roleToUser : roleToUserList){
            rolesQueryWrapper.eq("id", roleToUser.getRoleId());
            ROLE tmp = baseRoleMapper.selectOne(rolesQueryWrapper);
            roles.add(tmp);
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();


        for (ROLE auth : roles){
            authorities.add(new SimpleGrantedAuthority("ROLE_"+auth.getRole()));
        }

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
