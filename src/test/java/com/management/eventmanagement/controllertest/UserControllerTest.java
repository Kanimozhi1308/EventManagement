package com.management.eventmanagement.controllertest;

import com.management.eventmanagement.controller.UserController;
import com.management.eventmanagement.dto.UserLoginDTO;
import com.management.eventmanagement.dto.UserResponseDTO;
import com.management.eventmanagement.dto.UserSignupDTO;
import com.management.eventmanagement.model.User;
import com.management.eventmanagement.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserSignupDTO signupDTO;
    private UserResponseDTO userResponseDTO;
    private UserLoginDTO loginDTO;

    @BeforeEach
    void setup() {
        signupDTO = new UserSignupDTO();
        signupDTO.setFullName("John Doe");
        signupDTO.setEmail("john@example.com");
        signupDTO.setPassword("password123");

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setName("John Doe");
        userResponseDTO.setEmail("john@example.com");

        loginDTO = new UserLoginDTO();
        loginDTO.setEmail("john@example.com");
        loginDTO.setPassword("password123");
    }

    // ✅ Test: signUp
    @Test
    void testSignUp() {
        when(userService.registerUser(signupDTO)).thenReturn(userResponseDTO);

        ResponseEntity<UserResponseDTO> response = userController.signUp(signupDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("John Doe", response.getBody().getName());
        verify(userService, times(1)).registerUser(signupDTO);
    }

    // ✅ Test: login
    @Test
    void testLogin() {
        // Create a mock User to return
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFullName("John Doe");
        mockUser.setEmail("john@example.com");

        // Mock the service
        when(userService.loginUser(loginDTO)).thenReturn(mockUser);

        // Call the controller
        ResponseEntity<String> response = userController.login(loginDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Login Successful!", response.getBody());

        verify(userService, times(1)).loginUser(loginDTO);
    }

    // ✅ Test: profile
    @Test
    void testProfile() {
        when(userService.myProfile(1L)).thenReturn(userResponseDTO);

        ResponseEntity<UserResponseDTO> response = userController.profile(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody().getName());
        verify(userService, times(1)).myProfile(1L);
    }
}
