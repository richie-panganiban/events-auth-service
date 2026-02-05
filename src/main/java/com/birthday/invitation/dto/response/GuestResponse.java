package com.birthday.invitation.dto.response;

import com.birthday.invitation.entity.Guest;

import java.time.OffsetDateTime;
import java.util.UUID;

public class GuestResponse {

    private UUID id;
    private String name;
    private String email;
    private UUID eventId;
    private boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime lastAccessedAt;
    private String magicLink;

    public static GuestResponse from(Guest guest) {
        GuestResponse response = new GuestResponse();
        response.id = guest.getId();
        response.name = guest.getName();
        response.email = guest.getEmail();
        response.eventId = guest.getEvent().getId();
        response.isActive = guest.getIsActive();
        response.createdAt = guest.getCreatedAt();
        response.lastAccessedAt = guest.getLastAccessedAt();
        return response;
    }

    public static GuestResponse fromWithMagicLink(Guest guest, String frontendUrl) {
        GuestResponse response = from(guest);
        response.magicLink = frontendUrl + "/invite/" + guest.getMagicLinkToken();
        return response;
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UUID getEventId() { return eventId; }
    public boolean isActive() { return isActive; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getLastAccessedAt() { return lastAccessedAt; }
    public String getMagicLink() { return magicLink; }
}
