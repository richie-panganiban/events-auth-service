package com.birthday.invitation.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class TokenGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a cryptographically secure magic link token.
     * 32 bytes = 256 bits of entropy, URL-safe Base64 encoded.
     */
    public String generateMagicLinkToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Generates a cryptographically secure session token.
     * 64 bytes = 512 bits of entropy for session tokens.
     */
    public String generateSessionToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
