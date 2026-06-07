package com.example.Integration.project.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "study_plans")
public class StudyPlan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String grades;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private java.time.OffsetDateTime createdAt;

    private String adminNumber;

    @ManyToOne
    @JoinColumn(name = "curriculum_id")
    private Curriculum curriculum;

    @Column(columnDefinition = "text")
    private String schedule;

    private String notes;

    @OneToMany(mappedBy = "studyPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("studyPlan-entries")
    private List<PlanEntry> entries;
}
