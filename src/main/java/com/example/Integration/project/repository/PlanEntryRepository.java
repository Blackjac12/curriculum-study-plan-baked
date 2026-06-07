package com.example.Integration.project.repository;

import com.example.Integration.project.entity.PlanEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlanEntryRepository extends JpaRepository<PlanEntry, Long> {
    List<PlanEntry> findByStudyPlanId(Long studyPlanId);
}
