package com.example.Integration.project.service;

import com.example.Integration.project.entity.PlanEntryTopic;
import com.example.Integration.project.repository.PlanEntryTopicRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlanEntryTopicService {
    private final PlanEntryTopicRepository repo;

    public PlanEntryTopicService(PlanEntryTopicRepository repo) {
        this.repo = repo;
    }

    public List<PlanEntryTopic> findByPlanEntry(Long entryId) {
        return repo.findByPlanEntryId(entryId);
    }

    public PlanEntryTopic save(PlanEntryTopic pet) {
        return repo.save(pet);
    }

    public void delete(Long entryId, Long topicId) {
        repo.deleteById(new com.example.Integration.project.entity.PlanEntryTopicId(entryId, topicId));
    }
}
