package com.example.Integration.project.service;

import com.example.Integration.project.dto.*;
import com.example.Integration.project.entity.*;
import com.example.Integration.project.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudyPlanService {
    private final StudyPlanRepository studyPlanRepo;
    private final PlanEntryRepository planEntryRepo;
    private final PlanEntryTopicRepository planEntryTopicRepo;
    private final UserRepository userRepo;
    private final CurriculumRepository curriculumRepo;
    private final TopicRepository topicRepo;

    public StudyPlanService(
            StudyPlanRepository studyPlanRepo,
            PlanEntryRepository planEntryRepo,
            PlanEntryTopicRepository planEntryTopicRepo,
            UserRepository userRepo,
            CurriculumRepository curriculumRepo,
            TopicRepository topicRepo
    ) {
        this.studyPlanRepo = studyPlanRepo;
        this.planEntryRepo = planEntryRepo;
        this.planEntryTopicRepo = planEntryTopicRepo;
        this.userRepo = userRepo;
        this.curriculumRepo = curriculumRepo;
        this.topicRepo = topicRepo;
    }

    public List<StudyPlan> getAll() {
        return studyPlanRepo.findAll();
    }

    public StudyPlan getById(Long id) {
        return studyPlanRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("StudyPlan not found"));
    }

    @Transactional
    public StudyPlan createWithEntries(StudyPlanDTO dto) {
        User creator = userRepo.findById(dto.getCreatedById())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Curriculum curriculum = curriculumRepo.findById(dto.getCurriculumId())
                .orElseThrow(() -> new RuntimeException("Curriculum not found"));

        StudyPlan plan = new StudyPlan();
        plan.setTitle(dto.getTitle());
        plan.setAdminNumber(dto.getAdminNumber());
        plan.setSchedule(dto.getSchedule());
        plan.setNotes(dto.getNotes());
        plan.setGrades(dto.getGrades());
        plan.setCreatedAt(OffsetDateTime.now());
        plan.setCreatedBy(creator);
        plan.setCurriculum(curriculum);


        StudyPlan savedPlan = studyPlanRepo.save(plan);

        if (dto.getEntries() != null) {
            for (PlanEntryDTO entryDto : dto.getEntries()) {
                PlanEntry entry = new PlanEntry();
                entry.setStudyPlan(savedPlan);
                entry.setDate(LocalDate.parse(entryDto.getDate()));
                entry.setCreatedAt(OffsetDateTime.now());
                PlanEntry savedEntry = planEntryRepo.save(entry);

                if (entryDto.getTopics() != null) {
                    for (PlanEntryTopicDTO topicDto : entryDto.getTopics()) {
                        Topic topic = topicRepo.findById(topicDto.getTopicId())
                                .orElseThrow(() -> new RuntimeException("Topic not found"));

                        PlanEntryTopic pet = new PlanEntryTopic();
                        pet.setId(new PlanEntryTopicId(savedEntry.getId(), topic.getId()));
                        pet.setPlanEntry(savedEntry);
                        pet.setTopic(topic);
                        pet.setDescription(topicDto.getDescription());

                        planEntryTopicRepo.save(pet);
                    }
                }
            }
        }

        return savedPlan;
    }

    public void delete(Long id) {
        studyPlanRepo.deleteById(id);
    }

    public StudyPlan save(StudyPlan plan) {
        return studyPlanRepo.save(plan);
    }

    public StudyPlanResponseDTO toResponseDTO(StudyPlan plan) {
        StudyPlanResponseDTO dto = new StudyPlanResponseDTO();
        dto.setId(plan.getId());
        dto.setTitle(plan.getTitle());
        dto.setSchedule(plan.getSchedule());
        dto.setNotes(plan.getNotes());
        dto.setAdminNumber(plan.getAdminNumber());
        dto.setCurriculumTitle(plan.getCurriculum().getTitle());

        if (plan.getEntries() != null) {
            dto.setEntries(
                    plan.getEntries().stream().map(entry -> {
                        StudyPlanResponseDTO.EntryView ev = new StudyPlanResponseDTO.EntryView();
                        ev.setDate(entry.getDate().toString());

                        if (entry.getTopics() != null) {
                            ev.setTopics(
                                    entry.getTopics().stream().map(pet -> {
                                        StudyPlanResponseDTO.TopicView tv = new StudyPlanResponseDTO.TopicView();
                                        tv.setTopicId(pet.getTopic().getId());
                                        tv.setTitle(pet.getTopic().getTitle());
                                        tv.setDescription(pet.getDescription());
                                        return tv;
                                    }).toList()
                            );
                        }

                        return ev;
                    }).toList()
            );
        }

        return dto;
    }

    @Transactional
    public StudyPlan update(Long id, StudyPlanDTO dto) {
        StudyPlan plan = getById(id);
        if (dto.getTitle() != null) plan.setTitle(dto.getTitle());
        if (dto.getAdminNumber() != null) plan.setAdminNumber(dto.getAdminNumber());
        if (dto.getSchedule() != null) plan.setSchedule(dto.getSchedule());
        if (dto.getNotes() != null) plan.setNotes(dto.getNotes());
        if (dto.getGrades() != null) plan.setGrades(dto.getGrades());

        // Si vienen entries en el DTO, actualizar/crear/eliminar según date
        if (dto.getEntries() != null) {
            // Mapear entradas existentes por fecha
            List<PlanEntry> existingEntries = plan.getEntries() == null ? new ArrayList<>() : new ArrayList<>(plan.getEntries());
            java.util.Map<java.time.LocalDate, PlanEntry> existingByDate = new java.util.HashMap<>();
            for (PlanEntry e : existingEntries) {
                existingByDate.put(e.getDate(), e);
            }

            java.util.Set<java.time.LocalDate> incomingDates = new java.util.HashSet<>();

            for (PlanEntryDTO entryDto : dto.getEntries()) {
                java.time.LocalDate date = java.time.LocalDate.parse(entryDto.getDate());
                incomingDates.add(date);
                PlanEntry entry = existingByDate.get(date);
                if (entry == null) {
                    // crear nueva entrada
                    entry = new PlanEntry();
                    entry.setStudyPlan(plan);
                    entry.setDate(date);
                    entry.setCreatedAt(OffsetDateTime.now());
                    entry = planEntryRepo.save(entry);
                    // añadir a la colección del plan (in-memory)
                    if (plan.getEntries() == null) plan.setEntries(new java.util.ArrayList<>());
                    plan.getEntries().add(entry);
                }

                // Procesar topics de la entrada
                java.util.List<PlanEntryTopic> existingTopics = entry.getTopics() == null ? new java.util.ArrayList<>() : new java.util.ArrayList<>(entry.getTopics());
                java.util.Map<Long, PlanEntryTopic> existingTopicByTopicId = new java.util.HashMap<>();
                for (PlanEntryTopic pet : existingTopics) {
                    existingTopicByTopicId.put(pet.getTopic().getId(), pet);
                }

                java.util.Set<Long> incomingTopicIds = new java.util.HashSet<>();
                if (entryDto.getTopics() != null) {
                    for (PlanEntryTopicDTO topicDto : entryDto.getTopics()) {
                        incomingTopicIds.add(topicDto.getTopicId());
                        PlanEntryTopic pet = existingTopicByTopicId.get(topicDto.getTopicId());
                        com.example.Integration.project.entity.Topic topicEntity = topicRepo.findById(topicDto.getTopicId())
                                .orElseThrow(() -> new RuntimeException("Topic not found"));
                        if (pet == null) {
                            // crear nueva asociación
                            pet = new PlanEntryTopic();
                            pet.setId(new PlanEntryTopicId(entry.getId(), topicEntity.getId()));
                            pet.setPlanEntry(entry);
                            pet.setTopic(topicEntity);
                            pet.setDescription(topicDto.getDescription());
                            planEntryTopicRepo.save(pet);
                            // añadir a la colección del entry
                            if (entry.getTopics() == null) entry.setTopics(new java.util.ArrayList<>());
                            entry.getTopics().add(pet);
                        } else {
                            // actualizar descripción
                            pet.setDescription(topicDto.getDescription());
                            planEntryTopicRepo.save(pet);
                        }
                    }
                }

                // Eliminar topics que existen pero no vienen en el payload
                for (PlanEntryTopic existingPet : existingTopics) {
                    if (!incomingTopicIds.contains(existingPet.getTopic().getId())) {
                        // remover de la colección gestionada; con orphanRemoval=true JPA borrará la fila
                        if (entry.getTopics() != null) {
                            entry.getTopics().removeIf(p -> p.getId().equals(existingPet.getId()));
                        }
                    }
                }
            }

            // Borrar entries existentes que no están en el payload
            for (PlanEntry existingEntry : existingEntries) {
                if (!incomingDates.contains(existingEntry.getDate())) {
                    // borrar topics primero
                    if (plan.getEntries() != null) {
                        plan.getEntries().removeIf(e -> e.getId().equals(existingEntry.getId()));
                    }
                }
            }
        }

        return studyPlanRepo.save(plan);
    }
}
