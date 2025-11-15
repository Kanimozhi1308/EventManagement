package com.management.eventmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.management.eventmanagement.model.Event;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;// same as in properties

    /**
     * Sends a registration success email for Event Management app.
     */
    public void sendRegistrationEmail(String toEmail, String userName) {
        String subject = "Welcome to Event Management App!";
        String body = "Hi " + userName + ",\n\n"
                + "🎉 Your account has been successfully created!\n"
                + "You can now log in and explore upcoming events.\n\n"
                + "Best regards,\nEvent Management Team";

        sendEmail(toEmail, subject, body);
    }

    // NEW EVENT MAIL (ASYNC – DOES NOT BLOCK UI)
    @Async
    public void sendEventNotificationEmail(String toEmail, String participantName, Event event) {
        if (toEmail == null || participantName == null) {
            System.out.println("Skipping email because participant data is missing");
            return;
        }
        String subject = "New Event: " + event.getTitle(); // Email subject
        String body = "Hi " + participantName + ",\n\n"
                + "🎉 A new event has been posted in Event Management App!\n\n"
                + "Event Details:\n"
                + "Title: " + event.getTitle() + "\n"
                + "Date: " + event.getDate() + "\n"
                + "Venue: " + event.getVenue() + "\n\n"
                + "Don't miss it!\n"
                + "Best regards,\nEvent Management Team";

        sendEmail(toEmail, subject, body); // Calls your existing sendEmail method
    }

    /**
     * Generic method to send an email.
     */
    private void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Email sending failed: " + e.getMessage());
        }

    }
}