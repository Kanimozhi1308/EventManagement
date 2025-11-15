package com.management.eventmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.management.eventmanagement.dto.UserLoginDTO;
import com.management.eventmanagement.dto.UserResponseDTO;
import com.management.eventmanagement.dto.UserSignupDTO;
import com.management.eventmanagement.service.UserService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Registers a new user.
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signUp(@RequestBody UserSignupDTO signupDTO) {
        UserResponseDTO userResponse = userService.registerUser(signupDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    /**
     * Validates user login credentials.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDTO loginDTO) {
        userService.loginUser(loginDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Login Successful!");
    }

    @GetMapping("/my-profile/{id}")
    public ResponseEntity<UserResponseDTO> profile(@PathVariable Long id) {
        UserResponseDTO userprofile = userService.myProfile(id);
        return ResponseEntity.ok().body(userprofile);

    }
}