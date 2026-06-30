package com.hati.goal_system_api.dto.goalsystem;

import com.hati.goal_system_api.model.Frequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateGoalSystemRequest(
        @NotBlank
        String title,

        @NotNull
        Frequency frequency,

        @NotNull
        Long goalId
) {
}
