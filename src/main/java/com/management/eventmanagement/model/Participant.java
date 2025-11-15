package com.management.eventmanagement.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "participants")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private Long eventId; // ✅ Just the job ID (no foreign key)

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "participant_name")
    private String participantName;

    @Column(name = "participant_email")
    private String participantEmail;

    @Column(name = "participant_phone")
    private String participantPhone;

    private LocalDateTime registeredDate;

}
