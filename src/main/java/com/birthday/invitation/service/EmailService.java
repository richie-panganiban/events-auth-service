package com.birthday.invitation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendAdminMagicLink(String email, String name, String magicToken) {
        String magicLinkUrl = frontendUrl + "/admin/login/" + magicToken;
        log.info("=== ADMIN MAGIC LINK ===");
        log.info("Admin: {} ({})", name, email);
        log.info("Link: {}", magicLinkUrl);
        log.info("========================");
    }

    public void sendGuestMagicLink(String email, String name, String eventName, String magicToken) {
        String magicLinkUrl = frontendUrl + "/invite/" + magicToken;
        log.info("=== GUEST MAGIC LINK ===");
        log.info("Guest: {} ({})", name, email != null ? email : "no email");
        log.info("Event: {}", eventName);
        log.info("Link: {}", magicLinkUrl);
        log.info("========================");
    }
}
