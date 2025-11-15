package com.management.eventmanagement.controllertest;

import com.management.eventmanagement.controller.ViewAdminController;
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

import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ViewAdminControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private ParticipantService participantService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private ViewAdminController viewAdminController;

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

    // ==================== DASHBOARD TESTS ====================

    @Test
    void testMyAdminDashboard_ValidSession() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("userName")).thenReturn("Admin");
        when(eventService.getEventsByAdminId(1L)).thenReturn(List.of(event));
        when(participantService.getAllParticipants()).thenReturn(List.of(participant));

        String viewName = viewAdminController.myAdminDashboard(session, model);

        assertEquals("admin", viewName);
        verify(model).addAttribute("userName", "Admin");
        verify(model).addAttribute("adminId", 1L);
        verify(model).addAttribute("events", List.of(event));
        verify(model).addAttribute("allParticipants", List.of(participant));
    }

    @Test
    void testMyAdminDashboard_NoSession() {
        when(session.getAttribute("userId")).thenReturn(null);

        String viewName = viewAdminController.myAdminDashboard(session, model);

        assertEquals("redirect:/login", viewName);
    }

    // ==================== CREATE EVENT ====================

    @Test
    void testAddEvent() {
        when(eventService.createEvent(event)).thenReturn(event);

        String viewName = viewAdminController.addEvent(event);

        assertEquals("redirect:/admin/dashboard", viewName);
        verify(eventService, times(1)).createEvent(event);
    }

    // ==================== UPDATE EVENT ====================

    @Test
    void testUpdateEvent() {
        when(eventService.updateEvent(1L, event)).thenReturn(event);

        String viewName = viewAdminController.updateEvent(1L, event, model);

        assertEquals("redirect:/admin/dashboard", viewName);
        verify(eventService, times(1)).updateEvent(1L, event);
    }

    // ==================== DELETE EVENT ====================

    @Test
    void testDeleteEvent() {
        doNothing().when(eventService).deleteEvent(1L);

        String viewName = viewAdminController.deleteEvent(1L);

        assertEquals("redirect:/admin/dashboard", viewName);
        verify(eventService, times(1)).deleteEvent(1L);
    }

    // ==================== SEARCH EVENTS ====================

    @Test
    void testSearchEvents() {
        when(eventService.searchEvents("John", "Chennai", "Technology"))
                .thenReturn(List.of(event));

        String viewName = viewAdminController.searchEvents(
                "John", "Chennai", "Technology", model);

        assertEquals("admin", viewName);
        verify(model).addAttribute("events", List.of(event));
        verify(model).addAttribute("speakers", "John");
        verify(model).addAttribute("venue", "Chennai");
        verify(model).addAttribute("category", "Technology");
    }

    // ==================== VIEW ALL PARTICIPANTS ====================

    @Test
    void testViewAllParticipants() {
        when(participantService.getAllParticipants()).thenReturn(List.of(participant));

        String viewName = viewAdminController.viewAllParticipants(model);

        assertEquals("admin", viewName);
        verify(model).addAttribute("allParticipants", List.of(participant));
    }

    // ==================== VIEW ATTENDEES ====================

    @Test
    void testViewAttendees() {
        when(participantService.getAttendees(1L)).thenReturn(List.of(participant));

        List<Participant> list = viewAdminController.viewAttendees(1L);

        assertEquals(1, list.size());
        assertEquals("John Doe", list.get(0).getParticipantName());
    }
}
