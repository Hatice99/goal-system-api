package com.hati.goal_system_api.dto.goalsystem;

import com.hati.goal_system_api.model.Frequency;

public record GoalSystemResponse(
        Long id,
        String title,
        Frequency frequency,
        Long goalId
) {
}
