package com.example.Integration.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plan_entry_topics")
public class PlanEntryTopic {
    @EmbeddedId
    private PlanEntryTopicId id;

    @ManyToOne
    @MapsId("planEntryId")
    @JoinColumn(name = "plan_entry_id")
    @JsonBackReference("planEntry-topics")
    private PlanEntry planEntry;

    @ManyToOne
    @MapsId("topicId")
    @JoinColumn(name = "topic_id")
    private Topic topic;

    private String description;
}

