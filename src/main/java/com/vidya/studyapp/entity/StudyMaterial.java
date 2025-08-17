package com.vidya.studyapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String fileName;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User uploadedBy;
    
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

}
