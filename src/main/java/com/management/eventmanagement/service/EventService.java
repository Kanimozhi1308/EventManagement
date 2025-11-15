package com.management.eventmanagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.management.eventmanagement.dto.UserResponseDTO;
import com.management.eventmanagement.model.Event;
import com.management.eventmanagement.model.Participant;
import com.management.eventmanagement.repository.EventRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private EmailService emailService;

    public Event createEvent(Event event) {

        Long adminId = event.getAdminId();

        // Validate that adminId exists
        UserResponseDTO adminDTO = userService.getUserById(adminId);

        // Proceed to save event
        Event savedEvent = eventRepository.save(event);

        // Fetch all participants
        List<Participant> participants = participantService.getAllParticipants();

        // Send notification emails to participants
        // for (Participant p : participants) {
        // emailService.sendEventNotificationEmail(
        // p.getParticipantEmail(),
        // p.getParticipantName(),
        // savedEvent);
        // }
        sendEmailsInBatches(savedEvent, 20);

        return savedEvent;
    }

    @Async
    public void sendEmailsInBatches(Event savedEvent, int batchSize) {
        List<Participant> participants = participantService.getAllParticipants();

        for (int i = 0; i < participants.size(); i += batchSize) {
            int end = Math.min(i + batchSize, participants.size());
            List<Participant> batch = participants.subList(i, end);

            for (Participant p : batch) {
                emailService.sendEventNotificationEmail(
                        p.getParticipantEmail(),
                        p.getParticipantName(),
                        savedEvent);
            }

            try {
                Thread.sleep(1000); // optional: 1 second pause between batches to reduce server load
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long eventId) {
        return eventRepository.findById(eventId);
    }

    // Fetch event by ID
    public Event getEventByEventId(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id " + eventId));
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id " + id));

        // ✅ Update details
        event.setTitle(updatedEvent.getTitle());
        event.setVenue(updatedEvent.getVenue());
        event.setCategory(updatedEvent.getCategory());
        event.setSpeakers(updatedEvent.getSpeakers());
        event.setDescription(updatedEvent.getDescription());
        event.setDate(updatedEvent.getDate());

        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public List<Event> searchEvents(String speakers, String venue, String category) {

        // Search only by speakers
        if (speakers != null && !speakers.isEmpty()) {
            return eventRepository.findBySpeakersContainingIgnoreCase(speakers);
        }

        // Search only by venue
        if (venue != null && !venue.isEmpty()) {
            return eventRepository.findByVenueContainingIgnoreCase(venue);
        }

        // Search only by category
        if (category != null && !category.isEmpty()) {
            return eventRepository.findByCategoryContainingIgnoreCase(category);
        }

        // If nothing is entered, return all events
        return eventRepository.findAll();
    }

    public List<Event> getEventsByAdminId(Long adminId) {
        return eventRepository.findByAdminId(adminId);
    }

}
