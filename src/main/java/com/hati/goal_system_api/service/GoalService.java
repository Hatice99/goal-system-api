package com.hati.goal_system_api.service;

import com.hati.goal_system_api.dto.goal.CreateGoalRequest;
import com.hati.goal_system_api.dto.goal.GoalResponse;
import com.hati.goal_system_api.dto.goal.UpdateGoalRequest;
import com.hati.goal_system_api.exception.ResourceNotFoundException;
import com.hati.goal_system_api.model.Goal;
import com.hati.goal_system_api.model.User;
import com.hati.goal_system_api.repository.GoalRepository;
import com.hati.goal_system_api.repository.GoalSystemRepository;
import com.hati.goal_system_api.repository.SystemTaskRepository;
import com.hati.goal_system_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalSystemRepository goalSystemRepository;
    private final SystemTaskRepository systemTaskRepository;

    public GoalResponse createGoal(Long userId, CreateGoalRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Goal goal = new Goal();
        goal.setTitle(request.title());
        goal.setDescription(request.description());
        goal.setUser(user);

        Goal savedGoal = goalRepository.save(goal);

        return new GoalResponse(
                savedGoal.getId(),
                savedGoal.getTitle(),
                savedGoal.getDescription()
        );
    }

    public List<GoalResponse> getGoalsForUser(Long userId) {
        return goalRepository.findByUserId(userId).stream()
                .map(goal -> new GoalResponse(
                        goal.getId(),
                        goal.getTitle(),
                        goal.getDescription()
                ))
                .toList();
    }

    public GoalResponse getGoalById(Long userId, Long goalId) {
        Goal goal = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        return new GoalResponse(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription()
        );
    }

    public GoalResponse updateGoal(
            Long userId,
            Long goalId,
            UpdateGoalRequest request
    ) {
        Goal goal = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (request.title() != null) {
            goal.setTitle(request.title());
        }

        if (request.description() != null) {
            goal.setDescription(request.description());
        }

        Goal updatedGoal = goalRepository.save(goal);

        return new GoalResponse(
                updatedGoal.getId(),
                updatedGoal.getTitle(),
                updatedGoal.getDescription()
        );
    }

    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        Goal goal = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        systemTaskRepository.deleteAllByGoalSystemGoalId(goalId);
        goalSystemRepository.deleteAllByGoalId(goalId);
        goalRepository.delete(goal);
    }
}
