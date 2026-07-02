package com.hati.goal_system_api.dto.systemtask;

public record CompleteSystemTaskResponse(
        Long id,
        boolean completed
) {
}
