package com.birthday.invitation.controller;

import com.birthday.invitation.dto.response.EventResponse;
import com.birthday.invitation.security.GuestPrincipal;
import com.birthday.invitation.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEvent(
            @AuthenticationPrincipal GuestPrincipal principal,
            @PathVariable UUID eventId) {

        // Verify the guest is accessing their own event
        if (principal != null && !principal.getEventId().equals(eventId)) {
            return ResponseEntity.status(403).build();
        }

        EventResponse event = eventService.getEvent(eventId);
        return ResponseEntity.ok(event);
    }
}
