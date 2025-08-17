package com.vidya.studyapp.controller;

import com.vidya.studyapp.entity.Tag;
import com.vidya.studyapp.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class TagController {

    private final TagRepository tagRepository;

//    @PostMapping("/add")
//    public ResponseEntity<String> addTag(@RequestParam String name) {
//        Tag tag = Tag.builder().tagName(name).build();
//        tagRepository.save(tag);
//        return ResponseEntity.ok("Tag added successfully.");
//    }

    @GetMapping("/all")
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTag(@PathVariable Long id) {
        tagRepository.deleteById(id);
        return ResponseEntity.ok("Tag deleted.");
    }
}
