package com.hati.goal_system_api.service;

import com.hati.goal_system_api.dto.goalsystem.CreateGoalSystemRequest;
import com.hati.goal_system_api.dto.goalsystem.GoalSystemResponse;
import com.hati.goal_system_api.exception.ResourceNotFoundException;
import com.hati.goal_system_api.model.Goal;
import com.hati.goal_system_api.model.GoalSystem;
import com.hati.goal_system_api.repository.GoalRepository;
import com.hati.goal_system_api.repository.GoalSystemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoalSystemService {

    private final GoalSystemRepository goalSystemRepository;
    private final GoalRepository goalRepository;

    public GoalSystemResponse createGoalSystem(
            Long userId,
            CreateGoalSystemRequest request
    ) {
        Goal goal = goalRepository.findByIdAndUserId(request.goalId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        GoalSystem goalSystem = new GoalSystem();
        goalSystem.setTitle(request.title());
        goalSystem.setFrequency(request.frequency());
        goalSystem.setGoal(goal);

        GoalSystem savedGoalSystem = goalSystemRepository.save(goalSystem);

        return new GoalSystemResponse(
                savedGoalSystem.getId(),
                savedGoalSystem.getTitle(),
                savedGoalSystem.getFrequency(),
                savedGoalSystem.getGoal().getId()
        );
    }
}
