package com.management.eventmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.management.eventmanagement.dto.UserLoginDTO;
import com.management.eventmanagement.dto.UserResponseDTO;
import com.management.eventmanagement.dto.UserSignupDTO;
import com.management.eventmanagement.model.Role;
import com.management.eventmanagement.model.User;
import com.management.eventmanagement.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@CrossOrigin(origins = "*")
public class ViewUserController {

    @Autowired
    private UserService userService;

    // ===== Landing Page with both Signup and Login =====
    @GetMapping("/")
    public String landingPage(Model model) {
        model.addAttribute("signupDTO", new UserSignupDTO());
        model.addAttribute("loginDTO", new UserLoginDTO());
        return "index"; // templates/index.html
    }

    // ===== Login Page =====
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginDTO", new UserLoginDTO());
        return "login"; // templates/login.html
    }

    // ===== Signup Page =====
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("signupDTO", new UserSignupDTO());
        return "signup"; // templates/signup.html
    }

    // ===== Signup Handler =====
    @PostMapping("/signup")
    public String signup(@ModelAttribute("signupDTO") UserSignupDTO signupDTO,
            RedirectAttributes redirectAttributes) {

        if (signupDTO.getFullName() == null || signupDTO.getEmail() == null ||
                signupDTO.getPassword() == null || signupDTO.getMobileNumber() == null ||
                signupDTO.getRole() == null) {

            redirectAttributes.addFlashAttribute("signupError", "All fields are mandatory!");
            return "redirect:/signup";
        }

        if (!signupDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            redirectAttributes.addFlashAttribute("signupError", "Invalid email format!");
            return "redirect:/signup";
        }
        try {
            // 3. Check if email is already registered
            if (userService.existsByEmail(signupDTO.getEmail())) {
                redirectAttributes.addFlashAttribute("signupError", "Email is already registered!");
                return "redirect:/signup";
            }

            userService.registerUser(signupDTO);
            redirectAttributes.addFlashAttribute("signupMessage", "Registration successful! Please login.");
            return "redirect:/login";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("signupError", e.getMessage());
            return "redirect:/signup";
        }
    }

    // ===== Login Handler =====
    @PostMapping("/login")
    public String login(@ModelAttribute("loginDTO") UserLoginDTO loginDTO,
            Model model,
            HttpSession session) {
        try {

            // 🔒 Step 1: Validate credentials using service
            User message = userService.loginUser(loginDTO);

            // Validate login
            UserResponseDTO userDto = userService.getUserByEmail(loginDTO.getEmail());

            // Store user info in session
            session.setAttribute("userId", userDto.getId());
            session.setAttribute("role", userDto.getRole().name());
            session.setAttribute("userName", userDto.getName());

            // Redirect based on role
            if (userDto.getRole() == Role.ADMIN) {
                return "redirect:/admin/dashboard"; // handled by adminController
            } else if (userDto.getRole() == Role.PARTICIPANT) {
                return "redirect:/participant/dashboard"; // handled by ParticipantController
            } else {
                model.addAttribute("loginError", "Invalid user role.");
                return "login";
            }
        } catch (RuntimeException e) {
            model.addAttribute("loginError", e.getMessage());
            return "login";
        }
    }

    // ===== Logout Handler =====
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // clear session
        return "redirect:/login";
    }

}
