package com.hati.goal_system_api.dto.systemtask;

public record SystemTaskResponse(
        Long id,
        String title,
        boolean completed
) {
}
