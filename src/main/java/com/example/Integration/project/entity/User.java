package com.example.Integration.project.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false)
    private boolean isVerified = false;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "user")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<RoleAssignment> roleAssignments;

    @PrePersist
    @PreUpdate
    public void normalize() {
        if (this.email != null) {
            this.email = this.email.trim().toLowerCase();
        }
    }
}

