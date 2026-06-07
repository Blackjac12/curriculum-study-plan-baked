package com.example.Integration.project.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "curriculums")
public class Curriculum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String level;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    private String status;

    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    private OffsetDateTime reviewedAt;
    private String reviewMessage;

    @OneToMany(mappedBy = "curriculum")
    @JsonManagedReference
    private List<CurriculumTopic> topics;
}
