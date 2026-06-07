package com.example.Integration.project.service;

import com.example.Integration.project.entity.PlanEntry;
import com.example.Integration.project.repository.PlanEntryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlanEntryService {
    private final PlanEntryRepository repo;

    public PlanEntryService(PlanEntryRepository repo) {
        this.repo = repo;
    }

    public List<PlanEntry> findByStudyPlan(Long studyPlanId) {
        return repo.findByStudyPlanId(studyPlanId);
    }

    public PlanEntry save(PlanEntry entry) {
        return repo.save(entry);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
