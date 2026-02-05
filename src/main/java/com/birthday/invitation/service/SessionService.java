package com.birthday.invitation.service;

import com.birthday.invitation.entity.Admin;
import com.birthday.invitation.entity.AdminSession;
import com.birthday.invitation.entity.Guest;
import com.birthday.invitation.entity.GuestSession;
import com.birthday.invitation.repository.AdminSessionRepository;
import com.birthday.invitation.repository.GuestSessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {

    private final GuestSessionRepository guestSessionRepository;
    private final AdminSessionRepository adminSessionRepository;
    private final TokenGenerator tokenGenerator;

    @Value("${app.guest-session-validity-days:30}")
    private int guestSessionValidityDays;

    @Value("${app.admin-session-validity-days:7}")
    private int adminSessionValidityDays;

    public SessionService(
            GuestSessionRepository guestSessionRepository,
            AdminSessionRepository adminSessionRepository,
            TokenGenerator tokenGenerator) {
        this.guestSessionRepository = guestSessionRepository;
        this.adminSessionRepository = adminSessionRepository;
        this.tokenGenerator = tokenGenerator;
    }

    // Guest session methods

    @Transactional
    public GuestSession createGuestSession(Guest guest, String userAgent, String ipAddress) {
        GuestSession session = new GuestSession();
        session.setGuest(guest);
        session.setSessionToken(tokenGenerator.generateSessionToken());
        session.setUserAgent(userAgent);
        session.setIpAddress(ipAddress);
        session.setExpiresAt(OffsetDateTime.now().plusDays(guestSessionValidityDays));
        session.setIsRevoked(false);

        return guestSessionRepository.save(session);
    }

    @Transactional
    public Optional<GuestSession> validateAndRefreshGuestSession(String sessionToken) {
        Optional<GuestSession> sessionOpt = guestSessionRepository
                .findBySessionTokenWithGuestAndEvent(sessionToken);

        if (sessionOpt.isPresent()) {
            GuestSession session = sessionOpt.get();

            if (!session.isValid()) {
                return Optional.empty();
            }

            // Update last used timestamp
            session.setLastUsedAt(OffsetDateTime.now());
            guestSessionRepository.save(session);

            return Optional.of(session);
        }

        return Optional.empty();
    }

    @Transactional
    public void revokeGuestSession(String sessionToken) {
        guestSessionRepository.findBySessionToken(sessionToken)
                .ifPresent(session -> {
                    session.setIsRevoked(true);
                    guestSessionRepository.save(session);
                });
    }

    @Transactional
    public void revokeAllGuestSessions(UUID guestId) {
        guestSessionRepository.revokeAllByGuestId(guestId);
    }

    // Admin session methods

    @Transactional
    public AdminSession createAdminSession(Admin admin, String userAgent, String ipAddress) {
        AdminSession session = new AdminSession();
        session.setAdmin(admin);
        session.setSessionToken(tokenGenerator.generateSessionToken());
        session.setUserAgent(userAgent);
        session.setIpAddress(ipAddress);
        session.setExpiresAt(OffsetDateTime.now().plusDays(adminSessionValidityDays));
        session.setIsRevoked(false);

        return adminSessionRepository.save(session);
    }

    @Transactional
    public Optional<AdminSession> validateAndRefreshAdminSession(String sessionToken) {
        Optional<AdminSession> sessionOpt = adminSessionRepository
                .findBySessionTokenAndIsRevokedFalse(sessionToken);

        if (sessionOpt.isPresent()) {
            AdminSession session = sessionOpt.get();

            if (!session.isValid()) {
                return Optional.empty();
            }

            // Update last used timestamp
            session.setLastUsedAt(OffsetDateTime.now());
            adminSessionRepository.save(session);

            return Optional.of(session);
        }

        return Optional.empty();
    }

    @Transactional
    public void revokeAdminSession(String sessionToken) {
        adminSessionRepository.findBySessionToken(sessionToken)
                .ifPresent(session -> {
                    session.setIsRevoked(true);
                    adminSessionRepository.save(session);
                });
    }

    @Transactional
    public void revokeAllAdminSessions(UUID adminId) {
        adminSessionRepository.revokeAllByAdminId(adminId);
    }
}
