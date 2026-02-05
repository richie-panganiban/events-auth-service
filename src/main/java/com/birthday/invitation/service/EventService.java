package com.birthday.invitation.service;

import com.birthday.invitation.dto.request.CreateEventRequest;
import com.birthday.invitation.dto.request.UpdateEventRequest;
import com.birthday.invitation.dto.response.EventResponse;
import com.birthday.invitation.entity.Admin;
import com.birthday.invitation.entity.Event;
import com.birthday.invitation.exception.EventNotFoundException;
import com.birthday.invitation.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public EventResponse createEvent(Admin admin, CreateEventRequest request) {
        Event event = new Event();
        event.setAdmin(admin);
        event.setName(request.getName());
        event.setHostName(request.getHostName());
        event.setEventDate(request.getEventDate());
        event.setEventTime(request.getEventTime());
        event.setVenueName(request.getVenueName());
        event.setVenueAddress(request.getVenueAddress());
        event.setDescription(request.getDescription());

        Event saved = eventRepository.save(event);
        return EventResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> listEventsForAdmin(UUID adminId) {
        return eventRepository.findByAdminIdOrderByEventDateDesc(adminId)
                .stream()
                .map(EventResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventResponse getEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        return EventResponse.from(event);
    }

    @Transactional(readOnly = true)
    public Event getEventEntity(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
    }

    @Transactional
    public EventResponse updateEvent(UUID eventId, UUID adminId, UpdateEventRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getAdmin().getId().equals(adminId)) {
            throw new EventNotFoundException("Event not found");
        }

        if (request.getName() != null) event.setName(request.getName());
        if (request.getHostName() != null) event.setHostName(request.getHostName());
        if (request.getEventDate() != null) event.setEventDate(request.getEventDate());
        if (request.getEventTime() != null) event.setEventTime(request.getEventTime());
        if (request.getVenueName() != null) event.setVenueName(request.getVenueName());
        if (request.getVenueAddress() != null) event.setVenueAddress(request.getVenueAddress());
        if (request.getDescription() != null) event.setDescription(request.getDescription());

        Event saved = eventRepository.save(event);
        return EventResponse.from(saved);
    }

    @Transactional
    public void deleteEvent(UUID eventId, UUID adminId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getAdmin().getId().equals(adminId)) {
            throw new EventNotFoundException("Event not found");
        }

        eventRepository.delete(event);
    }
}
