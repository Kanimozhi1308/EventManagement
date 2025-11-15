package com.management.eventmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.management.eventmanagement.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

        // @Query("SELECT e FROM Event e WHERE " +
        // "(:date IS NULL OR e.date = :date) AND " +
        // "(:venue IS NULL OR LOWER(e.venue) LIKE LOWER(CONCAT('%', :venue, '%'))) AND
        // " +
        // "(:category IS NULL OR LOWER(e.category) LIKE LOWER(CONCAT('%', :category,
        // '%')))")
        // List<Event> findByFilters(@Param("date") LocalDate date,
        // @Param("venue") String venue,
        // @Param("category") String category);
        // Combined search with multiple optional filters
        // List<Event> findByDate(LocalDate date);

        // Search by speaker name (case-insensitive, partial match)
        List<Event> findBySpeakersContainingIgnoreCase(String speakers);

        List<Event> findByVenueContainingIgnoreCase(String venue);

        List<Event> findByCategoryContainingIgnoreCase(String category);

        List<Event> findByAdminId(Long adminId);
}
