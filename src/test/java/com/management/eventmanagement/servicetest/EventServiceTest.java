package com.management.eventmanagement.servicetest;

import com.management.eventmanagement.dto.UserResponseDTO;
import com.management.eventmanagement.model.Event;
import com.management.eventmanagement.model.Participant;
import com.management.eventmanagement.repository.EventRepository;
import com.management.eventmanagement.service.EmailService;
import com.management.eventmanagement.service.EventService;
import com.management.eventmanagement.service.ParticipantService;
import com.management.eventmanagement.service.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserService userService;

    @Mock
    private ParticipantService participantService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EventService eventService;

    // -----------------------------------------------------
    // TEST: createEvent()
    // -----------------------------------------------------
    @Test
    void testCreateEvent() {

        Event event = new Event();
        event.setAdminId(1L);

        UserResponseDTO adminDTO = new UserResponseDTO();
        when(userService.getUserById(1L)).thenReturn(adminDTO);

        Event saved = new Event();
        saved.setId(10L);

        when(eventRepository.save(event)).thenReturn(saved);

        Participant p1 = new Participant();
        p1.setId(1L);
        p1.setParticipantName("A");
        p1.setParticipantEmail("a@gmail.com");

        Participant p2 = new Participant();
        p2.setId(2L);
        p2.setParticipantName("B");
        p2.setParticipantEmail("b@gmail.com");

        List<Participant> participants = Arrays.asList(p1, p2);

        when(participantService.getAllParticipants()).thenReturn(participants);

        Event result = eventService.createEvent(event);

        assertNotNull(result);
        assertEquals(10L, result.getId());

        verify(userService).getUserById(1L);
        verify(eventRepository).save(event);
        verify(participantService, atLeastOnce()).getAllParticipants();
    }

    // -----------------------------------------------------
    // TEST: getAllEvents()
    // -----------------------------------------------------
    @Test
    void testGetAllEvents() {
        when(eventRepository.findAll()).thenReturn(List.of(new Event(), new Event()));

        List<Event> result = eventService.getAllEvents();

        assertEquals(2, result.size());
        verify(eventRepository).findAll();
    }

    // -----------------------------------------------------
    // TEST: getEventById()
    // -----------------------------------------------------
    @Test
    void testGetEventById() {
        Event event = new Event();
        event.setId(5L);

        when(eventRepository.findById(5L)).thenReturn(Optional.of(event));

        Optional<Event> result = eventService.getEventById(5L);

        assertTrue(result.isPresent());
        assertEquals(5L, result.get().getId());
    }

    // -----------------------------------------------------
    // TEST: getEventByEventId()
    // -----------------------------------------------------
    @Test
    void testGetEventByEventId() {
        Event event = new Event();
        event.setId(11L);

        when(eventRepository.findById(11L)).thenReturn(Optional.of(event));

        Event result = eventService.getEventByEventId(11L);

        assertEquals(11L, result.getId());
    }

    @Test
    void testGetEventByEventId_NotFound() {
        when(eventRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> eventService.getEventByEventId(100L));
    }

    // -----------------------------------------------------
    // TEST: updateEvent()
    // -----------------------------------------------------
    @Test
    void testUpdateEvent() {
        Event existing = new Event();
        existing.setId(1L);

        Event updated = new Event();
        updated.setTitle("New Title");
        updated.setVenue("Hall 1");
        updated.setCategory("Tech");
        updated.setSpeakers("John");
        updated.setDescription("Desc");
        updated.setDate(LocalDate.parse("2025-05-10"));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(eventRepository.save(any(Event.class))).thenReturn(existing);

        Event result = eventService.updateEvent(1L, updated);

        assertEquals("New Title", result.getTitle());
        assertEquals("Hall 1", result.getVenue());
        assertEquals("Tech", result.getCategory());
        assertEquals("John", result.getSpeakers());

        verify(eventRepository).save(existing);
    }

    // -----------------------------------------------------
    // TEST: deleteEvent()
    // -----------------------------------------------------
    @Test
    void testDeleteEvent() {
        eventService.deleteEvent(10L);
        verify(eventRepository).deleteById(10L);
    }

    // -----------------------------------------------------
    // TEST: searchEvents()
    // -----------------------------------------------------
    @Test
    void testSearchBySpeakers() {
        List<Event> events = List.of(new Event());

        when(eventRepository.findBySpeakersContainingIgnoreCase("john"))
                .thenReturn(events);

        List<Event> result = eventService.searchEvents("john", null, null);

        assertEquals(1, result.size());
        verify(eventRepository).findBySpeakersContainingIgnoreCase("john");
    }

    @Test
    void testSearchByVenue() {
        List<Event> events = List.of(new Event());

        when(eventRepository.findByVenueContainingIgnoreCase("hall"))
                .thenReturn(events);

        List<Event> result = eventService.searchEvents(null, "hall", null);

        assertEquals(1, result.size());
        verify(eventRepository).findByVenueContainingIgnoreCase("hall");
    }

    @Test
    void testSearchByCategory() {
        List<Event> events = List.of(new Event());

        when(eventRepository.findByCategoryContainingIgnoreCase("tech"))
                .thenReturn(events);

        List<Event> result = eventService.searchEvents(null, null, "tech");

        assertEquals(1, result.size());
        verify(eventRepository).findByCategoryContainingIgnoreCase("tech");
    }

    @Test
    void testSearchWithoutFilter() {
        List<Event> events = List.of(new Event(), new Event());
        when(eventRepository.findAll()).thenReturn(events);

        List<Event> result = eventService.searchEvents(null, null, null);

        assertEquals(2, result.size());
        verify(eventRepository).findAll();
    }

    // -----------------------------------------------------
    // TEST: getEventsByAdminId()
    // -----------------------------------------------------
    @Test
    void testGetEventsByAdminId() {
        when(eventRepository.findByAdminId(2L))
                .thenReturn(List.of(new Event()));

        List<Event> result = eventService.getEventsByAdminId(2L);

        assertEquals(1, result.size());
        verify(eventRepository).findByAdminId(2L);
    }
}
