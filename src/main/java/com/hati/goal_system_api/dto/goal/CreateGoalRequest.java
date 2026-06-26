package com.hati.goal_system_api.dto.goal;

public record CreateGoalRequest(
        String title,
        String description
) {
}
