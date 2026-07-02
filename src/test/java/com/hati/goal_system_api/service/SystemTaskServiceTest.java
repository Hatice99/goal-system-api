package com.hati.goal_system_api.service;

import com.hati.goal_system_api.dto.systemtask.CreateSystemTaskRequest;
import com.hati.goal_system_api.dto.systemtask.CompleteSystemTaskResponse;
import com.hati.goal_system_api.dto.systemtask.SystemTaskResponse;
import com.hati.goal_system_api.dto.systemtask.UpdateSystemTaskRequest;
import com.hati.goal_system_api.exception.ResourceNotFoundException;
import com.hati.goal_system_api.model.Frequency;
import com.hati.goal_system_api.model.Goal;
import com.hati.goal_system_api.model.GoalSystem;
import com.hati.goal_system_api.model.SystemTask;
import com.hati.goal_system_api.model.User;
import com.hati.goal_system_api.repository.GoalRepository;
import com.hati.goal_system_api.repository.GoalSystemRepository;
import com.hati.goal_system_api.repository.SystemTaskRepository;
import com.hati.goal_system_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class SystemTaskServiceTest {

    @Autowired
    private SystemTaskService systemTaskService;

    @Autowired
    private SystemTaskRepository systemTaskRepository;

    @Autowired
    private GoalSystemRepository goalSystemRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldAddTaskToUsersGoalSystem() {
        User user = new User();
        user.setUsername("hati");
        user.setEmail("hati@example.com");
        user.setPassword("hashed-password");
        User savedUser = userRepository.save(user);

        Goal goal = new Goal();
        goal.setTitle("Improve health");
        goal.setDescription("Build healthy routines");
        goal.setUser(savedUser);
        Goal savedGoal = goalRepository.save(goal);

        GoalSystem goalSystem = new GoalSystem();
        goalSystem.setTitle("Morning exercise");
        goalSystem.setFrequency(Frequency.DAILY);
        goalSystem.setGoal(savedGoal);
        GoalSystem savedGoalSystem = goalSystemRepository.save(goalSystem);

        CreateSystemTaskRequest request =
                new CreateSystemTaskRequest("Run for 20 minutes");

        SystemTaskResponse response = systemTaskService.createSystemTask(
                savedUser.getId(),
                savedGoalSystem.getId(),
                request
        );

        SystemTask savedTask = systemTaskRepository.findById(response.id())
                .orElseThrow();

        assertThat(response.title()).isEqualTo("Run for 20 minutes");
        assertThat(response.completed()).isFalse();
        assertThat(savedTask.getGoalSystem().getId())
                .isEqualTo(savedGoalSystem.getId());
    }

    @Test
    void shouldNotAddTaskToAnotherUsersGoalSystem() {
        User firstUser = new User();
        firstUser.setUsername("hati");
        firstUser.setEmail("hati@example.com");
        firstUser.setPassword("hashed-password");
        User savedFirstUser = userRepository.save(firstUser);

        User secondUser = new User();
        secondUser.setUsername("alex");
        secondUser.setEmail("alex@example.com");
        secondUser.setPassword("hashed-password");
        User savedSecondUser = userRepository.save(secondUser);

        Goal secondUsersGoal = new Goal();
        secondUsersGoal.setTitle("Improve health");
        secondUsersGoal.setDescription("Build healthy routines");
        secondUsersGoal.setUser(savedSecondUser);
        Goal savedGoal = goalRepository.save(secondUsersGoal);

        GoalSystem secondUsersGoalSystem = new GoalSystem();
        secondUsersGoalSystem.setTitle("Morning exercise");
        secondUsersGoalSystem.setFrequency(Frequency.DAILY);
        secondUsersGoalSystem.setGoal(savedGoal);
        GoalSystem savedGoalSystem = goalSystemRepository.save(secondUsersGoalSystem);

        CreateSystemTaskRequest request =
                new CreateSystemTaskRequest("Run for 20 minutes");

        assertThatThrownBy(() -> systemTaskService.createSystemTask(
                savedFirstUser.getId(),
                savedGoalSystem.getId(),
                request
        ))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("GoalSystem not found");

        assertThat(systemTaskRepository.findAll()).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenGoalSystemDoesNotExist() {
        CreateSystemTaskRequest request =
                new CreateSystemTaskRequest("Run for 20 minutes");

        assertThatThrownBy(() -> systemTaskService.createSystemTask(
                1L,
                999L,
                request
        ))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("GoalSystem not found");

        assertThat(systemTaskRepository.findAll()).isEmpty();
    }

    @Test
    void shouldGetTaskByIdForUser() {
        User user = new User();
        user.setUsername("hati");
        user.setEmail("hati@example.com");
        user.setPassword("hashed-password");
        User savedUser = userRepository.save(user);

        Goal goal = new Goal();
        goal.setTitle("Improve health");
        goal.setDescription("Build healthy routines");
        goal.setUser(savedUser);
        Goal savedGoal = goalRepository.save(goal);

        GoalSystem goalSystem = new GoalSystem();
        goalSystem.setTitle("Morning exercise");
        goalSystem.setFrequency(Frequency.DAILY);
        goalSystem.setGoal(savedGoal);
        GoalSystem savedGoalSystem = goalSystemRepository.save(goalSystem);

        SystemTask task = new SystemTask();
        task.setTitle("Run for 20 minutes");
        task.setGoalSystem(savedGoalSystem);
        SystemTask savedTask = systemTaskRepository.save(task);

        SystemTaskResponse response = systemTaskService.getSystemTaskById(
                savedUser.getId(),
                savedTask.getId()
        );

        assertThat(response.id()).isEqualTo(savedTask.getId());
        assertThat(response.title()).isEqualTo("Run for 20 minutes");
        assertThat(response.completed()).isFalse();
    }

    @Test
    void shouldNotGetTaskBelongingToAnotherUser() {
        User firstUser = new User();
        firstUser.setUsername("hati");
        firstUser.setEmail("hati@example.com");
        firstUser.setPassword("hashed-password");
        User savedFirstUser = userRepository.save(firstUser);

        User secondUser = new User();
        secondUser.setUsername("alex");
        secondUser.setEmail("alex@example.com");
        secondUser.setPassword("hashed-password");
        User savedSecondUser = userRepository.save(secondUser);

        Goal secondUsersGoal = new Goal();
        secondUsersGoal.setTitle("Improve health");
        secondUsersGoal.setDescription("Build healthy routines");
        secondUsersGoal.setUser(savedSecondUser);
        Goal savedGoal = goalRepository.save(secondUsersGoal);

        GoalSystem secondUsersGoalSystem = new GoalSystem();
        secondUsersGoalSystem.setTitle("Morning exercise");
        secondUsersGoalSystem.setFrequency(Frequency.DAILY);
        secondUsersGoalSystem.setGoal(savedGoal);
        GoalSystem savedGoalSystem = goalSystemRepository.save(secondUsersGoalSystem);

        SystemTask task = new SystemTask();
        task.setTitle("Run for 20 minutes");
        task.setGoalSystem(savedGoalSystem);
        SystemTask savedTask = systemTaskRepository.save(task);

        assertThatThrownBy(() -> systemTaskService.getSystemTaskById(
                savedFirstUser.getId(),
                savedTask.getId()
        ))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("SystemTask not found");
    }

    @Test
    void shouldThrowExceptionWhenTaskDoesNotExist() {
        assertThatThrownBy(() -> systemTaskService.getSystemTaskById(1L, 999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("SystemTask not found");
    }

    @Test
    void shouldUpdateTaskTitleForUser() {
        User user = new User();
        user.setUsername("hati");
        user.setEmail("hati@example.com");
        user.setPassword("hashed-password");
        User savedUser = userRepository.save(user);

        Goal goal = new Goal();
        goal.setTitle("Improve health");
        goal.setDescription("Build healthy routines");
        goal.setUser(savedUser);
        Goal savedGoal = goalRepository.save(goal);

        GoalSystem goalSystem = new GoalSystem();
        goalSystem.setTitle("Morning exercise");
        goalSystem.setFrequency(Frequency.DAILY);
        goalSystem.setGoal(savedGoal);
        GoalSystem savedGoalSystem = goalSystemRepository.save(goalSystem);

        SystemTask task = new SystemTask();
        task.setTitle("Run");
        task.setGoalSystem(savedGoalSystem);
        SystemTask savedTask = systemTaskRepository.save(task);

        UpdateSystemTaskRequest request =
                new UpdateSystemTaskRequest("Run for 20 minutes");

        SystemTaskResponse response = systemTaskService.updateSystemTask(
                savedUser.getId(),
                savedTask.getId(),
                request
        );

        SystemTask updatedTask = systemTaskRepository.findById(savedTask.getId())
                .orElseThrow();

        assertThat(response.title()).isEqualTo("Run for 20 minutes");
        assertThat(response.completed()).isFalse();
        assertThat(updatedTask.getTitle()).isEqualTo("Run for 20 minutes");
        assertThat(updatedTask.isCompleted()).isFalse();
    }

    @Test
    void shouldNotUpdateTaskBelongingToAnotherUser() {
        User firstUser = new User();
        firstUser.setUsername("hati");
        firstUser.setEmail("hati@example.com");
        firstUser.setPassword("hashed-password");
        User savedFirstUser = userRepository.save(firstUser);

        User secondUser = new User();
        secondUser.setUsername("alex");
        secondUser.setEmail("alex@example.com");
        secondUser.setPassword("hashed-password");
        User savedSecondUser = userRepository.save(secondUser);

        Goal goal = new Goal();
        goal.setTitle("Improve health");
        goal.setDescription("Build healthy routines");
        goal.setUser(savedSecondUser);
        Goal savedGoal = goalRepository.save(goal);

        GoalSystem goalSystem = new GoalSystem();
        goalSystem.setTitle("Morning exercise");
        goalSystem.setFrequency(Frequency.DAILY);
        goalSystem.setGoal(savedGoal);
        GoalSystem savedGoalSystem = goalSystemRepository.save(goalSystem);

        SystemTask task = new SystemTask();
        task.setTitle("Run");
        task.setGoalSystem(savedGoalSystem);
        SystemTask savedTask = systemTaskRepository.save(task);

        UpdateSystemTaskRequest request =
                new UpdateSystemTaskRequest("Run for 20 minutes");

        assertThatThrownBy(() -> systemTaskService.updateSystemTask(
                savedFirstUser.getId(),
                savedTask.getId(),
                request
        ))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("SystemTask not found");

        assertThat(systemTaskRepository.findById(savedTask.getId()))
                .get()
                .extracting(SystemTask::getTitle)
                .isEqualTo("Run");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUnknownTask() {
        UpdateSystemTaskRequest request =
                new UpdateSystemTaskRequest("Run for 20 minutes");

        assertThatThrownBy(() -> systemTaskService.updateSystemTask(
                1L,
                999L,
                request
        ))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("SystemTask not found");
    }

    @Test
    void shouldCompleteTaskForUser() {
        User user = new User();
        user.setUsername("hati");
        user.setEmail("hati@example.com");
        user.setPassword("hashed-password");
        User savedUser = userRepository.save(user);

        Goal goal = new Goal();
        goal.setTitle("Improve health");
        goal.setDescription("Build healthy routines");
        goal.setUser(savedUser);
        Goal savedGoal = goalRepository.save(goal);

        GoalSystem goalSystem = new GoalSystem();
        goalSystem.setTitle("Morning exercise");
        goalSystem.setFrequency(Frequency.DAILY);
        goalSystem.setGoal(savedGoal);
        GoalSystem savedGoalSystem = goalSystemRepository.save(goalSystem);

        SystemTask task = new SystemTask();
        task.setTitle("Run for 20 minutes");
        task.setGoalSystem(savedGoalSystem);
        SystemTask savedTask = systemTaskRepository.save(task);

        CompleteSystemTaskResponse response = systemTaskService.completeSystemTask(
                savedUser.getId(),
                savedTask.getId()
        );

        SystemTask completedTask = systemTaskRepository.findById(savedTask.getId())
                .orElseThrow();

        assertThat(response.id()).isEqualTo(savedTask.getId());
        assertThat(response.completed()).isTrue();
        assertThat(completedTask.isCompleted()).isTrue();
    }

    @Test
    void shouldNotCompleteTaskBelongingToAnotherUser() {
        User firstUser = new User();
        firstUser.setUsername("hati");
        firstUser.setEmail("hati@example.com");
        firstUser.setPassword("hashed-password");
        User savedFirstUser = userRepository.save(firstUser);

        User secondUser = new User();
        secondUser.setUsername("alex");
        secondUser.setEmail("alex@example.com");
        secondUser.setPassword("hashed-password");
        User savedSecondUser = userRepository.save(secondUser);

        Goal goal = new Goal();
        goal.setTitle("Improve health");
        goal.setDescription("Build healthy routines");
        goal.setUser(savedSecondUser);
        Goal savedGoal = goalRepository.save(goal);

        GoalSystem goalSystem = new GoalSystem();
        goalSystem.setTitle("Morning exercise");
        goalSystem.setFrequency(Frequency.DAILY);
        goalSystem.setGoal(savedGoal);
        GoalSystem savedGoalSystem = goalSystemRepository.save(goalSystem);

        SystemTask task = new SystemTask();
        task.setTitle("Run for 20 minutes");
        task.setGoalSystem(savedGoalSystem);
        SystemTask savedTask = systemTaskRepository.save(task);

        assertThatThrownBy(() -> systemTaskService.completeSystemTask(
                savedFirstUser.getId(),
                savedTask.getId()
        ))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("SystemTask not found");

        assertThat(systemTaskRepository.findById(savedTask.getId()))
                .get()
                .extracting(SystemTask::isCompleted)
                .isEqualTo(false);
    }

    @Test
    void shouldThrowExceptionWhenCompletingUnknownTask() {
        assertThatThrownBy(() -> systemTaskService.completeSystemTask(1L, 999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("SystemTask not found");
    }
}
