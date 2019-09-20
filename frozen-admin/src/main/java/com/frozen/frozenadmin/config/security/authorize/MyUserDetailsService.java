package com.frozen.frozenadmin.config.security.authorize;

import com.frozen.frozenadmin.po.Role;
import com.frozen.frozenadmin.server.RoleService;
import com.frozen.frozenadmin.server.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: frozen
 * @Date: 2019/4/20 16:41
 * @Description: userDetailsService 认证系统中的用户密码
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.frozen.frozenadmin.po.User user = userService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名或者密码不正确");
        }
        String password = user.getPassword();
        password = (new BCryptPasswordEncoder()).encode(password);
        //用户具有的权限
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        List<Role> roles = roleService.getRolesByUserId(user.getId());
        String[] strArray = new String[roles.size()];
        List<String> rolestr =new ArrayList<>();
        for(int i=0;i<roles.size();i++){
            strArray[i]=roles.get(i).getName();
            rolestr.add(roles.get(i).getName());
        }

        return new User(username,password,getRoles(rolestr));
        //return User.withUsername(username).password(password).authorities(authorities).roles(strArray).disabled(false).accountExpired(false).accountLocked(false).credentialsExpired(false).build();
    }
    private Collection<GrantedAuthority> getRoles(List<String> roles){
        List<GrantedAuthority> list = new ArrayList<>();
        for (String role : roles){
            SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
            list.add(grantedAuthority);
        }
        return list;
    }
}
