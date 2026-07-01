package com.hati.goal_system_api.service;

import com.hati.goal_system_api.dto.goalsystem.CreateGoalSystemRequest;
import com.hati.goal_system_api.dto.goalsystem.GoalSystemResponse;
import com.hati.goal_system_api.dto.goalsystem.UpdateGoalSystemRequest;
import com.hati.goal_system_api.exception.ResourceNotFoundException;
import com.hati.goal_system_api.model.Goal;
import com.hati.goal_system_api.model.GoalSystem;
import com.hati.goal_system_api.repository.GoalRepository;
import com.hati.goal_system_api.repository.GoalSystemRepository;
import com.hati.goal_system_api.repository.SystemTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalSystemService {

    private final GoalSystemRepository goalSystemRepository;
    private final GoalRepository goalRepository;
    private final SystemTaskRepository systemTaskRepository;

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

    public List<GoalSystemResponse> getGoalSystemsForUser(Long userId) {
        return goalSystemRepository.findByGoalUserId(userId).stream()
                .map(goalSystem -> new GoalSystemResponse(
                        goalSystem.getId(),
                        goalSystem.getTitle(),
                        goalSystem.getFrequency(),
                        goalSystem.getGoal().getId()
                ))
                .toList();
    }

    public GoalSystemResponse updateGoalSystem(
            Long userId,
            Long goalSystemId,
            UpdateGoalSystemRequest request
    ) {
        GoalSystem goalSystem = goalSystemRepository
                .findByIdAndGoalUserId(goalSystemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("GoalSystem not found"));

        if (request.title() != null) {
            goalSystem.setTitle(request.title());
        }

        if (request.frequency() != null) {
            goalSystem.setFrequency(request.frequency());
        }

        GoalSystem updatedGoalSystem = goalSystemRepository.save(goalSystem);

        return new GoalSystemResponse(
                updatedGoalSystem.getId(),
                updatedGoalSystem.getTitle(),
                updatedGoalSystem.getFrequency(),
                updatedGoalSystem.getGoal().getId()
        );
    }

    @Transactional
    public void deleteGoalSystem(Long userId, Long goalSystemId) {
        GoalSystem goalSystem = goalSystemRepository
                .findByIdAndGoalUserId(goalSystemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("GoalSystem not found"));

        systemTaskRepository.deleteAllByGoalSystemId(goalSystemId);
        goalSystemRepository.delete(goalSystem);
    }
}
