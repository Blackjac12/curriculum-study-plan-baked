package com.example.Integration.project.controller;

import com.example.Integration.project.dto.TopicDTO;
import com.example.Integration.project.entity.Topic;
import com.example.Integration.project.service.TopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/topics")
public class TopicController {
    private final TopicService service;

    public TopicController(TopicService service) {
        this.service = service;
    }

    @GetMapping
    public List<Topic> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Topic getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Topic> create(@RequestBody TopicDTO dto) {
        Topic created = service.create(dto);
        return ResponseEntity.created(URI.create("/topics/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public Topic update(@PathVariable Long id, @RequestBody TopicDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
