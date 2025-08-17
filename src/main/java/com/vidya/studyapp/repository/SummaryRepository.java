package com.vidya.studyapp.repository;

import com.vidya.studyapp.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
    List<Summary> findByStudentId(Long studentId);
    List<Summary> findByMaterialId(Long materialId);
    Optional<Summary> findByStudentIdAndMaterialId(Long studentId, Long materialId);

}
