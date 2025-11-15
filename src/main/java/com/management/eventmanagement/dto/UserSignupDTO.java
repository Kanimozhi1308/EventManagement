package com.management.eventmanagement.dto;

import com.management.eventmanagement.model.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupDTO {

    private String fullName;
    private String email;
    private String password;
    private String mobileNumber;
    private Role role;

}
