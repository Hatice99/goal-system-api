package com.hati.goal_system_api.repository;

import com.hati.goal_system_api.model.GoalSystem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalSystemRepository extends JpaRepository<GoalSystem, Long> {

    List<GoalSystem> findByGoalId(Long goalId);
}
