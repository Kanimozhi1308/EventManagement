package com.management.eventmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.management.eventmanagement.model.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    // ✅ Correct methods based on your entity fields
    List<Participant> findByEventId(Long eventId);

    List<Participant> findByUserId(Long userId);

    Optional<Participant> findByUserIdAndEventId(Long userId, Long eventId);

}
