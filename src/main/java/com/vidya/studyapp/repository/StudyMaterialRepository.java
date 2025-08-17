package com.vidya.studyapp.repository;

import com.vidya.studyapp.entity.StudyMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {
    List<StudyMaterial> findByUploadedById(Long teacherId);
}
