package com.birthday.invitation.dto.response;

import com.birthday.invitation.entity.Admin;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AdminResponse {

    private UUID id;
    private String name;
    private String email;
    private boolean isActive;
    private OffsetDateTime createdAt;

    public static AdminResponse from(Admin admin) {
        AdminResponse response = new AdminResponse();
        response.id = admin.getId();
        response.name = admin.getName();
        response.email = admin.getEmail();
        response.isActive = admin.getIsActive();
        response.createdAt = admin.getCreatedAt();
        return response;
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public boolean isActive() { return isActive; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
