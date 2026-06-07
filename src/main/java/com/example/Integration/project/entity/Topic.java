package com.example.Integration.project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "topics")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String category;

    @ElementCollection
    @CollectionTable(
            name = "topic_subtopics",
            joinColumns = @JoinColumn(name = "topic_id")
    )
    @Column(name = "subtopic")
    private List<String> subtopics;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
