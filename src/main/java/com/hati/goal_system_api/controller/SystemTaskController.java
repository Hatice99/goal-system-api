package com.hati.goal_system_api.controller;

import com.hati.goal_system_api.dto.systemtask.CreateSystemTaskRequest;
import com.hati.goal_system_api.dto.systemtask.CompleteSystemTaskResponse;
import com.hati.goal_system_api.dto.systemtask.SystemTaskResponse;
import com.hati.goal_system_api.dto.systemtask.UpdateSystemTaskRequest;
import com.hati.goal_system_api.service.SystemTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SystemTaskController {

    private final SystemTaskService systemTaskService;

    @PostMapping("/systems/{id}/tasks")
    public ResponseEntity<SystemTaskResponse> createSystemTask(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @Valid @RequestBody CreateSystemTaskRequest request
    ) {
        SystemTaskResponse response =
                systemTaskService.createSystemTask(userId, id, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<SystemTaskResponse> getSystemTaskById(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(systemTaskService.getSystemTaskById(userId, id));
    }

    @PatchMapping("/tasks/{id}")
    public ResponseEntity<SystemTaskResponse> updateSystemTask(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateSystemTaskRequest request
    ) {
        return ResponseEntity.ok(
                systemTaskService.updateSystemTask(userId, id, request)
        );
    }

    @PatchMapping("/tasks/{id}/complete")
    public ResponseEntity<CompleteSystemTaskResponse> completeSystemTask(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                systemTaskService.completeSystemTask(userId, id)
        );
    }
}
