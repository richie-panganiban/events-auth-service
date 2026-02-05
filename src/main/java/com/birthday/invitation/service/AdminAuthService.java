package com.birthday.invitation.service;

import com.birthday.invitation.dto.response.AdminResponse;
import com.birthday.invitation.dto.response.AuthResponse;
import com.birthday.invitation.entity.Admin;
import com.birthday.invitation.entity.AdminSession;
import com.birthday.invitation.exception.InvalidMagicLinkException;
import com.birthday.invitation.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final SessionService sessionService;
    private final TokenGenerator tokenGenerator;
    private final EmailService emailService;

    @Value("${app.admin-magic-link-expiry-minutes:15}")
    private int magicLinkExpiryMinutes;

    public AdminAuthService(
            AdminRepository adminRepository,
            SessionService sessionService,
            TokenGenerator tokenGenerator,
            EmailService emailService) {
        this.adminRepository = adminRepository;
        this.sessionService = sessionService;
        this.tokenGenerator = tokenGenerator;
        this.emailService = emailService;
    }

    @Transactional
    public void requestMagicLink(String email) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidMagicLinkException("No admin account found with this email"));

        if (!admin.getIsActive()) {
            throw new InvalidMagicLinkException("This admin account has been deactivated");
        }

        // Generate new magic link token
        String magicToken = tokenGenerator.generateMagicLinkToken();
        admin.setMagicLinkToken(magicToken);
        admin.setMagicLinkExpiresAt(OffsetDateTime.now().plusMinutes(magicLinkExpiryMinutes));
        adminRepository.save(admin);

        // Send email
        emailService.sendAdminMagicLink(admin.getEmail(), admin.getName(), magicToken);
    }

    @Transactional
    public AuthResponse validateMagicLink(String magicToken, String userAgent, String ipAddress) {
        Admin admin = adminRepository.findByMagicLinkToken(magicToken)
                .orElseThrow(() -> new InvalidMagicLinkException("Invalid or expired login link"));

        if (!admin.getIsActive()) {
            throw new InvalidMagicLinkException("This admin account has been deactivated");
        }

        if (!admin.isMagicLinkValid()) {
            throw new InvalidMagicLinkException("This login link has expired. Please request a new one.");
        }

        // Invalidate the magic link (single use for admins)
        admin.setMagicLinkToken(null);
        admin.setMagicLinkExpiresAt(null);
        adminRepository.save(admin);

        // Create new session
        AdminSession session = sessionService.createAdminSession(admin, userAgent, ipAddress);

        return AuthResponse.builder()
                .sessionToken(session.getSessionToken())
                .name(admin.getName())
                .email(admin.getEmail())
                .expiresAt(session.getExpiresAt())
                .build();
    }

    @Transactional(readOnly = true)
    public Optional<AdminResponse> verifySession(String sessionToken) {
        return sessionService.validateAndRefreshAdminSession(sessionToken)
                .map(session -> AdminResponse.from(session.getAdmin()));
    }

    @Transactional
    public void invalidateSession(String sessionToken) {
        sessionService.revokeAdminSession(sessionToken);
    }
}
