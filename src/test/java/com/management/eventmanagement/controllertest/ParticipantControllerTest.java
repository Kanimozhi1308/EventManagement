package com.management.eventmanagement.controllertest;

import com.management.eventmanagement.controller.ParticipantController;
import com.management.eventmanagement.model.Participant;
import com.management.eventmanagement.model.User;
import com.management.eventmanagement.service.ParticipantService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ParticipantControllerTest {

    @Mock
    private ParticipantService participantService;

    @InjectMocks
    private ParticipantController participantController;

    private Participant participant;
    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);

        participant = new Participant();
        participant.setId(1L);
        participant.setParticipantName("John Doe");
        participant.setParticipantEmail("john@example.com");
        participant.setUserId(null);
        participant.setRegisteredDate(LocalDateTime.now());
    }

    // ✅ Test: registerParticipant
    @Test
    void testRegisterParticipant() {
        when(participantService.saveParticipant(any(Participant.class))).thenReturn(participant);

        ResponseEntity<Participant> response = participantController.registerParticipant(participant);

        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getParticipantName());
        verify(participantService, times(1)).saveParticipant(participant);
    }

    // ✅ Test: getMyRegistrations
    @Test
    void testGetMyRegistrations() {
        when(participantService.getRegistrationsByUser(any(User.class)))
                .thenReturn(List.of(participant));

        List<Participant> registrations = participantController.getMyRegistrations(1L);

        assertNotNull(registrations);
        assertEquals(1, registrations.size());
        assertEquals("John Doe", registrations.get(0).getParticipantName());
        verify(participantService, times(1)).getRegistrationsByUser(any(User.class));
    }

    // ✅ Test: getAllParticipants
    @Test
    void testGetAllParticipants() {
        when(participantService.getAllParticipants()).thenReturn(List.of(participant));

        ResponseEntity<List<Participant>> response = participantController.getAllParticipants();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getParticipantName());
        verify(participantService, times(1)).getAllParticipants();
    }

    // ✅ Test: getParticipantById
    @Test
    void testGetParticipantById() {
        when(participantService.getParticipantById(1L)).thenReturn(participant);

        ResponseEntity<Participant> response = participantController.getParticipantById(1L);

        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getParticipantName());
        verify(participantService, times(1)).getParticipantById(1L);
    }

    // ✅ Test: getParticipantsByEvent
    @Test
    void testGetParticipantsByEvent() {
        when(participantService.getParticipantsByEvent(100L)).thenReturn(List.of(participant));

        ResponseEntity<List<Participant>> response = participantController.getParticipantsByEvent(100L);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getParticipantName());
        verify(participantService, times(1)).getParticipantsByEvent(100L);
    }
}
