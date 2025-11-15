package com.management.eventmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.management.eventmanagement.model.Event;
import com.management.eventmanagement.model.Participant;
import com.management.eventmanagement.service.EventService;
import com.management.eventmanagement.service.ParticipantService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class ViewAdminController {

    @Autowired
    private EventService eventService;

    @Autowired
    private ParticipantService participantService;

    // =================== ADMIN-SPECIFIC DASHBOARD ===================
    @GetMapping("/dashboard")
    public String myAdminDashboard(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");
        String userName = (String) session.getAttribute("userName");

        if (userId == null) {
            return "redirect:/login";
        }

        List<Event> events = eventService.getEventsByAdminId(userId);

        List<Participant> allParticipants = participantService.getAllParticipants();

        model.addAttribute("userName", userName);
        model.addAttribute("adminId", userId);
        model.addAttribute("events", events);
        model.addAttribute("allParticipants", allParticipants);
        model.addAttribute("event", new Event());

        return "admin";
    }

    // =================== CREATE EVENT ===================
    @PostMapping("/add")
    public String addEvent(@ModelAttribute Event event) {
        eventService.createEvent(event);
        return "redirect:/admin/dashboard";
    }

    // =================== UPDATE EVENT ===================
    @PostMapping("/edit/{id}")
    public String updateEvent(@PathVariable("id") Long eventId, @ModelAttribute Event updatedEvent, Model model) {
        try {
            eventService.updateEvent(eventId, updatedEvent);
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    // =================== DELETE EVENT ===================
    @GetMapping("/delete/{eventId}")
    public String deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return "redirect:/admin/dashboard";
    }

    // =================== SEARCH EVENTS ===================
    @GetMapping("/search")
    public String searchEvents(
            @RequestParam(required = false) String speakers,
            @RequestParam(required = false) String venue,
            @RequestParam(required = false) String category,
            Model model) {

        // Search using optional filters
        List<Event> events = eventService.searchEvents(speakers, venue, category);
        model.addAttribute("events", events);
        model.addAttribute("speakers", speakers);
        model.addAttribute("venue", venue);
        model.addAttribute("category", category);

        return "admin"; // return back to admin.html
    }

    // =================== VIEW ALL PARTICIPANTS ===================
    @GetMapping("/all")
    public String viewAllParticipants(Model model) {
        List<Participant> participants = participantService.getAllParticipants();
        model.addAttribute("allParticipants", participants);
        return "admin";
    }

    @GetMapping("/event/{eventId}/attendees")
    @ResponseBody
    public List<Participant> viewAttendees(@PathVariable Long eventId) {
        return participantService.getAttendees(eventId);
    }

}
