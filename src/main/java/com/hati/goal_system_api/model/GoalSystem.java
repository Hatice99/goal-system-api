package com.hati.goal_system_api.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// GoalSystem avoids the name "System" because java.lang.System already exists in Java.
@Entity
@Table(name = "goal_systems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoalSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequency frequency;

    // Many GoalSystems belong to one Goal.
    @ManyToOne(fetch = FetchType.LAZY)
    // This creates the "goal_id" column in the "goal_systems" table.
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // One GoalSystem can have many SystemTasks.
    @OneToMany(mappedBy = "goalSystem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SystemTask> tasks = new ArrayList<>();

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
