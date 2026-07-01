package com.hati.goal_system_api.repository;

import com.hati.goal_system_api.model.SystemTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemTaskRepository extends JpaRepository<SystemTask, Long> {

    List<SystemTask> findByGoalSystemId(Long goalSystemId);

    void deleteAllByGoalSystemId(Long goalSystemId);

    void deleteAllByGoalSystemGoalId(Long goalId);
}
