package com.hati.goal_system_api.dto.systemtask;

import jakarta.validation.constraints.NotBlank;

public record CreateSystemTaskRequest(
        @NotBlank
        String title
) {
}
