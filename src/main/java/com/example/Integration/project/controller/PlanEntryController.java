package com.example.Integration.project.controller;

import com.example.Integration.project.entity.PlanEntry;
import com.example.Integration.project.service.PlanEntryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/plan-entries")
@CrossOrigin(origins = "*")
public class PlanEntryController {
    private final PlanEntryService service;

    public PlanEntryController(PlanEntryService service) {
        this.service = service;
    }

    @GetMapping("/study-plan/{studyPlanId}")
    public List<PlanEntry> findByStudyPlan(@PathVariable Long studyPlanId) {
        return service.findByStudyPlan(studyPlanId);
    }

    @PostMapping("/create")
    public PlanEntry create(@RequestBody PlanEntry entry) {
        return service.save(entry);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
