package com.hati.goal_system_api.service;

import com.hati.goal_system_api.dto.goal.CreateGoalRequest;
import com.hati.goal_system_api.dto.goal.GoalResponse;
import com.hati.goal_system_api.exception.ResourceNotFoundException;
import com.hati.goal_system_api.model.Goal;
import com.hati.goal_system_api.model.User;
import com.hati.goal_system_api.repository.GoalRepository;
import com.hati.goal_system_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

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
}
