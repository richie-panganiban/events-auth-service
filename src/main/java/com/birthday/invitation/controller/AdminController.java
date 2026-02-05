package com.birthday.invitation.controller;

import com.birthday.invitation.dto.request.CreateEventRequest;
import com.birthday.invitation.dto.request.CreateGuestRequest;
import com.birthday.invitation.dto.request.UpdateEventRequest;
import com.birthday.invitation.dto.response.EventResponse;
import com.birthday.invitation.dto.response.GuestResponse;
import com.birthday.invitation.security.AdminPrincipal;
import com.birthday.invitation.service.EventService;
import com.birthday.invitation.service.GuestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final EventService eventService;
    private final GuestService guestService;

    public AdminController(EventService eventService, GuestService guestService) {
        this.eventService = eventService;
        this.guestService = guestService;
    }

    // Event Management

    @PostMapping("/events")
    public ResponseEntity<EventResponse> createEvent(
            @AuthenticationPrincipal AdminPrincipal principal,
            @Valid @RequestBody CreateEventRequest request) {

        EventResponse event = eventService.createEvent(principal.getAdmin(), request);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventResponse>> listEvents(
            @AuthenticationPrincipal AdminPrincipal principal) {

        List<EventResponse> events = eventService.listEventsForAdmin(principal.getAdminId());
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventResponse> getEvent(
            @PathVariable UUID eventId) {

        EventResponse event = eventService.getEvent(eventId);
        return ResponseEntity.ok(event);
    }

    @PutMapping("/events/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(
            @AuthenticationPrincipal AdminPrincipal principal,
            @PathVariable UUID eventId,
            @Valid @RequestBody UpdateEventRequest request) {

        EventResponse event = eventService.updateEvent(eventId, principal.getAdminId(), request);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @AuthenticationPrincipal AdminPrincipal principal,
            @PathVariable UUID eventId) {

        eventService.deleteEvent(eventId, principal.getAdminId());
        return ResponseEntity.noContent().build();
    }

    // Guest Management

    @PostMapping("/events/{eventId}/guests")
    public ResponseEntity<GuestResponse> addGuest(
            @AuthenticationPrincipal AdminPrincipal principal,
            @PathVariable UUID eventId,
            @Valid @RequestBody CreateGuestRequest request) {

        GuestResponse guest = guestService.createGuest(eventId, principal.getAdminId(), request);
        return ResponseEntity.ok(guest);
    }

    @GetMapping("/events/{eventId}/guests")
    public ResponseEntity<List<GuestResponse>> listGuests(
            @AuthenticationPrincipal AdminPrincipal principal,
            @PathVariable UUID eventId) {

        List<GuestResponse> guests = guestService.listGuestsForEvent(eventId, principal.getAdminId());
        return ResponseEntity.ok(guests);
    }

    @DeleteMapping("/guests/{guestId}")
    public ResponseEntity<Void> deleteGuest(
            @AuthenticationPrincipal AdminPrincipal principal,
            @PathVariable UUID guestId) {

        guestService.deleteGuest(guestId, principal.getAdminId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/guests/{guestId}/regenerate-link")
    public ResponseEntity<GuestResponse> regenerateMagicLink(
            @AuthenticationPrincipal AdminPrincipal principal,
            @PathVariable UUID guestId) {

        GuestResponse guest = guestService.regenerateMagicLink(guestId, principal.getAdminId());
        return ResponseEntity.ok(guest);
    }

    @DeleteMapping("/guests/{guestId}/sessions")
    public ResponseEntity<Void> revokeAllSessions(
            @AuthenticationPrincipal AdminPrincipal principal,
            @PathVariable UUID guestId) {

        guestService.revokeAllSessions(guestId, principal.getAdminId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/guests/{guestId}/deactivate")
    public ResponseEntity<Void> deactivateGuest(
            @AuthenticationPrincipal AdminPrincipal principal,
            @PathVariable UUID guestId) {

        guestService.deactivateGuest(guestId, principal.getAdminId());
        return ResponseEntity.noContent().build();
    }
}
