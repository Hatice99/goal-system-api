package com.hati.goal_system_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// SystemTask is a single task inside a GoalSystem.
@Entity
@Table(name = "system_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    // New tasks are not completed by default.
    @Column(nullable = false)
    private boolean completed = false;

    // Many SystemTasks belong to one GoalSystem.
    @ManyToOne(fetch = FetchType.LAZY)
    // This creates the "system_id" column in the "system_tasks" table.
    @JoinColumn(name = "system_id", nullable = false)
    private GoalSystem goalSystem;
}
