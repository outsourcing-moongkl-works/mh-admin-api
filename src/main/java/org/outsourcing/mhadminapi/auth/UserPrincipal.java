package org.outsourcing.mhadminapi.auth;

import org.outsourcing.mhadminapi.entity.Admin;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

//user 객체 생성
public class UserPrincipal implements UserDetails {
    private Admin admin;

    public UserPrincipal(Admin admin) {
        super();
        this.admin = admin;
    }


    public Admin getAdmin() {
        return this.admin;
    }

    public String getAdminId() {
        return String.valueOf(this.admin.getId());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(admin.getRole().getRoleName()));
    }

    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    @Override
    public String getUsername() {
        return admin.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static UserPrincipal create(Admin admin) {
        return new UserPrincipal(admin);
    }
}

