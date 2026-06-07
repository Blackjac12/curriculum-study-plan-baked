package com.example.Integration.project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role_assignments")
public class RoleAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "assigned_by")
    private User assignedBy;

    @Column(nullable = false)
    private OffsetDateTime assignedAt = OffsetDateTime.now();

    private OffsetDateTime acceptedAt;
    private String status;
}
