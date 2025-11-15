package com.management.eventmanagement.repositorytest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.management.eventmanagement.model.Participant;
import com.management.eventmanagement.repository.ParticipantRepository;

@ExtendWith(MockitoExtension.class) // ✅ Enable Mockito annotations
class ParticipantRepositoryTest {

    @Mock
    private ParticipantRepository participantRepository;

    // Optional: If not using @ExtendWith, you can initialize mocks manually
    // @BeforeEach
    // void setUp() {
    // MockitoAnnotations.openMocks(this);
    // }

    @Test
    void testFindByEventId() {
        Participant p1 = new Participant();
        p1.setEventId(1L);

        when(participantRepository.findByEventId(1L)).thenReturn(Arrays.asList(p1));

        List<Participant> participants = participantRepository.findByEventId(1L);

        assertEquals(1, participants.size());
        assertEquals(1L, participants.get(0).getEventId());
    }

    @Test
    void testFindByUserIdAndEventId() {
        Participant p = new Participant();
        p.setEventId(1L);
        p.setUserId(2L);

        when(participantRepository.findByUserIdAndEventId(2L, 1L)).thenReturn(Optional.of(p));

        Optional<Participant> existing = participantRepository.findByUserIdAndEventId(2L, 1L);

        assertTrue(existing.isPresent());
        assertEquals(2L, existing.get().getUserId());
        assertEquals(1L, existing.get().getEventId());
    }
}
