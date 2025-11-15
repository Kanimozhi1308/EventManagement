package com.management.eventmanagement.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.management.eventmanagement.model.Participant;
import com.management.eventmanagement.model.User;
import com.management.eventmanagement.service.ParticipantService;

@RestController
@RequestMapping("/api/participant")
@CrossOrigin(origins = "*")
public class ParticipantController {

    @Autowired
    private ParticipantService participantService;

    // Register a new participant
    @PostMapping("/register")
    public ResponseEntity<Participant> registerParticipant(@RequestBody Participant participant) {
        participant.setRegisteredDate(LocalDateTime.now());
        Participant saved = participantService.saveParticipant(participant);
        return ResponseEntity.ok(saved);
    }

    // Get all events a participant registered for
    @GetMapping("/my-registrations/{userId}")
    public List<Participant> getMyRegistrations(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return participantService.getRegistrationsByUser(user);
    }

    // Fetch all participants
    @GetMapping
    public ResponseEntity<List<Participant>> getAllParticipants() {
        List<Participant> participants = participantService.getAllParticipants();
        return ResponseEntity.ok(participants);
    }

    // Fetch participant by ID
    @GetMapping("/{id}")
    public ResponseEntity<Participant> getParticipantById(@PathVariable Long id) {
        Participant participant = participantService.getParticipantById(id);
        return ResponseEntity.ok(participant);
    }

    // Fetch participants by Event ID
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Participant>> getParticipantsByEvent(@PathVariable Long eventId) {
        List<Participant> participants = participantService.getParticipantsByEvent(eventId);
        return ResponseEntity.ok(participants);
    }

}
