package com.hati.goal_system_api.dto.goal;

public record UpdateGoalRequest(
        String title,
        String description
) {
}
