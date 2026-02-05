package com.birthday.invitation.security;

import com.birthday.invitation.entity.Guest;
import com.birthday.invitation.entity.GuestSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class GuestPrincipal implements Principal {

    private final Guest guest;
    private final GuestSession session;

    public GuestPrincipal(Guest guest, GuestSession session) {
        this.guest = guest;
        this.session = session;
    }

    @Override
    public String getName() {
        return guest.getName();
    }

    public UUID getGuestId() {
        return guest.getId();
    }

    public UUID getEventId() {
        return guest.getEvent().getId();
    }

    public Guest getGuest() {
        return guest;
    }

    public GuestSession getSession() {
        return session;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_GUEST"));
    }
}
