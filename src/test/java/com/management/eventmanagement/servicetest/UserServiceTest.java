package com.management.eventmanagement.servicetest;

import com.management.eventmanagement.dto.UserLoginDTO;
import com.management.eventmanagement.dto.UserResponseDTO;
import com.management.eventmanagement.dto.UserSignupDTO;
import com.management.eventmanagement.model.Role;
import com.management.eventmanagement.model.User;
import com.management.eventmanagement.repository.UserRepository;
import com.management.eventmanagement.service.EmailService;
import com.management.eventmanagement.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    private UserSignupDTO signupDTO;
    private UserLoginDTO loginDTO;
    private User user;

    @BeforeEach
    void setup() {
        signupDTO = new UserSignupDTO();
        signupDTO.setFullName("John Doe");
        signupDTO.setEmail("john@example.com");
        signupDTO.setPassword("password123");
        signupDTO.setMobileNumber("1234567890");
        signupDTO.setRole(Role.PARTICIPANT);

        loginDTO = new UserLoginDTO();
        loginDTO.setEmail("john@example.com");
        loginDTO.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setFullName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setMobileNumber("1234567890");
        user.setRole(Role.PARTICIPANT);
    }

    @Test
    void testRegisterUser_Success() {
        when(repo.findByEmail(signupDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(signupDTO.getPassword())).thenReturn("encodedPassword");
        when(repo.save(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.registerUser(signupDTO);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        verify(emailService, times(1)).sendRegistrationEmail(user.getEmail(), user.getFullName());
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        when(repo.findByEmail(signupDTO.getEmail())).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(signupDTO);
        });

        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    void testLoginUser_Success() {
        when(repo.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(true);

        User loggedIn = userService.loginUser(loginDTO);

        assertNotNull(loggedIn);
        assertEquals(user.getEmail(), loggedIn.getEmail());
    }

    @Test
    void testLoginUser_InvalidPassword() {
        when(repo.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.loginUser(loginDTO);
        });

        assertEquals("Invalid Email or Password", exception.getMessage());
    }

    @Test
    void testLoginUser_EmailNotFound() {
        when(repo.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.loginUser(loginDTO);
        });

        assertEquals("Invalid Email or Password", exception.getMessage());
    }

    @Test
    void testMyProfile() {
        when(repo.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO profile = userService.myProfile(1L);

        assertNotNull(profile);
        assertEquals("John Doe", profile.getName());
    }

    @Test
    void testGetUserByEmail() {
        when(repo.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        UserResponseDTO response = userService.getUserByEmail("john@example.com");

        assertEquals("John Doe", response.getName());
    }

    @Test
    void testGetUserById() {
        when(repo.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO response = userService.getUserById(1L);

        assertEquals("John Doe", response.getName());
    }

    @Test
    void testExistsByEmail() {
        when(repo.existsByEmail("john@example.com")).thenReturn(true);

        assertTrue(userService.existsByEmail("john@example.com"));
    }
}
