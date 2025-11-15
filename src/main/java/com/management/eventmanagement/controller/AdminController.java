package com.management.eventmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.management.eventmanagement.model.Event;
import com.management.eventmanagement.model.Participant;
import com.management.eventmanagement.service.EventService;
import com.management.eventmanagement.service.ParticipantService;

@RestController
@RequestMapping("/api/event")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private EventService eventService;

    @Autowired
    private ParticipantService participantService;

    // Create Event
    @PostMapping("/add")
    public Event createEvent(@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    // Update Event
    @PutMapping("/edit/{eventId}")
    public Event updateEvent(@PathVariable Long eventId, @RequestBody Event updatedEvent) {
        return eventService.updateEvent(eventId, updatedEvent);
    }

    // Delete Event
    @DeleteMapping("/delete/{eventId}")
    public String deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return "Event deleted successfully!";
    }

    // Get Participants for an Event
    @GetMapping("/{eventId}/participants")
    public List<Participant> getParticipants(@PathVariable Long eventId) {
        return participantService.getParticipantsForEvent(eventId);
    }

    // Search events by date, venue, or category
    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEvents(
            @RequestParam(required = false) String speakers,
            @RequestParam(required = false) String venue,
            @RequestParam(required = false) String category) {

        List<Event> events = eventService.searchEvents(speakers, venue, category);
        return ResponseEntity.ok(events);
    }

    // Get attendees for an event
    @GetMapping("/{id}/attendees")
    public ResponseEntity<List<Participant>> viewAttendees(@PathVariable Long id) {
        List<Participant> attendees = participantService.getAttendees(id);
        if (attendees.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(attendees);
    }

}
