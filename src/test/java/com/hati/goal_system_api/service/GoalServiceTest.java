package com.hati.goal_system_api.service;

import com.hati.goal_system_api.dto.goal.CreateGoalRequest;
import com.hati.goal_system_api.dto.goal.GoalResponse;
import com.hati.goal_system_api.dto.goal.UpdateGoalRequest;
import com.hati.goal_system_api.exception.ResourceNotFoundException;
import com.hati.goal_system_api.model.Goal;
import com.hati.goal_system_api.model.User;
import com.hati.goal_system_api.repository.GoalRepository;
import com.hati.goal_system_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class GoalServiceTest {

    @Autowired
    private GoalService goalService;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateGoalForUser() {
        User user = new User();
        user.setUsername("hati");
        user.setEmail("hati@example.com");
        user.setPassword("hashed-password");
        User savedUser = userRepository.save(user);

        CreateGoalRequest request = new CreateGoalRequest(
                "Learn Spring Boot",
                "Build a personal backend project"
        );

        GoalResponse response = goalService.createGoal(savedUser.getId(), request);

        List<Goal> goals = goalRepository.findByUserId(savedUser.getId());

        assertThat(response.id()).isNotNull();
        assertThat(response.title()).isEqualTo("Learn Spring Boot");
        assertThat(response.description()).isEqualTo("Build a personal backend project");
        assertThat(goals).hasSize(1);
        assertThat(goals.getFirst().getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        CreateGoalRequest request = new CreateGoalRequest(
                "Learn Spring Boot",
                "Build a personal backend project"
        );

        assertThatThrownBy(() -> goalService.createGoal(999L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void shouldGetGoalsForUser() {
        User user = new User();
        user.setUsername("hati");
        user.setEmail("hati@example.com");
        user.setPassword("hashed-password");
        User savedUser = userRepository.save(user);

        Goal firstGoal = new Goal();
        firstGoal.setTitle("Learn Spring Boot");
        firstGoal.setDescription("Build a backend project");
        firstGoal.setUser(savedUser);

        Goal secondGoal = new Goal();
        secondGoal.setTitle("Exercise regularly");
        secondGoal.setDescription("Improve physical health");
        secondGoal.setUser(savedUser);

        goalRepository.saveAll(List.of(firstGoal, secondGoal));

        List<GoalResponse> responses = goalService.getGoalsForUser(savedUser.getId());

        assertThat(responses).hasSize(2);
        assertThat(responses)
                .extracting(GoalResponse::title)
                .containsExactlyInAnyOrder("Learn Spring Boot", "Exercise regularly");
    }

    @Test
    void shouldOnlyGetGoalsBelongingToUser() {
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

        Goal firstUsersGoal = new Goal();
        firstUsersGoal.setTitle("Learn Spring Boot");
        firstUsersGoal.setDescription("Build a backend project");
        firstUsersGoal.setUser(savedFirstUser);

        Goal secondUsersGoal = new Goal();
        secondUsersGoal.setTitle("Learn PostgreSQL");
        secondUsersGoal.setDescription("Practice database queries");
        secondUsersGoal.setUser(savedSecondUser);

        goalRepository.saveAll(List.of(firstUsersGoal, secondUsersGoal));

        List<GoalResponse> responses = goalService.getGoalsForUser(savedFirstUser.getId());

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().title()).isEqualTo("Learn Spring Boot");
    }

    @Test
    void shouldGetGoalByIdForUser() {
        User user = new User();
        user.setUsername("hati");
        user.setEmail("hati@example.com");
        user.setPassword("hashed-password");
        User savedUser = userRepository.save(user);

        Goal goal = new Goal();
        goal.setTitle("Learn Spring Boot");
        goal.setDescription("Build a backend project");
        goal.setUser(savedUser);
        Goal savedGoal = goalRepository.save(goal);

        GoalResponse response = goalService.getGoalById(
                savedUser.getId(),
                savedGoal.getId()
        );

        assertThat(response.id()).isEqualTo(savedGoal.getId());
        assertThat(response.title()).isEqualTo("Learn Spring Boot");
        assertThat(response.description()).isEqualTo("Build a backend project");
    }

    @Test
    void shouldNotGetGoalBelongingToAnotherUser() {
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
        secondUsersGoal.setTitle("Learn PostgreSQL");
        secondUsersGoal.setDescription("Practice database queries");
        secondUsersGoal.setUser(savedSecondUser);
        Goal savedGoal = goalRepository.save(secondUsersGoal);

        assertThatThrownBy(() -> goalService.getGoalById(
                savedFirstUser.getId(),
                savedGoal.getId()
        ))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Goal not found");
    }

    @Test
    void shouldThrowExceptionWhenGoalDoesNotExist() {
        assertThatThrownBy(() -> goalService.getGoalById(1L, 999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Goal not found");
    }

    @Test
    void shouldUpdateGoalForUser() {
        User user = new User();
        user.setUsername("hati");
        user.setEmail("hati@example.com");
        user.setPassword("hashed-password");
        User savedUser = userRepository.save(user);

        Goal goal = new Goal();
        goal.setTitle("Learn Java");
        goal.setDescription("Study Java fundamentals");
        goal.setUser(savedUser);
        Goal savedGoal = goalRepository.save(goal);

        UpdateGoalRequest request = new UpdateGoalRequest(
                "Learn Spring Boot",
                "Build a backend project"
        );

        GoalResponse response = goalService.updateGoal(
                savedUser.getId(),
                savedGoal.getId(),
                request
        );

        Goal updatedGoal = goalRepository.findById(savedGoal.getId()).orElseThrow();

        assertThat(response.title()).isEqualTo("Learn Spring Boot");
        assertThat(response.description()).isEqualTo("Build a backend project");
        assertThat(updatedGoal.getTitle()).isEqualTo("Learn Spring Boot");
        assertThat(updatedGoal.getDescription()).isEqualTo("Build a backend project");
    }

    @Test
    void shouldOnlyUpdateProvidedGoalFields() {
        User user = new User();
        user.setUsername("hati");
        user.setEmail("hati@example.com");
        user.setPassword("hashed-password");
        User savedUser = userRepository.save(user);

        Goal goal = new Goal();
        goal.setTitle("Learn Java");
        goal.setDescription("Keep this description");
        goal.setUser(savedUser);
        Goal savedGoal = goalRepository.save(goal);

        UpdateGoalRequest request = new UpdateGoalRequest(
                "Learn Spring Boot",
                null
        );

        GoalResponse response = goalService.updateGoal(
                savedUser.getId(),
                savedGoal.getId(),
                request
        );

        assertThat(response.title()).isEqualTo("Learn Spring Boot");
        assertThat(response.description()).isEqualTo("Keep this description");
    }

    @Test
    void shouldNotUpdateGoalBelongingToAnotherUser() {
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
        secondUsersGoal.setTitle("Learn PostgreSQL");
        secondUsersGoal.setDescription("Practice database queries");
        secondUsersGoal.setUser(savedSecondUser);
        Goal savedGoal = goalRepository.save(secondUsersGoal);

        UpdateGoalRequest request = new UpdateGoalRequest(
                "Changed by another user",
                null
        );

        assertThatThrownBy(() -> goalService.updateGoal(
                savedFirstUser.getId(),
                savedGoal.getId(),
                request
        ))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Goal not found");

        Goal unchangedGoal = goalRepository.findById(savedGoal.getId()).orElseThrow();
        assertThat(unchangedGoal.getTitle()).isEqualTo("Learn PostgreSQL");
    }

    @Test
    void shouldUpdateOnlyGoalDescription() {
        User user = new User();
        user.setUsername("hati");
        user.setEmail("hati@example.com");
        user.setPassword("hashed-password");
        User savedUser = userRepository.save(user);

        Goal goal = new Goal();
        goal.setTitle("Keep this title");
        goal.setDescription("Old description");
        goal.setUser(savedUser);
        Goal savedGoal = goalRepository.save(goal);

        UpdateGoalRequest request = new UpdateGoalRequest(
                null,
                "New description"
        );

        GoalResponse response = goalService.updateGoal(
                savedUser.getId(),
                savedGoal.getId(),
                request
        );

        assertThat(response.title()).isEqualTo("Keep this title");
        assertThat(response.description()).isEqualTo("New description");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingGoal() {
        UpdateGoalRequest request = new UpdateGoalRequest(
                "Learn Spring Boot",
                null
        );

        assertThatThrownBy(() -> goalService.updateGoal(1L, 999L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Goal not found");
    }
}
