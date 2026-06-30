package com.hati.goal_system_api.repository;

import com.hati.goal_system_api.model.GoalSystem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoalSystemRepository extends JpaRepository<GoalSystem, Long> {

    List<GoalSystem> findByGoalId(Long goalId);

    List<GoalSystem> findByGoalUserId(Long userId);

    Optional<GoalSystem> findByIdAndGoalUserId(Long id, Long userId);

    void deleteAllByGoalId(Long goalId);
}
