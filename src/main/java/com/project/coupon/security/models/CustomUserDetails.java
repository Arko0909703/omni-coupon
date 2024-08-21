package com.project.coupon.security.models;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.project.coupon.authservice.models.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@JsonDeserialize
@Getter
@Setter
public class CustomUserDetails implements UserDetails {
    private String username;
    private String password;
    private List<CustomGrantedAuthority> authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    private User user;
    private  Long userId;

    public User getUser() {
        return user;
    }
    

    private boolean otpRequired;
    public CustomUserDetails() {}

    public CustomUserDetails(User user) {
//        this.user = user;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.enabled = true;
        this.credentialsNonExpired = true;
        this.password = user.getHashedPassword();
        if(user.getEmail() != null) {
            this.username = user.getEmail();
        }
        this.user = user;
        this.userId = userId;

        List<CustomGrantedAuthority> grantedAuthorities = new ArrayList<>();

        // We do not have admin role yet. So, remove it for now
        grantedAuthorities.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Prefix roles with ROLE_
                .collect(Collectors.toList());


        this.authorities = grantedAuthorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
