package com.birthday.invitation.dto.response;

import com.birthday.invitation.entity.Event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public class EventResponse {

    private UUID id;
    private String name;
    private String hostName;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private String venueName;
    private String venueAddress;
    private String description;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private int guestCount;

    public static EventResponse from(Event event) {
        EventResponse response = new EventResponse();
        response.id = event.getId();
        response.name = event.getName();
        response.hostName = event.getHostName();
        response.eventDate = event.getEventDate();
        response.eventTime = event.getEventTime();
        response.venueName = event.getVenueName();
        response.venueAddress = event.getVenueAddress();
        response.description = event.getDescription();
        response.createdAt = event.getCreatedAt();
        response.updatedAt = event.getUpdatedAt();
        response.guestCount = event.getGuests() != null ? event.getGuests().size() : 0;
        return response;
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getHostName() { return hostName; }
    public LocalDate getEventDate() { return eventDate; }
    public LocalTime getEventTime() { return eventTime; }
    public String getVenueName() { return venueName; }
    public String getVenueAddress() { return venueAddress; }
    public String getDescription() { return description; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public int getGuestCount() { return guestCount; }
}
