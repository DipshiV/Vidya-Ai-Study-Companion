package com.vidya.studyapp.controller;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.vidya.studyapp.entity.StudyMaterial;
import com.vidya.studyapp.entity.Tag;
import com.vidya.studyapp.entity.User;
import com.vidya.studyapp.repository.StudyMaterialRepository;
import com.vidya.studyapp.repository.TagRepository;
import com.vidya.studyapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/material")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class StudyMaterialController {

    private final StudyMaterialRepository materialRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

 // STUDENT CANNOT UPLOAD NOW
    @PostMapping("/upload")
    public ResponseEntity<?> uploadMaterial(@RequestParam("file") MultipartFile file,
                                            @RequestParam("title") String title,
                                            @RequestParam("tagName") String tagName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        boolean isTeacher = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_TEACHER"));
        if (!isTeacher) {
            return ResponseEntity.status(403).body("Only TEACHER can upload material");
        }

        try {
            Tag tag = tagRepository.findByTagName(tagName).orElseGet(() -> {
                Tag newTag = Tag.builder().tagName(tagName).build();
                return tagRepository.save(newTag);
            });

            String uploadDir = new File("src/main/resources/uploads").getAbsolutePath();
            File directory = new File(uploadDir);
            if (!directory.exists()) directory.mkdirs();

            String fileName = file.getOriginalFilename();
            String filePath = uploadDir + File.separator + fileName;
            file.transferTo(new File(filePath));

            StudyMaterial material = StudyMaterial.builder()
                    .title(title)
                    .fileName(fileName)
                    .uploadedBy(user)
                    .tag(tag)
                    .build();

            materialRepository.save(material);
            return ResponseEntity.ok("Material uploaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    //  Admin - View all materials
    @GetMapping("/all")
    public ResponseEntity<?> getAllMaterials() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(403).body("Access denied: ADMIN only");
        }

        List<StudyMaterial> materials = materialRepository.findAll();
        return ResponseEntity.ok(materials);
    }

    // View own uploaded materials (Teacher or Student)
    @GetMapping("/my-materials")
    public ResponseEntity<?> getMyMaterials() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<StudyMaterial> materials = materialRepository.findByUploadedById(user.getId());
        return ResponseEntity.ok(materials);
    }

    //  Student: View own + all teacher uploads
    @GetMapping("/student/materials")
    public ResponseEntity<?> getMaterialsForStudent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        boolean isStudent = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_STUDENT"));

        if (!isStudent) {
            return ResponseEntity.status(403).body("Access denied: STUDENT role required");
        }

        List<StudyMaterial> allMaterials = materialRepository.findAll();
        List<StudyMaterial> studentViewableMaterials = allMaterials.stream()
                .filter(material ->
                        material.getUploadedBy().getId().equals(student.getId()) ||
                        material.getUploadedBy().getRole().name().equalsIgnoreCase("TEACHER"))
                        .collect(Collectors.toList());

        return ResponseEntity.ok(studentViewableMaterials);
    }

    //  Delete (Uploader or Admin)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMaterial(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        return materialRepository.findById(id).map(material -> {
            if (material.getUploadedBy().getId().equals(currentUser.getId()) || isAdmin) {
                materialRepository.delete(material);
                return ResponseEntity.ok("Material deleted");
            } else {
                return ResponseEntity.status(403).body("Not authorized to delete this material");
            }
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Material not found"));
    }
}
