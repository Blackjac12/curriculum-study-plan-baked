package com.example.Integration.project.dto;

import java.util.List;

public class CurriculumDTO {
    public Long id;
    public String title;
    public String level;
    public String status;
    public List<TopicDTO> topics;
    public List<Long> topicIds;
}
