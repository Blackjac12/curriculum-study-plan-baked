package com.example.Integration.project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CurriculumTopicId implements Serializable {
    private Long curriculumId;
    private Long topicId;
}
