package com.management.eventmanagement.controllertest;

import com.management.eventmanagement.controller.ViewParticipantController;
import com.management.eventmanagement.model.Event;
import com.management.eventmanagement.model.Participant;
import com.management.eventmanagement.model.User;
import com.management.eventmanagement.service.EventService;
import com.management.eventmanagement.service.ParticipantService;
import com.management.eventmanagement.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ViewParticipantControllerTest {

    @Mock
    private ParticipantService participantService;

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ViewParticipantController participantController;

    private User user;
    private Event event;
    private Participant participant;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setFullName("John Doe");
        user.setEmail("john@example.com");
        user.setMobileNumber("9876543210");

        event = new Event();
        event.setId(100L);
        event.setTitle("Tech Conference");
        event.setVenue("Chennai");
        event.setCategory("Technology");
        event.setDate(LocalDate.of(2025, 11, 20));

        participant = new Participant(1L, 100L, 1L, "John Doe",
                "john@example.com", "9876543210", LocalDateTime.now());
    }

    // ============================
    // TEST redirectToDashboard
    // ============================

    @Test
    void testRedirectToDashboard_UserExists() {
        when(userService.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        String view = participantController.redirectToDashboard("john@example.com", session);

        assertEquals("redirect:/participant/dashboard", view);

        verify(session).setAttribute("userId", 1L);
        verify(session).setAttribute("userName", "John Doe");
        verify(session).setAttribute("userEmail", "john@example.com");
        verify(session).setAttribute("userPhone", "9876543210");
    }

    @Test
    void testRedirectToDashboard_UserNotFound() {
        when(userService.findByEmail("john@example.com")).thenReturn(Optional.empty());

        String view = participantController.redirectToDashboard("john@example.com", session);

        assertEquals("redirect:/login", view);
    }

    // ============================
    // TEST participantDashboard NORMAL
    // ============================

    @Test
    void testParticipantDashboard_ValidSession() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("userName")).thenReturn("John Doe");

        when(eventService.getAllEvents()).thenReturn(List.of(event));
        when(participantService.findByUserId(1L)).thenReturn(List.of(participant));
        when(eventService.getEventByEventId(100L)).thenReturn(event);

        String view = participantController.participantDashboard(session, model);

        assertEquals("participant", view);

        verify(model).addAttribute("userName", "John Doe");
        verify(model).addAttribute("userId", 1L);
        verify(model).addAttribute(eq("events"), anyList());
        verify(model).addAttribute(eq("registeredEventDetails"), anyList());
    }

    @Test
    void testParticipantDashboard_NoSession() {
        when(session.getAttribute("userId")).thenReturn(null);

        String view = participantController.participantDashboard(session, model);

        assertEquals("redirect:/login", view);
    }

    // ============================
    // TEST registerForEvent
    // ============================

    @Test
    void testRegisterForEvent_Success() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("role")).thenReturn("PARTICIPANT");
        when(participantService.findByUserIdAndEventId(1L, 100L))
                .thenReturn(Optional.empty());

        String view = participantController.registerForEvent(
                session, 100L, "John Doe", "john@example.com", "9876543210",
                redirectAttributes);

        assertEquals("redirect:/participant/dashboard", view);
        verify(participantService).saveParticipant(any(Participant.class));
    }

    @Test
    void testRegisterForEvent_AlreadyRegistered() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(participantService.findByUserIdAndEventId(1L, 100L))
                .thenReturn(Optional.of(participant));

        String view = participantController.registerForEvent(
                session, 100L, "John Doe", "john@example.com", "9876543210",
                redirectAttributes);

        assertEquals("redirect:/participant/dashboard", view);
        verify(participantService, never()).saveParticipant(any());
    }

    // ============================
    // TEST search /participant/search
    // ============================

    @Test
    void testParticipantDashboardSearch() {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        String speakers = "Muthulakshmi";
        String venue = "Chennai";
        String category = "Tech";

        List<Event> mockEvents = List.of(new Event(), new Event());
        when(eventService.searchEvents(speakers, venue, category)).thenReturn(mockEvents);

        String viewName = participantController.participantDashboard(
                speakers, venue, category, session, model);

        assertEquals("participant", viewName);

        verify(model).addAttribute("events", mockEvents);
        verify(model).addAttribute("speaker", speakers); // FIXED
        verify(model).addAttribute("venue", venue);
        verify(model).addAttribute("category", category);
    }

    // ============================
    // TEST /participant/event/{id}
    // ============================

    @Test
    void testGetParticipantsByEventId() {
        when(participantService.getParticipantsForEvent(100L))
                .thenReturn(List.of(participant));

        List<Participant> participants = participantController.getParticipantsByEventId(100L);

        assertEquals(1, participants.size());
        assertEquals("John Doe", participants.get(0).getParticipantName());
    }

}
