package com.example.Integration.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.Integration.project.dto.CurriculumDTO;
import com.example.Integration.project.dto.TopicDTO;
import com.example.Integration.project.entity.*;
import com.example.Integration.project.exception.ResourceNotFoundException;
import com.example.Integration.project.repository.CurriculumRepository;
import com.example.Integration.project.repository.CurriculumTopicRepository;
import com.example.Integration.project.repository.TopicRepository;
import com.example.Integration.project.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CurriculumService {
    private final CurriculumRepository curriculumRepo;
    private final TopicRepository topicRepo;
    private final CurriculumTopicRepository ctRepo;
    private final UserRepository userRepo;
    private static final Logger log = LoggerFactory.getLogger(CurriculumService.class);

    public CurriculumService(CurriculumRepository curriculumRepo, TopicRepository topicRepo, CurriculumTopicRepository ctRepo, UserRepository userRepo) {
        this.curriculumRepo = curriculumRepo;
        this.topicRepo = topicRepo;
        this.ctRepo = ctRepo;
        this.userRepo = userRepo;
    }

    public List<Curriculum> findAll() {
        return curriculumRepo.findAll();
    }

    public Curriculum findById(Long id) {
        return curriculumRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Curriculum no encontrado: " + id));
    }

    @Transactional
    public Curriculum create(CurriculumDTO dto) {
        Curriculum c = new Curriculum();
        c.setTitle(dto.title);
        c.setLevel(dto.level);
        // createdBy, etc. si querés mapear, hacelo aquí
        Curriculum saved = curriculumRepo.save(c);

        // 1) crear topics nuevos si vienen en payload
        List<Topic> createdTopics = new ArrayList<>();
        if (dto.topics != null && !dto.topics.isEmpty()) {
            for (TopicDTO tDto : dto.topics) {
                Topic t = new Topic();
                t.setTitle(tDto.title);
                t.setCategory(tDto.category);
                t.setSubtopics(tDto.subtopics);
                createdTopics.add(topicRepo.save(t));
            }
        }

        // 2) asociar topicIds (existentes)
        List<Topic> topicsToAttach = new ArrayList<>();
        if (dto.topicIds != null) {
            for (Long tid : dto.topicIds) {
                Topic t = topicRepo.findById(tid).orElseThrow(() -> new ResourceNotFoundException("Topic no encontrado: " + tid));
                topicsToAttach.add(t);
            }
        }

        topicsToAttach.addAll(createdTopics);

        short pos = 0;
        for (Topic topic : topicsToAttach) {
            CurriculumTopic ct = new CurriculumTopic();
            ct.setId(new CurriculumTopicId(saved.getId(), topic.getId()));
            ct.setCurriculum(saved);
            ct.setTopic(topic);
            ct.setPosition(pos++);
            ctRepo.save(ct);
        }

        return curriculumRepo.findById(saved.getId()).orElse(saved);
    }

    @Transactional
    public Curriculum update(Long id, CurriculumDTO dto) {
        Curriculum c = findById(id);
        if (dto.title != null) c.setTitle(dto.title);
        if (dto.level != null) c.setLevel(dto.level);
        curriculumRepo.save(c);


        return c;
    }

    @Transactional
    public void addTopicToCurriculum(Long curriculumId, Long topicId) {
        Curriculum c = findById(curriculumId);
        Topic t = topicRepo.findById(topicId).orElseThrow(() -> new ResourceNotFoundException("Topic no encontrado: " + topicId));

        short nextPos = 0;
        List<CurriculumTopic> existing = ctRepo.findAll();
        CurriculumTopic ct = new CurriculumTopic();
        ct.setId(new CurriculumTopicId(c.getId(), t.getId()));
        ct.setCurriculum(c);
        ct.setTopic(t);
        ct.setPosition(nextPos);
        ctRepo.save(ct);
    }

    @Transactional
    public void removeTopicFromCurriculum(Long curriculumId, Long topicId) {
        CurriculumTopicId id = new CurriculumTopicId(curriculumId, topicId);
        if (ctRepo.existsById(id)) {
            ctRepo.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Asociación curriculum-topic no encontrada");
        }
    }

    public void delete(Long id) {
        Curriculum c = findById(id);
        curriculumRepo.delete(c);
    }

    public Curriculum save(Curriculum curriculum) {
        return curriculumRepo.save(curriculum);
    }

    @Transactional
    public Curriculum setCreatedBy(Long id, Long userId) {
        log.debug("setCreatedBy START id={}, userId={}", id, userId);
        Curriculum c = findById(id);
        log.debug("Before change createdBy={}", c.getCreatedBy() != null ? c.getCreatedBy().getId() : null);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + userId));
        c.setCreatedBy(user);

        Curriculum saved = curriculumRepo.saveAndFlush(c);
        log.debug("After saveAndFlush createdBy={}", saved.getCreatedBy() != null ? saved.getCreatedBy().getId() : null);

        Curriculum fresh = curriculumRepo.findById(saved.getId()).orElse(saved);
        log.debug("After re-fetch createdBy={}", fresh.getCreatedBy() != null ? fresh.getCreatedBy().getId() : null);
        return fresh;
    }

    @Transactional
    public Curriculum setReview(Long id, Long reviewerId, String reviewMessage) {
        log.debug("setReview START id={}, reviewerId={}, reviewMessage={}", id, reviewerId, reviewMessage);
        Curriculum c = findById(id);

        if (reviewerId == null) {
            // limpiar review
            c.setReviewedBy(null);
            c.setReviewedAt(null);
            c.setReviewMessage(null);
        } else {
            User reviewer = userRepo.findById(reviewerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario revisor no encontrado: " + reviewerId));
            c.setReviewedBy(reviewer);
            c.setReviewedAt(OffsetDateTime.now());
            // reviewMessage puede ser null -> limpiar, o no -> setear
            c.setReviewMessage(reviewMessage);
        }

        Curriculum saved = curriculumRepo.saveAndFlush(c);
        // re-fetch para evitar proxies nulos y devolver estado consistente
        return curriculumRepo.findById(saved.getId()).orElse(saved);
    }

}
