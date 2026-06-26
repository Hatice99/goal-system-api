package com.hati.goal_system_api.dto.goal;

public record GoalResponse(
        Long id,
        String title,
        String description
) {
}
