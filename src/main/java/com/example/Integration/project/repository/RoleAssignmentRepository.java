package com.example.Integration.project.repository;

import com.example.Integration.project.entity.RoleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleAssignmentRepository extends JpaRepository<RoleAssignment, Long> {
}