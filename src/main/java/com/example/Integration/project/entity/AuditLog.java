package com.example.Integration.project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String action;

    private String resourceType;
    private Long resourceId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> data;

    private OffsetDateTime createdAt = OffsetDateTime.now();
}
