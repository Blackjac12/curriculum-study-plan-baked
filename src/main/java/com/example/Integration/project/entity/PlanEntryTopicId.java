package com.example.Integration.project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PlanEntryTopicId implements Serializable {
    private Long planEntryId;
    private Long topicId;
}
