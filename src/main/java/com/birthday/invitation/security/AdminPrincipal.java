package com.birthday.invitation.security;

import com.birthday.invitation.entity.Admin;
import com.birthday.invitation.entity.AdminSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class AdminPrincipal implements Principal {

    private final Admin admin;
    private final AdminSession session;

    public AdminPrincipal(Admin admin, AdminSession session) {
        this.admin = admin;
        this.session = session;
    }

    @Override
    public String getName() {
        return admin.getName();
    }

    public UUID getAdminId() {
        return admin.getId();
    }

    public String getEmail() {
        return admin.getEmail();
    }

    public Admin getAdmin() {
        return admin;
    }

    public AdminSession getSession() {
        return session;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
