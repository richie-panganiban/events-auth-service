package com.birthday.invitation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${spring.mail.username:noreply@birthday-invitation.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAdminMagicLink(String email, String name, String magicToken) {
        String magicLinkUrl = frontendUrl + "/admin/login/" + magicToken;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Your Birthday Invitation Admin Login Link");
            message.setText(String.format(
                "Hi %s,\n\n" +
                "Click the link below to log in to the Birthday Invitation admin panel:\n\n" +
                "%s\n\n" +
                "This link will expire in 15 minutes.\n\n" +
                "If you didn't request this link, please ignore this email.\n\n" +
                "Best,\nBirthday Invitation Team",
                name, magicLinkUrl
            ));

            mailSender.send(message);
            log.info("Admin magic link sent to: {}", email);
        } catch (Exception e) {
            // Log the error but also log the link for development purposes
            log.error("Failed to send email to {}: {}", email, e.getMessage());
            log.info("DEV MODE - Admin magic link for {}: {}", email, magicLinkUrl);
        }
    }

    public void sendGuestMagicLink(String email, String name, String eventName, String magicToken) {
        String magicLinkUrl = frontendUrl + "/invite/" + magicToken;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("You're Invited to " + eventName + "!");
            message.setText(String.format(
                "Hi %s,\n\n" +
                "You're invited to %s!\n\n" +
                "Click the link below to view your invitation:\n\n" +
                "%s\n\n" +
                "This is your personal invitation link - you can use it anytime to view the event details.\n\n" +
                "See you there!",
                name, eventName, magicLinkUrl
            ));

            mailSender.send(message);
            log.info("Guest invitation sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", email, e.getMessage());
            log.info("DEV MODE - Guest magic link for {}: {}", email, magicLinkUrl);
        }
    }
}
