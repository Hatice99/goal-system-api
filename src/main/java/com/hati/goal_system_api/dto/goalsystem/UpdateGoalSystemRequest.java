package com.hati.goal_system_api.dto.goalsystem;

import com.hati.goal_system_api.model.Frequency;

public record UpdateGoalSystemRequest(
        String title,
        Frequency frequency
) {
}
