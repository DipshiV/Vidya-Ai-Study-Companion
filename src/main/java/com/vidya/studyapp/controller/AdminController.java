package com.vidya.studyapp.controller;

import com.vidya.studyapp.entity.Role;
import com.vidya.studyapp.entity.User;
import com.vidya.studyapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;  // Inject encoder

 // Add teacher (already included earlier)
    @PostMapping("/add-teacher")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addTeacher(@RequestBody User user) {
        if (userRepo.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("⚠ Username already exists");
        }

        user.setRole(Role.TEACHER);
        user.setEnabled(true);  // Active by default
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);

        return ResponseEntity.ok("Teacher created successfully");
    }

    //  Get all teachers
    @GetMapping("/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllTeachers() {
        List<User> teachers = userRepo.findByRole(Role.TEACHER);
        return ResponseEntity.ok(teachers);
    }

    // Delete a teacher by ID
    @DeleteMapping("/delete-teacher/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteTeacher(@PathVariable Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().equals(Role.TEACHER)) {
            return ResponseEntity.badRequest().body("User is not a teacher.");
        }

        userRepo.deleteById(id);
        return ResponseEntity.ok(" Teacher deleted successfully");
    }
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepo.findAll());
    }

    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        if (!userRepo.existsById(id)) {
            return ResponseEntity.status(404).body("User not found");
        }
        userRepo.deleteById(id);
        return ResponseEntity.ok("User deleted successfully.");
    }

    @PutMapping("/user/{id}/change-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changeUserPassword(@PathVariable Long id, @RequestParam String newPassword) {
        return userRepo.findById(id)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepo.save(user);
                    return ResponseEntity.ok("Password updated successfully.");
                })
                .orElse(ResponseEntity.status(404).body("User not found"));
    }

}
