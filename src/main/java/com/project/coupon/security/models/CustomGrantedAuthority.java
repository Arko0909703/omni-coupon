package com.project.coupon.security.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.security.core.GrantedAuthority;

@JsonDeserialize
public class CustomGrantedAuthority implements GrantedAuthority {
    //    private Role role;
    private String authority;

    public CustomGrantedAuthority() {}


    public CustomGrantedAuthority(String role) {
//        this.role = role;
        this.authority = role;
    }

    @Override
    public String getAuthority() {
//        return role.getName();
        return authority;
    }
}
