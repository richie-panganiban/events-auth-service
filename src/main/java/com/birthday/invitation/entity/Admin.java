package com.birthday.invitation.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(name = "magic_link_token", unique = true, length = 64)
    private String magicLinkToken;

    @Column(name = "magic_link_expires_at")
    private OffsetDateTime magicLinkExpiresAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdminSession> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    public boolean isMagicLinkValid() {
        return magicLinkToken != null &&
               magicLinkExpiresAt != null &&
               magicLinkExpiresAt.isAfter(OffsetDateTime.now());
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMagicLinkToken() { return magicLinkToken; }
    public void setMagicLinkToken(String magicLinkToken) { this.magicLinkToken = magicLinkToken; }

    public OffsetDateTime getMagicLinkExpiresAt() { return magicLinkExpiresAt; }
    public void setMagicLinkExpiresAt(OffsetDateTime magicLinkExpiresAt) { this.magicLinkExpiresAt = magicLinkExpiresAt; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public List<AdminSession> getSessions() { return sessions; }
    public void setSessions(List<AdminSession> sessions) { this.sessions = sessions; }

    public List<Event> getEvents() { return events; }
    public void setEvents(List<Event> events) { this.events = events; }
}
