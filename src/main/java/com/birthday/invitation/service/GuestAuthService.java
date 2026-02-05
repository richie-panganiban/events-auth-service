package com.birthday.invitation.service;

import com.birthday.invitation.dto.response.AuthResponse;
import com.birthday.invitation.dto.response.GuestResponse;
import com.birthday.invitation.entity.Guest;
import com.birthday.invitation.entity.GuestSession;
import com.birthday.invitation.exception.InvalidMagicLinkException;
import com.birthday.invitation.repository.GuestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class GuestAuthService {

    private final GuestRepository guestRepository;
    private final SessionService sessionService;

    public GuestAuthService(GuestRepository guestRepository, SessionService sessionService) {
        this.guestRepository = guestRepository;
        this.sessionService = sessionService;
    }

    @Transactional
    public AuthResponse validateMagicLink(String magicToken, String userAgent, String ipAddress) {
        Guest guest = guestRepository.findByMagicLinkTokenWithEvent(magicToken)
                .orElseThrow(() -> new InvalidMagicLinkException("Invalid or expired invitation link"));

        if (!guest.getIsActive()) {
            throw new InvalidMagicLinkException("This invitation has been revoked");
        }

        // Update last accessed timestamp
        guest.setLastAccessedAt(OffsetDateTime.now());
        guestRepository.save(guest);

        // Create new session
        GuestSession session = sessionService.createGuestSession(guest, userAgent, ipAddress);

        return AuthResponse.builder()
                .sessionToken(session.getSessionToken())
                .name(guest.getName())
                .eventId(guest.getEvent().getId())
                .expiresAt(session.getExpiresAt())
                .build();
    }

    @Transactional(readOnly = true)
    public Optional<GuestResponse> verifySession(String sessionToken) {
        return sessionService.validateAndRefreshGuestSession(sessionToken)
                .map(session -> GuestResponse.from(session.getGuest()));
    }

    @Transactional
    public void invalidateSession(String sessionToken) {
        sessionService.revokeGuestSession(sessionToken);
    }
}
