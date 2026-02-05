package com.birthday.invitation.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AuthResponse {

    private String sessionToken;
    private String name;
    private String email;
    private UUID eventId;
    private OffsetDateTime expiresAt;

    private AuthResponse() {}

    public static Builder builder() {
        return new Builder();
    }

    public AuthResponse withoutToken() {
        AuthResponse copy = new AuthResponse();
        copy.name = this.name;
        copy.email = this.email;
        copy.eventId = this.eventId;
        copy.expiresAt = this.expiresAt;
        return copy;
    }

    // Getters
    public String getSessionToken() { return sessionToken; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UUID getEventId() { return eventId; }
    public OffsetDateTime getExpiresAt() { return expiresAt; }

    public static class Builder {
        private final AuthResponse response = new AuthResponse();

        public Builder sessionToken(String sessionToken) {
            response.sessionToken = sessionToken;
            return this;
        }

        public Builder name(String name) {
            response.name = name;
            return this;
        }

        public Builder email(String email) {
            response.email = email;
            return this;
        }

        public Builder eventId(UUID eventId) {
            response.eventId = eventId;
            return this;
        }

        public Builder expiresAt(OffsetDateTime expiresAt) {
            response.expiresAt = expiresAt;
            return this;
        }

        public AuthResponse build() {
            return response;
        }
    }
}
