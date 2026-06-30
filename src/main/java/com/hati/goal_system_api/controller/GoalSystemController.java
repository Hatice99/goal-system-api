package com.hati.goal_system_api.controller;

import com.hati.goal_system_api.dto.goalsystem.CreateGoalSystemRequest;
import com.hati.goal_system_api.dto.goalsystem.GoalSystemResponse;
import com.hati.goal_system_api.service.GoalSystemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/systems")
@RequiredArgsConstructor
public class GoalSystemController {

    private final GoalSystemService goalSystemService;

    @PostMapping
    public ResponseEntity<GoalSystemResponse> createGoalSystem(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateGoalSystemRequest request
    ) {
        GoalSystemResponse response = goalSystemService.createGoalSystem(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
