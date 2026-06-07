package com.example.Integration.project.controller;

import com.example.Integration.project.dto.CurriculumDTO;
import com.example.Integration.project.entity.Curriculum;
import com.example.Integration.project.service.CurriculumService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/curriculums")
public class CurriculumController {
    private final CurriculumService service;

    public CurriculumController(CurriculumService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public List<Curriculum> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Curriculum getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<Curriculum> create(@RequestBody CurriculumDTO dto) {
        Curriculum created = service.create(dto);
        return ResponseEntity.created(URI.create("/curriculums/" + created.getId())).body(created);
    }

    //set createdBy to a curriculum
    @PutMapping("/{id}/creator/{userId}")
    public Curriculum setCreator(@PathVariable Long id, @PathVariable Long userId) {
        return service.setCreatedBy(id, userId);
    }


    @PutMapping("/update/{id}")
    public Curriculum update(@PathVariable Long id, @RequestBody CurriculumDTO dto) {
        return service.update(id, dto);
    }

    // Nuevo endpoint: set/reset review info
    // Se acepta reviewerId como String para permitir que el front mande 'null' o lo omita.
    @PutMapping("/{id}/review")
    public Curriculum setReview(@PathVariable Long id,
                                @RequestParam(required = false) String reviewerId,
                                @RequestParam(required = false) String reviewMessage) {
        Long reviewerIdLong = null;
        if (reviewerId != null && !reviewerId.isBlank() && !"null".equalsIgnoreCase(reviewerId)) {
            try {
                reviewerIdLong = Long.parseLong(reviewerId);
            } catch (NumberFormatException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "reviewerId must be a number or null");
            }
        }
        // Nota: si reviewerId es null (o 'null'), llamamos a setReview con reviewerId null para limpiar review info.
        return service.setReview(id, reviewerIdLong, reviewMessage);
    }

    @PostMapping("/{curriculumId}/topics/{topicId}")
    public ResponseEntity<Void> addTopic(@PathVariable Long curriculumId, @PathVariable Long topicId) {
        service.addTopicToCurriculum(curriculumId, topicId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{curriculumId}/topics/{topicId}")
    public ResponseEntity<Void> removeTopic(@PathVariable Long curriculumId, @PathVariable Long topicId) {
        service.removeTopicFromCurriculum(curriculumId, topicId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
