package com.birthday.invitation.service;

import com.birthday.invitation.dto.request.CreateGuestRequest;
import com.birthday.invitation.dto.response.GuestResponse;
import com.birthday.invitation.entity.Event;
import com.birthday.invitation.entity.Guest;
import com.birthday.invitation.exception.GuestNotFoundException;
import com.birthday.invitation.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GuestService {

    private final GuestRepository guestRepository;
    private final EventService eventService;
    private final TokenGenerator tokenGenerator;
    private final EmailService emailService;
    private final SessionService sessionService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public GuestService(
            GuestRepository guestRepository,
            EventService eventService,
            TokenGenerator tokenGenerator,
            EmailService emailService,
            SessionService sessionService) {
        this.guestRepository = guestRepository;
        this.eventService = eventService;
        this.tokenGenerator = tokenGenerator;
        this.emailService = emailService;
        this.sessionService = sessionService;
    }

    @Transactional
    public GuestResponse createGuest(UUID eventId, UUID adminId, CreateGuestRequest request) {
        Event event = eventService.getEventEntity(eventId);

        // Verify admin owns this event
        if (!event.getAdmin().getId().equals(adminId)) {
            throw new GuestNotFoundException("Event not found");
        }

        Guest guest = new Guest();
        guest.setEvent(event);
        guest.setName(request.getName());
        guest.setEmail(request.getEmail());
        guest.setMagicLinkToken(tokenGenerator.generateMagicLinkToken());
        guest.setIsActive(true);

        Guest saved = guestRepository.save(guest);

        // Send invitation email if email is provided
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            emailService.sendGuestMagicLink(
                    saved.getEmail(),
                    saved.getName(),
                    event.getName(),
                    saved.getMagicLinkToken()
            );
        }

        return GuestResponse.fromWithMagicLink(saved, frontendUrl);
    }

    @Transactional(readOnly = true)
    public List<GuestResponse> listGuestsForEvent(UUID eventId, UUID adminId) {
        Event event = eventService.getEventEntity(eventId);

        // Verify admin owns this event
        if (!event.getAdmin().getId().equals(adminId)) {
            throw new GuestNotFoundException("Event not found");
        }

        return guestRepository.findByEventIdOrderByCreatedAtDesc(eventId)
                .stream()
                .map(g -> GuestResponse.fromWithMagicLink(g, frontendUrl))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteGuest(UUID guestId, UUID adminId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new GuestNotFoundException("Guest not found"));

        // Verify admin owns this event
        if (!guest.getEvent().getAdmin().getId().equals(adminId)) {
            throw new GuestNotFoundException("Guest not found");
        }

        guestRepository.delete(guest);
    }

    @Transactional
    public GuestResponse regenerateMagicLink(UUID guestId, UUID adminId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new GuestNotFoundException("Guest not found"));

        // Verify admin owns this event
        if (!guest.getEvent().getAdmin().getId().equals(adminId)) {
            throw new GuestNotFoundException("Guest not found");
        }

        // Revoke all existing sessions for this guest
        sessionService.revokeAllGuestSessions(guestId);

        // Generate new magic link
        guest.setMagicLinkToken(tokenGenerator.generateMagicLinkToken());
        Guest saved = guestRepository.save(guest);

        return GuestResponse.fromWithMagicLink(saved, frontendUrl);
    }

    @Transactional
    public void revokeAllSessions(UUID guestId, UUID adminId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new GuestNotFoundException("Guest not found"));

        // Verify admin owns this event
        if (!guest.getEvent().getAdmin().getId().equals(adminId)) {
            throw new GuestNotFoundException("Guest not found");
        }

        sessionService.revokeAllGuestSessions(guestId);
    }

    @Transactional
    public void deactivateGuest(UUID guestId, UUID adminId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new GuestNotFoundException("Guest not found"));

        // Verify admin owns this event
        if (!guest.getEvent().getAdmin().getId().equals(adminId)) {
            throw new GuestNotFoundException("Guest not found");
        }

        guest.setIsActive(false);
        guestRepository.save(guest);

        // Also revoke all sessions
        sessionService.revokeAllGuestSessions(guestId);
    }
}
