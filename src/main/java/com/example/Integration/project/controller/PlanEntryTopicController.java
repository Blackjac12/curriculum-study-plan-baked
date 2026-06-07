package com.example.Integration.project.controller;

import com.example.Integration.project.entity.PlanEntryTopic;
import com.example.Integration.project.service.PlanEntryTopicService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/plan-entry-topics")
@CrossOrigin(origins = "*")
public class PlanEntryTopicController {
    private final PlanEntryTopicService service;

    public PlanEntryTopicController(PlanEntryTopicService service) {
        this.service = service;
    }

    @GetMapping("/entry/{entryId}")
    public List<PlanEntryTopic> findByPlanEntry(@PathVariable Long entryId) {
        return service.findByPlanEntry(entryId);
    }

    @PostMapping("/create")
    public PlanEntryTopic create(@RequestBody PlanEntryTopic pet) {
        return service.save(pet);
    }

    @DeleteMapping("/{entryId}/{topicId}")
    public void delete(@PathVariable Long entryId, @PathVariable Long topicId) {
        service.delete(entryId, topicId);
    }
}
