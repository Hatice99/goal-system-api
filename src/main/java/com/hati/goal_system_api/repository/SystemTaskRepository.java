package com.hati.goal_system_api.repository;

import com.hati.goal_system_api.model.SystemTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SystemTaskRepository extends JpaRepository<SystemTask, Long> {

    List<SystemTask> findByGoalSystemId(Long goalSystemId);

    Optional<SystemTask> findByIdAndGoalSystemGoalUserId(Long id, Long userId);

    void deleteAllByGoalSystemId(Long goalSystemId);

    void deleteAllByGoalSystemGoalId(Long goalId);
}
