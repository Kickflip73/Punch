package com.yrmjhtdjxh.punch.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class JwtUser implements UserDetails {

    private String username;
    private String password;
    private Integer role;


    public JwtUser(String username, String password, Integer role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    //用户是否未过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //用户是否为锁定
    @Override
    public boolean isAccountNonLocked() {
        //TODO
        return true;
    }

    //密码是否过期
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //用户是否激活
    @Override
    public boolean isEnabled() {
        return true;
    }
}
