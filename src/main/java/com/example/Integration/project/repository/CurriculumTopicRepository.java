package com.example.Integration.project.repository;

import com.example.Integration.project.entity.CurriculumTopic;
import com.example.Integration.project.entity.CurriculumTopicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurriculumTopicRepository extends JpaRepository<CurriculumTopic, CurriculumTopicId> {
}
