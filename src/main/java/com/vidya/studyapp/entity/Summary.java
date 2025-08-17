package com.vidya.studyapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long summaryId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String summaryEn;

    private String nativeLanguage;

    @Column(columnDefinition = "TEXT")
    private String summaryNl;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private StudyMaterial material;
}
