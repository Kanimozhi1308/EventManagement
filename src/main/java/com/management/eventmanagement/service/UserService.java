package com.management.eventmanagement.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.management.eventmanagement.dto.UserLoginDTO;
import com.management.eventmanagement.dto.UserResponseDTO;
import com.management.eventmanagement.dto.UserSignupDTO;
import com.management.eventmanagement.model.User;
import com.management.eventmanagement.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder; // Make sure you have a Bean configured

    @Autowired
    private EmailService emailService;

    /**
     * Registers a new user after validating email uniqueness.
     */
    public UserResponseDTO registerUser(UserSignupDTO dto) {

        // Manual validations

        // Name validation — should not be empty and at least 3 characters
        if (dto.getFullName() == null || dto.getFullName().trim().length() < 3) {
            throw new RuntimeException("Full name must be at least 3 characters long");
        }

        // Email validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (dto.getEmail() == null || !dto.getEmail().matches(emailRegex)) {
            throw new RuntimeException("Invalid email format");
        }

        // Password validation
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long");
        }
        // Check if the email already exists
        Optional<User> existingUser = repo.findByEmail(dto.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Create and save user
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // encrypt password
        user.setMobileNumber(dto.getMobileNumber());
        user.setRole(dto.getRole());
        repo.save(user);

        // Send registration email
        emailService.sendRegistrationEmail(user.getEmail(), user.getFullName());

        // Return safe response
        return new UserResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getRole());
    }

    /**
     * Validates a user's credentials (email and password).
     * <p>
     * This method checks whether a user exists for the given email and verifies if
     * the provided password matches the stored one. If either the email does not
     * exist
     * or the password is incorrect, it throws a {@link RuntimeException}.
     * </p>
     *
     * @param email    the user's email address
     * @param password the user's plain text password
     * @return the {@link User} object if validation is successful
     * @throws RuntimeException if the email or password is invalid
     */
    public User loginUser(UserLoginDTO userLoginDTO) {
        User user = repo.findByEmail(userLoginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid Email or Password"));

        // Check password match
        boolean isPasswordMatch = passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword());
        if (!isPasswordMatch) {
            throw new RuntimeException("Invalid Email or Password");
        }
        return user;
    }

    /**
     * Finds user entity by email
     */
    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    /**
     * Fetches user details by email (used for session setup after login).
     */
    public UserResponseDTO getUserByEmail(String email) {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponseDTO(user.getId(), user.getFullName(), user.getEmail(), user.getMobileNumber(),
                user.getRole());
    }

    /**
     * Retrieves user details by ID.
     */
    public UserResponseDTO getUserById(Long id) {
        User user = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return new UserResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getRole());
    }

    /**
     * Checks if the email already exists in the database.
     */
    public boolean existsByEmail(String email) {
        return repo.existsByEmail(email);
    }

    public UserResponseDTO myProfile(Long id) {
        // Find user by ID
        User user = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Map entity to DTO (only expose safe data)
        return new UserResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getRole());
    }

}
