package com.management.eventmanagement.controllertest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.management.eventmanagement.controller.ViewUserController;
import com.management.eventmanagement.dto.UserLoginDTO;
import com.management.eventmanagement.dto.UserResponseDTO;
import com.management.eventmanagement.dto.UserSignupDTO;
import com.management.eventmanagement.model.Role;
import com.management.eventmanagement.model.User;
import com.management.eventmanagement.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

class ViewUserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private ViewUserController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ============================================================
    // SIGNUP TESTS
    // ============================================================

    @Test
    void testSignup_MissingFields() {
        UserSignupDTO dto = new UserSignupDTO(); // empty fields

        String result = controller.signup(dto, redirectAttributes);

        assertEquals("redirect:/signup", result);
        verify(redirectAttributes).addFlashAttribute(eq("signupError"), anyString());
    }

    @Test
    void testSignup_InvalidEmailFormat() {
        UserSignupDTO dto = new UserSignupDTO();
        dto.setFullName("John");
        dto.setEmail("invalidEmail");
        dto.setPassword("1234");
        dto.setMobileNumber("9876543210");
        dto.setRole(Role.PARTICIPANT);

        String result = controller.signup(dto, redirectAttributes);

        assertEquals("redirect:/signup", result);
        verify(redirectAttributes).addFlashAttribute(eq("signupError"), eq("Invalid email format!"));
    }

    @Test
    void testSignup_EmailAlreadyRegistered() {
        UserSignupDTO dto = new UserSignupDTO();
        dto.setFullName("John");
        dto.setEmail("test@gmail.com");
        dto.setPassword("1234");
        dto.setMobileNumber("9876543210");
        dto.setRole(Role.PARTICIPANT);

        when(userService.existsByEmail("test@gmail.com")).thenReturn(true);

        String result = controller.signup(dto, redirectAttributes);

        assertEquals("redirect:/signup", result);
        verify(redirectAttributes).addFlashAttribute(eq("signupError"), eq("Email is already registered!"));
    }

    @Test
    void testSignup_Success() {
        UserSignupDTO dto = new UserSignupDTO();
        dto.setFullName("John");
        dto.setEmail("john@gmail.com");
        dto.setPassword("1234");
        dto.setMobileNumber("9998887776");
        dto.setRole(Role.PARTICIPANT);

        when(userService.existsByEmail(dto.getEmail())).thenReturn(false);

        String result = controller.signup(dto, redirectAttributes);

        assertEquals("redirect:/login", result);
        verify(userService).registerUser(dto);
        verify(redirectAttributes).addFlashAttribute(eq("signupMessage"), anyString());
    }

    // ============================================================
    // LOGIN TESTS
    // ============================================================

    @Test
    void testLogin_AdminSuccess() {
        UserLoginDTO dto = new UserLoginDTO("admin@gmail.com", "pass");

        User fakeUser = new User();
        fakeUser.setEmail("admin@gmail.com");

        UserResponseDTO response = new UserResponseDTO();
        response.setId(1L);
        response.setName("Admin User");
        response.setRole(Role.ADMIN);

        when(userService.loginUser(dto)).thenReturn(fakeUser);
        when(userService.getUserByEmail(dto.getEmail())).thenReturn(response);

        String result = controller.login(dto, model, session);

        assertEquals("redirect:/admin/dashboard", result);
        verify(session).setAttribute("userId", 1L);
        verify(session).setAttribute("role", "ADMIN");
    }

    @Test
    void testLogin_ParticipantSuccess() {
        UserLoginDTO dto = new UserLoginDTO("user@gmail.com", "pass");

        User fakeUser = new User();
        fakeUser.setEmail("user@gmail.com");

        UserResponseDTO response = new UserResponseDTO();
        response.setId(5L);
        response.setName("User One");
        response.setRole(Role.PARTICIPANT);

        when(userService.loginUser(dto)).thenReturn(fakeUser);
        when(userService.getUserByEmail(dto.getEmail())).thenReturn(response);

        String result = controller.login(dto, model, session);

        assertEquals("redirect:/participant/dashboard", result);
    }

    @Test
    void testLogin_InvalidCredentials() {
        UserLoginDTO dto = new UserLoginDTO("wrong@gmail.com", "wrong");

        when(userService.loginUser(dto)).thenThrow(new RuntimeException("Invalid credentials"));

        String result = controller.login(dto, model, session);

        assertEquals("login", result);
        verify(model).addAttribute(eq("loginError"), eq("Invalid credentials"));
    }

    // ============================================================
    // LOGOUT TEST
    // ============================================================

    @Test
    void testLogout() {
        String result = controller.logout(session);

        verify(session).invalidate();
        assertEquals("redirect:/login", result);
    }
}
