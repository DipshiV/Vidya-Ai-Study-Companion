package com.vidya.studyapp.controller;

import java.io.File;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.vidya.studyapp.entity.StudyMaterial;
import com.vidya.studyapp.entity.Summary;
import com.vidya.studyapp.entity.User;
import com.vidya.studyapp.repository.StudyMaterialRepository;
import com.vidya.studyapp.repository.SummaryRepository;
import com.vidya.studyapp.repository.UserRepository;
import com.vidya.studyapp.service.OpenAIService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/summary")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryRepository summaryRepository;
    private final UserRepository userRepository;
    private final StudyMaterialRepository materialRepository;
    private final OpenAIService openAIService;

    //  All materials viewable by student (self + teacher uploaded)
    @GetMapping("/student/viewable-materials")
    public ResponseEntity<?> getViewableMaterialsForStudent(Principal principal) {
        String username = principal.getName();

        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<StudyMaterial> allMaterials = materialRepository.findAll();

        List<StudyMaterial> viewable = allMaterials.stream()
                .filter(material ->
                        material.getUploadedBy().getId().equals(student.getId()) ||  // own uploads
                        material.getUploadedBy().getRole().name().equalsIgnoreCase("TEACHER")  // from teachers
                )
                .collect(Collectors.toList());

        return ResponseEntity.ok(viewable);
    }

    //  Get summaries submitted by student
    @GetMapping("/student/summaries")
    public ResponseEntity<List<Summary>> getMySummaries(Principal principal) {
        String username = principal.getName();

        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return ResponseEntity.ok(summaryRepository.findByStudentId(student.getId()));
    }

    //  Generate summary from selected material
    @PostMapping("/generate")
    public ResponseEntity<?> generateSummary(@RequestParam Long materialId,
                                             @RequestParam String langCode,
                                             Principal principal) {
        String username = principal.getName();

        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Optional<StudyMaterial> materialOpt = materialRepository.findById(materialId);
        if (materialOpt.isEmpty()) return ResponseEntity.badRequest().body("Invalid material ID");

        StudyMaterial material = materialOpt.get();

        //  Check if this student is allowed to access this material
        if (!(material.getUploadedBy().getId().equals(student.getId()) ||
                material.getUploadedBy().getRole().name().equalsIgnoreCase("TEACHER"))) {
            return ResponseEntity.status(403).body("You are not authorized to summarize this material.");
        }

        try {
            //  Read PDF file
            String filePath = "src/main/resources/uploads/" + material.getFileName();
            File file = new File(filePath);
            if (!file.exists()) return ResponseEntity.badRequest().body("PDF file not found");

            PDDocument document = PDDocument.load(file);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();

            //  Generate summary and translate
            String englishSummary = openAIService.summarizeText(text);
            String translatedSummary = openAIService.translateWithHF(englishSummary, langCode);

            //  Save summary
            Summary summary = Summary.builder()
                    .title(material.getTitle())
                    .summaryEn(englishSummary)
                    .summaryNl(translatedSummary)
                    .nativeLanguage(langCode)
                    .student(student)
                    .material(material)
                    .build();

            summaryRepository.save(summary);

            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Summary generation failed: " + e.getMessage());
        }
    }

    //  Translate existing summary
    @PostMapping("/translate")
    public ResponseEntity<?> translateExistingSummary(@RequestParam Long summaryId,
                                                      @RequestParam String langCode) {
        Optional<Summary> summaryOpt = summaryRepository.findById(summaryId);
        if (summaryOpt.isEmpty()) return ResponseEntity.status(404).body("Summary not found");

        Summary summary = summaryOpt.get();
        String translated = openAIService.translateWithHF(summary.getSummaryEn(), langCode);

        summary.setSummaryNl(translated);
        summary.setNativeLanguage(langCode);
        summaryRepository.save(summary);

        return ResponseEntity.ok(summary);
    }

    //  Get one summary for student + material combo
    @GetMapping("/student/{studentId}/material/{materialId}")
    public ResponseEntity<?> getSummaryForStudent(@PathVariable Long studentId,
                                                  @PathVariable Long materialId) {
        Optional<Summary> summary = summaryRepository.findByStudentIdAndMaterialId(studentId, materialId);
        return summary.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("Summary not found."));
    }

    //  Get all summaries by a student (admin/teacher use)
    @GetMapping("/student/{studentId}/all")
    public List<Summary> getAllSummariesForStudent(@PathVariable Long studentId) {
        return summaryRepository.findByStudentId(studentId);
    }

    //  Delete a summary
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSummary(@PathVariable Long id) {
        return summaryRepository.findById(id)
                .map(summary -> {
                    summaryRepository.delete(summary);
                    return ResponseEntity.ok("Summary deleted successfully.");
                })
                .orElse(ResponseEntity.status(404).body("Summary not found."));
    }
    
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadSummary(@PathVariable Long id) {
        Optional<Summary> summaryOpt = summaryRepository.findById(id);
        if (summaryOpt.isEmpty()) return ResponseEntity.status(404).body("Summary not found");

        Summary summary = summaryOpt.get();
        String filename = "summary_" + summary.getSummaryId() + ".txt";
        String content = "Title: " + summary.getTitle() + "\n\n"
                + "Summary (English):\n" + summary.getSummaryEn() + "\n\n"
                + "Summary (Native - " + summary.getNativeLanguage() + "):\n" + summary.getSummaryNl();

        try {
            File file = new File("src/main/resources/downloads");
            if (!file.exists()) file.mkdirs();
            File summaryFile = new File(file, filename);
            java.nio.file.Files.write(summaryFile.toPath(), content.getBytes());

            return ResponseEntity.ok(summaryFile.getName());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Download failed: " + e.getMessage());
        }
    }

}
