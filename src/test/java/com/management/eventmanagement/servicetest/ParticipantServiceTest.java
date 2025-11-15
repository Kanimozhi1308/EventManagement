package com.management.eventmanagement.servicetest;

import com.management.eventmanagement.model.Participant;
import com.management.eventmanagement.model.User;
import com.management.eventmanagement.repository.ParticipantRepository;
import com.management.eventmanagement.service.ParticipantService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ParticipantService participantService;

    private Participant participant;
    private User user;

    @BeforeEach
    void setup() {
        participant = new Participant();
        participant.setId(1L);
        participant.setEventId(100L);
        participant.setUserId(10L);
        participant.setParticipantName("John Doe");
        participant.setParticipantEmail("john@example.com");
        participant.setParticipantPhone("1234567890");
        participant.setRegisteredDate(LocalDateTime.now());

        user = new User();
        user.setId(10L);
    }

    @Test
    void testSaveParticipant() {
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        Participant saved = participantService.saveParticipant(participant);

        assertNotNull(saved);
        assertEquals(participant.getParticipantEmail(), saved.getParticipantEmail());
        verify(participantRepository, times(1)).save(participant);
    }

    @Test
    void testGetAllParticipants() {
        when(participantRepository.findAll()).thenReturn(List.of(participant));

        List<Participant> participants = participantService.getAllParticipants();

        assertEquals(1, participants.size());
        assertEquals("John Doe", participants.get(0).getParticipantName());
    }

    @Test
    void testGetParticipantById_Found() {
        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));

        Participant found = participantService.getParticipantById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void testGetParticipantById_NotFound() {
        when(participantRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            participantService.getParticipantById(2L);
        });

        assertTrue(exception.getMessage().contains("Participant not found"));
    }

    @Test
    void testGetParticipantsForEvent() {
        when(participantRepository.findByEventId(100L)).thenReturn(List.of(participant));

        List<Participant> participants = participantService.getParticipantsForEvent(100L);

        assertEquals(1, participants.size());
        assertEquals(100L, participants.get(0).getEventId());
    }

    @Test
    void testGetRegistrationsByUser() {
        when(participantRepository.findByUserId(10L)).thenReturn(List.of(participant));

        List<Participant> registrations = participantService.getRegistrationsByUser(user);

        assertEquals(1, registrations.size());
        assertEquals(10L, registrations.get(0).getUserId());
    }

    @Test
    void testFindByUserIdAndEventId() {
        when(participantRepository.findByUserIdAndEventId(10L, 100L)).thenReturn(Optional.of(participant));

        Optional<Participant> existing = participantService.findByUserIdAndEventId(10L, 100L);

        assertTrue(existing.isPresent());
        assertEquals("John Doe", existing.get().getParticipantName());
    }

    @Test
    void testGetAttendees() {
        when(participantRepository.findByEventId(100L)).thenReturn(List.of(participant));

        List<Participant> attendees = participantService.getAttendees(100L);

        assertEquals(1, attendees.size());
        assertEquals("John Doe", attendees.get(0).getParticipantName());
    }
}
