package com.management.eventmanagement.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.management.eventmanagement.model.Event;
import com.management.eventmanagement.model.Participant;
import com.management.eventmanagement.model.User;
import com.management.eventmanagement.service.EventService;
import com.management.eventmanagement.service.ParticipantService;
import com.management.eventmanagement.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/participant")
public class ViewParticipantController {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    // =================== REDIRECT TO DASHBOARD ===================
    @GetMapping
    public String redirectToDashboard(@RequestParam String email, HttpSession session) {
        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            return "redirect:/login";
        }

        session.setAttribute("userId", user.get().getId());
        session.setAttribute("userName", user.get().getFullName());
        session.setAttribute("userEmail", user.get().getEmail());
        session.setAttribute("userPhone", user.get().getMobileNumber());

        return "redirect:/participant/dashboard";
    }

    // =================== DASHBOARD ===================
    @GetMapping("/dashboard")
    public String participantDashboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        String userName = (String) session.getAttribute("userName");
        String role = (String) session.getAttribute("role");

        if (userId == null) {
            return "redirect:/login";
        }

        // Fetch all available events
        List<Event> events = eventService.getAllEvents();

        // Fetch events the user has registered for
        List<Participant> registrations = participantService.findByUserId(userId);

        // Combine registered events with their event details
        List<Map<String, Object>> registeredEventDetails = new ArrayList<>();
        for (Participant reg : registrations) {
            Event event = eventService.getEventByEventId(reg.getEventId());
            if (event != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("event", event);
                map.put("registration", reg);
                registeredEventDetails.add(map);
            }
        }

        model.addAttribute("userName", userName);
        model.addAttribute("userId", userId);
        model.addAttribute("events", events);
        model.addAttribute("registeredEventDetails", registeredEventDetails);

        return "participant";
    }

    // =================== REGISTER FOR EVENT ===================
    @PostMapping("/register")
    public String registerForEvent(
            HttpSession session,
            @RequestParam Long eventId,
            @RequestParam String participantName,
            @RequestParam String participantEmail,
            @RequestParam String participantPhone,
            RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please log in to register for an event.");
            return "redirect:/login";
        }

        if ("ADMIN".equals(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Admin cannot register for events!");
            return "redirect:/admin/dashboard";
        }

        // Prevent duplicate registration
        Optional<Participant> existing = participantService.findByUserIdAndEventId(userId, eventId);
        if (existing.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "You have already registered for this event!");
            return "redirect:/participant/dashboard";
        }

        participantService.saveParticipant(
                new Participant(null, eventId, userId, participantName, participantEmail, participantPhone,
                        LocalDateTime.now()));

        redirectAttributes.addFlashAttribute("successMessage", "You have successfully registered for the event!");
        return "redirect:/participant/dashboard";
    }

    // This method returns all participants
    @GetMapping("/all")
    public List<Participant> getAllParticipants() {
        return participantService.getAllParticipants();
    }

    // Get participants by event ID
    @GetMapping("/event/{eventId}")
    public List<Participant> getParticipantsByEventId(@PathVariable Long eventId) {
        return participantService.getParticipantsForEvent(eventId);
    }

    @GetMapping("/search")
    public String participantDashboard(
            @RequestParam(required = false) String speakers,
            @RequestParam(required = false) String venue,
            @RequestParam(required = false) String category,
            HttpSession session,
            Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return "redirect:/login";

        List<Event> events = eventService.searchEvents(speakers, venue, category);

        model.addAttribute("events", events);
        model.addAttribute("speaker", speakers);
        model.addAttribute("venue", venue);
        model.addAttribute("category", category);

        return "participant";
    }

}
