package com.management.eventmanagement.controllertest;

import com.management.eventmanagement.controller.AdminController;
import com.management.eventmanagement.model.Event;
import com.management.eventmanagement.model.Participant;
import com.management.eventmanagement.service.EventService;
import com.management.eventmanagement.service.ParticipantService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private ParticipantService participantService;

    @InjectMocks
    private AdminController adminController;

    private Event event;
    private Participant participant;

    @BeforeEach
    void setup() {
        event = new Event();
        event.setId(1L);
        event.setTitle("Tech Conference");
        event.setVenue("Chennai");
        event.setCategory("Technology");
        event.setDate(LocalDate.of(2025, 11, 20));

        participant = new Participant();
        participant.setId(1L);
        participant.setParticipantName("John Doe");
        participant.setParticipantEmail("john@example.com");
    }

    // ✅ Test: createEvent
    @Test
    void testCreateEvent() {
        when(eventService.createEvent(event)).thenReturn(event);

        Event created = adminController.createEvent(event);

        assertNotNull(created);
        assertEquals("Tech Conference", created.getTitle());
        verify(eventService, times(1)).createEvent(event);
    }

    // ✅ Test: updateEvent
    @Test
    void testUpdateEvent() {
        Event updatedEvent = new Event();
        updatedEvent.setTitle("Updated Conference");

        when(eventService.updateEvent(1L, updatedEvent)).thenReturn(updatedEvent);

        Event result = adminController.updateEvent(1L, updatedEvent);

        assertNotNull(result);
        assertEquals("Updated Conference", result.getTitle());
        verify(eventService, times(1)).updateEvent(1L, updatedEvent);
    }

    // ✅ Test: deleteEvent
    @Test
    void testDeleteEvent() {
        doNothing().when(eventService).deleteEvent(1L);

        String response = adminController.deleteEvent(1L);

        assertEquals("Event deleted successfully!", response);
        verify(eventService, times(1)).deleteEvent(1L);
    }

    // ✅ Test: getParticipants
    @Test
    void testGetParticipants() {
        when(participantService.getParticipantsForEvent(1L))
                .thenReturn(List.of(participant));

        List<Participant> participants = adminController.getParticipants(1L);

        assertNotNull(participants);
        assertEquals(1, participants.size());
        assertEquals("John Doe", participants.get(0).getParticipantName());
        verify(participantService, times(1)).getParticipantsForEvent(1L);
    }

    // ✅ Test: searchEvents (corrected for speakers, venue, category)
    @Test
    void testSearchEvents() {
        when(eventService.searchEvents("John", "Chennai", "Technology"))
                .thenReturn(List.of(event));

        ResponseEntity<List<Event>> response = adminController.searchEvents("John", "Chennai", "Technology");

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Tech Conference", response.getBody().get(0).getTitle());

        verify(eventService, times(1))
                .searchEvents("John", "Chennai", "Technology");
    }

    // ✅ Test: viewAttendees — attendees found
    @Test
    void testViewAttendees_Found() {
        when(participantService.getAttendees(1L)).thenReturn(List.of(participant));

        ResponseEntity<List<Participant>> response = adminController.viewAttendees(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getParticipantName());
    }

    // ✅ Test: viewAttendees — no attendees
    @Test
    void testViewAttendees_Empty() {
        when(participantService.getAttendees(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Participant>> response = adminController.viewAttendees(1L);

        assertEquals(204, response.getStatusCodeValue()); // HTTP 204 No Content
    }
}
