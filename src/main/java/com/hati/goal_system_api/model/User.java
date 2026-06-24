package com.hati.goal_system_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// @Entity: This Java class is managed by JPA as a database entity.
@Entity
// @Table: Defines the database table name.
@Table(name = "users")
// Lombok generates getters, setters, and constructors automatically.
@Getter
@Setter
@NoArgsConstructor //empty constructor
@AllArgsConstructor //constructor with all fields
public class User {

    // @Id: Primary key of the table.
    @Id
    // The database generates the ID automatically.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable = false: required field. unique = true: value must be unique.
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    // Later we store the hashed password here, not the plain text password.
    @Column(nullable = false)
    private String password;

    // Set only when the row is created and never updated afterwards.
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // @PrePersist: This method runs automatically right before the first save.
    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
