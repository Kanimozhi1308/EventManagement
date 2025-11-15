package com.management.eventmanagement.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.management.eventmanagement.model.Participant;
import com.management.eventmanagement.model.User;
import com.management.eventmanagement.repository.ParticipantRepository;

import jakarta.transaction.Transactional;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    // Get participants for event
    public List<Participant> getParticipantsForEvent(Long eventId) {
        return participantRepository.findByEventId(eventId);
    }

    // Save a participant directly (used for /register endpoint)
    @Transactional
    public Participant saveParticipant(Participant participant) {
        participant.setRegisteredDate(LocalDateTime.now());
        return participantRepository.save(participant);
    }

    // Get all events a user registered for
    public List<Participant> getRegistrationsByUser(User user) {
        return participantRepository.findByUserId(user.getId());
    }

    // Get all participants
    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    // Get participant by ID
    public Participant getParticipantById(Long id) {
        return participantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participant not found with id: " + id));
    }

    // Get participants by Event ID
    public List<Participant> getParticipantsByEvent(Long eventId) {
        return participantRepository.findByEventId(eventId);
    }

    /**
     * Returns all participant registrations made by a specific user.
     *
     * @param userId the ID of the user
     * @return list of participant records for that user
     */
    public List<Participant> findByUserId(Long userId) {
        return participantRepository.findByUserId(userId);
    }

    /**
     * Finds a specific participant registration by both user ID and event ID.
     * Useful for checking if a user is already registered for an event.
     *
     * @param userId  the ID of the user
     * @param eventId the ID of the event
     * @return optional containing participant details if registration exists
     */
    public Optional<Participant> findByUserIdAndEventId(Long userId, Long eventId) {
        return participantRepository.findByUserIdAndEventId(userId, eventId);
    }

    /**
     * Returns all attendees registered for a specific event.
     *
     * @param eventId the ID of the event
     * @return list of participants registered for the event
     */
    public List<Participant> getAttendees(Long eventId) {
        return participantRepository.findByEventId(eventId);
    }

}
