package org.outsourcing.mhadminapi.auth;

import org.outsourcing.mhadminapi.entity.Admin;
import org.outsourcing.mhadminapi.entity.Enterprise;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L; // `UserPrincipal` 클래스에 추가
    private Admin admin;
    private Enterprise enterprise;

    public UserPrincipal(Admin admin) {
        this.admin = admin;
    }

    public UserPrincipal(Enterprise enterprise) {
        this.enterprise = enterprise;
    }
    public Admin getAdmin() {
        return this.admin;
    }
    public Enterprise getEnterprise() {
        return this.enterprise;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (isAdmin()) {
            return Collections.singletonList(new SimpleGrantedAuthority(String.valueOf(admin.getRole())));
        } else if (isEnterprise()) {
            // 예시로, Enterprise 엔티티에도 getRole 메소드가 있다고 가정
            return Collections.singletonList(new SimpleGrantedAuthority(String.valueOf(enterprise.getRole())));
        }
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        if (isAdmin()) {
            return admin.getPassword();
        } else if (isEnterprise()) {
            return enterprise.getPassword(); // Enterprise 엔티티에 getPassword 메소드가 있다고 가정
        }
        return null;
    }

    @Override
    public String getUsername() {
        if (isAdmin()) {
            return admin.getEmail();
        } else if (isEnterprise()) {
            return enterprise.getLoginId(); // Enterprise 엔티티에 getEmail 메소드가 있다고 가정
        }
        return null;
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

    public static UserPrincipal createAdmin(Admin admin) {
        return new UserPrincipal(admin);
    }

    public static UserPrincipal createEnterprise(Enterprise enterprise) {
        return new UserPrincipal(enterprise);
    }

    public boolean isAdmin() {
        return this.admin != null;
    }

    public boolean isEnterprise() {
        return this.enterprise != null;
    }
}
