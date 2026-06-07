package com.example.Integration.project.dto;

import lombok.Data;
import java.util.List;

@Data
public class StudyPlanResponseDTO {
    private Long id;
    private String title;
    private String schedule;
    private String notes;
    private String adminNumber;
    private String curriculumTitle;
    private List<EntryView> entries;

    @Data
    public static class EntryView {
        private String date;
        private List<TopicView> topics;
    }

    @Data
    public static class TopicView {
        private Long topicId;
        private String title;
        private String description;
    }
}
