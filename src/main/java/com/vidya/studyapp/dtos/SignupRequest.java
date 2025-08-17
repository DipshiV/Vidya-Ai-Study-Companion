package com.vidya.studyapp.dtos;

import lombok.Data;

@Data
public class SignupRequest {
    private String name;
    private String email;
    private String username;
    private String password;
    private String role;  // "ADMIN", "TEACHER", or "STUDENT"
}

