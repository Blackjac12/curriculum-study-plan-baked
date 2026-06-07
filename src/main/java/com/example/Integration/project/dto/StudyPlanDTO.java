package com.example.Integration.project.dto;

import lombok.Data;
import java.util.List;

@Data
public class StudyPlanDTO {
    private String title;
    private String adminNumber;
    private String schedule;
    private String notes;
    private String grades;
    private Long createdById;
    private Long curriculumId;
    private List<PlanEntryDTO> entries;

    public void setId(Long id) {
    }
}
