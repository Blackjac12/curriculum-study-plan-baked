package com.example.Integration.project.repository;

import com.example.Integration.project.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {}