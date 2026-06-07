package com.example.Integration.project.dto;

import lombok.Data;
import java.util.List;

@Data
public class PlanEntryDTO {
    private String date;
    private List<PlanEntryTopicDTO> topics;
}
