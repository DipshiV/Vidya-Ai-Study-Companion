package com.vidya.studyapp.controller;

import com.vidya.studyapp.entity.Feedback;
import com.vidya.studyapp.entity.Role;
import com.vidya.studyapp.entity.User;
import com.vidya.studyapp.repository.FeedbackRepository;
import com.vidya.studyapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    //  Submit Feedback (Teacher & Student)
    @PostMapping("/submit")
    public ResponseEntity<String> submitFeedback(@RequestBody Feedback feedback, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(404).body("User not found");

        feedback.setUser(user);
        feedbackRepository.save(feedback);
        return ResponseEntity.ok("Feedback submitted successfully by " + user.getRole());
    }

    // View My Feedback
    @GetMapping("/my")
    public ResponseEntity<List<Feedback>> getMyFeedbacks(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(404).build();

        return ResponseEntity.ok(feedbackRepository.findByUserId(user.getId()));
    }

    //  View All Feedbacks (Admin Only)
    @GetMapping("/all")
    public ResponseEntity<?> getAllFeedbacks(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) return ResponseEntity.status(403).body("Only ADMIN can view all feedbacks");

        return ResponseEntity.ok(feedbackRepository.findAll());
    }

    //  View All Students' Feedbacks (Teacher Only)
    @GetMapping("/students")
    public ResponseEntity<?> getAllStudentFeedbacks(Authentication auth) {
        boolean isTeacher = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_TEACHER"));

        if (!isTeacher) return ResponseEntity.status(403).body("Only TEACHER can view student feedbacks");

        // Get all students
        List<User> students = userRepository.findByRole(Role.STUDENT);
        List<Long> studentIds = students.stream().map(User::getId).toList();

        List<Feedback> studentFeedbacks = feedbackRepository.findByUserIdIn(studentIds);
        return ResponseEntity.ok(studentFeedbacks);
    }

    //  Delete Feedback (self or admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFeedback(@PathVariable Long id, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(404).body("User not found");

        Feedback feedback = feedbackRepository.findById(id).orElse(null);
        if (feedback == null) return ResponseEntity.status(404).body("Feedback not found");

        boolean isOwner = feedback.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole().equals(Role.ADMIN);

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).body("You are not authorized to delete this feedback");
        }

        feedbackRepository.deleteById(id);
        return ResponseEntity.ok("Feedback deleted successfully.");
    }

    //  Optional: Get feedback by userId (Admin only)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Feedback>> getFeedbacksByUser(@PathVariable Long userId, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) return ResponseEntity.status(403).build();

        return ResponseEntity.ok(feedbackRepository.findByUserId(userId));
    }
}
