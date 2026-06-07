package com.example.Integration.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plan_entries")
public class PlanEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "study_plan_id", nullable = false)
    @JsonBackReference("studyPlan-entries")
    private StudyPlan studyPlan;

    @Column(nullable = false)
    private LocalDate date;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "planEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("planEntry-topics")
    private List<PlanEntryTopic> topics;
}
