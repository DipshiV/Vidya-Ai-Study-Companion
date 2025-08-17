package com.vidya.studyapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vidya.studyapp.dtos.JwtResponse;
import com.vidya.studyapp.dtos.LoginRequest;
import com.vidya.studyapp.dtos.SignupRequest;
import com.vidya.studyapp.entity.Role;
import com.vidya.studyapp.entity.User;
import com.vidya.studyapp.repository.UserRepository;
import com.vidya.studyapp.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("⚠ Username already exists");
        }

        //  Only allow role STUDENT
        if (!"STUDENT".equalsIgnoreCase(request.getRole())) {
            return ResponseEntity.status(403).body(" Only STUDENT signup allowed");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT)     // forcefully assign role
                .enabled(true)
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("Student registered successfully");
    }

    //  Login and return JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println(" Entered /api/auth/login for user: " + request.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByUsername(request.getUsername())
                                      .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

            System.out.println("Token generated for " + user.getUsername());

            return ResponseEntity.ok(new JwtResponse(token, user.getUsername(), user.getRole().name(), user.getId()));
        } catch (BadCredentialsException ex) {
            System.out.println("Bad credentials for user: " + request.getUsername());
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}
