package com.hati.goal_system_api.dto.auth;

import java.time.LocalDateTime;

public record RegisterResponse(
        Long id,
        String username,
        String email,
        LocalDateTime createdAt
) {
}
