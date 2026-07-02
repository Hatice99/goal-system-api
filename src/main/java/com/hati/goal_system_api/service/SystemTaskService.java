package com.hati.goal_system_api.service;

import com.hati.goal_system_api.dto.systemtask.CreateSystemTaskRequest;
import com.hati.goal_system_api.dto.systemtask.CompleteSystemTaskResponse;
import com.hati.goal_system_api.dto.systemtask.SystemTaskResponse;
import com.hati.goal_system_api.dto.systemtask.UpdateSystemTaskRequest;
import com.hati.goal_system_api.exception.ResourceNotFoundException;
import com.hati.goal_system_api.model.GoalSystem;
import com.hati.goal_system_api.model.SystemTask;
import com.hati.goal_system_api.repository.GoalSystemRepository;
import com.hati.goal_system_api.repository.SystemTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemTaskService {

    private final SystemTaskRepository systemTaskRepository;
    private final GoalSystemRepository goalSystemRepository;

    public SystemTaskResponse createSystemTask(
            Long userId,
            Long goalSystemId,
            CreateSystemTaskRequest request
    ) {
        GoalSystem goalSystem = goalSystemRepository
                .findByIdAndGoalUserId(goalSystemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("GoalSystem not found"));

        SystemTask task = new SystemTask();
        task.setTitle(request.title());
        task.setGoalSystem(goalSystem);

        SystemTask savedTask = systemTaskRepository.save(task);

        return new SystemTaskResponse(
                savedTask.getId(),
                savedTask.getTitle(),
                savedTask.isCompleted()
        );
    }

    public SystemTaskResponse getSystemTaskById(Long userId, Long taskId) {
        SystemTask task = systemTaskRepository
                .findByIdAndGoalSystemGoalUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("SystemTask not found"));

        return toResponse(task);
    }

    public SystemTaskResponse updateSystemTask(
            Long userId,
            Long taskId,
            UpdateSystemTaskRequest request
    ) {
        SystemTask task = systemTaskRepository
                .findByIdAndGoalSystemGoalUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("SystemTask not found"));

        task.setTitle(request.title());
        SystemTask updatedTask = systemTaskRepository.save(task);

        return toResponse(updatedTask);
    }

    public CompleteSystemTaskResponse completeSystemTask(
            Long userId,
            Long taskId
    ) {
        SystemTask task = systemTaskRepository
                .findByIdAndGoalSystemGoalUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("SystemTask not found"));

        task.complete();
        SystemTask completedTask = systemTaskRepository.save(task);

        return new CompleteSystemTaskResponse(
                completedTask.getId(),
                completedTask.isCompleted()
        );
    }

    @Transactional
    public void deleteSystemTask(Long userId, Long taskId) {
        SystemTask task = systemTaskRepository
                .findByIdAndGoalSystemGoalUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("SystemTask not found"));

        systemTaskRepository.delete(task);
    }

    private SystemTaskResponse toResponse(SystemTask task) {
        return new SystemTaskResponse(
                task.getId(),
                task.getTitle(),
                task.isCompleted()
        );
    }
}
