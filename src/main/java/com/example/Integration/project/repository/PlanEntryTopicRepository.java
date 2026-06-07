package com.example.Integration.project.repository;

import com.example.Integration.project.entity.PlanEntryTopic;
import com.example.Integration.project.entity.PlanEntryTopicId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlanEntryTopicRepository extends JpaRepository<PlanEntryTopic, PlanEntryTopicId> {
    List<PlanEntryTopic> findByPlanEntryId(Long planEntryId);
}
