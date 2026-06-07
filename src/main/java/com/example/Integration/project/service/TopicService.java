package com.example.Integration.project.service;

import com.example.Integration.project.dto.TopicDTO;
import com.example.Integration.project.entity.Topic;
import com.example.Integration.project.exception.ResourceNotFoundException;
import com.example.Integration.project.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicService {
    private final TopicRepository repo;

    public TopicService(TopicRepository repo) {
        this.repo = repo;
    }

    public List<Topic> findAll() {
        return repo.findAll();
    }

    public Topic findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Topic no encontrado: " + id));
    }

    public Topic create(TopicDTO dto) {
        Topic t = new Topic();
        t.setTitle(dto.title);
        t.setCategory(dto.category);
        t.setSubtopics(dto.subtopics);
        return repo.save(t);
    }

    public Topic update(Long id, TopicDTO dto) {
        Topic t = findById(id);
        if (dto.title != null) t.setTitle(dto.title);
        if (dto.category != null) t.setCategory(dto.category);
        if (dto.subtopics != null) t.setSubtopics(dto.subtopics);
        return repo.save(t);
    }

    public void delete(Long id) {
        Topic t = findById(id);
        repo.delete(t);
    }

    public List<Topic> createBulkFromDtos(List<TopicDTO> dtos) {
        List<Topic> list = dtos.stream().map(d -> {
            Topic t = new Topic();
            t.setTitle(d.title);
            t.setCategory(d.category);
            t.setSubtopics(d.subtopics);
            return t;
        }).collect(Collectors.toList());
        return repo.saveAll(list);
    }
}
