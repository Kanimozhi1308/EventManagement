package com.management.eventmanagement.repositorytest;

import com.management.eventmanagement.model.Role;
import com.management.eventmanagement.model.User;
import com.management.eventmanagement.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        user1 = new User();
        user1.setId(1L);
        user1.setFullName("Admin User");
        user1.setEmail("admin@example.com");
        user1.setRole(Role.ADMIN);

        user2 = new User();
        user2.setId(2L);
        user2.setFullName("Participant User");
        user2.setEmail("participant@example.com");
        user2.setRole(Role.PARTICIPANT);
    }

    @Test
    void testFindByEmail() {
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user1));

        Optional<User> found = userRepository.findByEmail("admin@example.com");

        assertTrue(found.isPresent());
        assertEquals("Admin User", found.get().getFullName());
    }

    @Test
    void testExistsByEmail() {
        when(userRepository.existsByEmail("participant@example.com")).thenReturn(true);

        boolean exists = userRepository.existsByEmail("participant@example.com");

        assertTrue(exists);
    }

    @Test
    void testFindByRole() {
        when(userRepository.findByRole(Role.ADMIN)).thenReturn(Arrays.asList(user1));

        List<User> admins = userRepository.findByRole(Role.ADMIN);

        assertEquals(1, admins.size());
        assertEquals(Role.ADMIN, admins.get(0).getRole());
    }
}
