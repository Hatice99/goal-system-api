package com.hati.goal_system_api.service;

import com.hati.goal_system_api.dto.goalsystem.CreateGoalSystemRequest;
import com.hati.goal_system_api.dto.goalsystem.GoalSystemResponse;
import com.hati.goal_system_api.dto.goalsystem.UpdateGoalSystemRequest;
import com.hati.goal_system_api.exception.ResourceNotFoundException;
import com.hati.goal_system_api.model.Frequency;
import com.hati.goal_system_api.model.Goal;
import com.hati.goal_system_api.model.GoalSystem;
import com.hati.goal_system_api.model.User;
import com.hati.goal_system_api.repository.GoalRepository;
import com.hati.goal_system_api.repository.GoalSystemRepository;
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
class GoalSystemServiceTest {

    @Autowired
    private GoalSystemService goalSystemService;

    @Autowired
    private GoalSystemRepository goalSystemRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateGoalSystemForUsersGoal() {
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

        CreateGoalSystemRequest request = new CreateGoalSystemRequest(
                "Morning exercise",
                Frequency.DAILY,
                savedGoal.getId()
        );

        GoalSystemResponse response = goalSystemService.createGoalSystem(
                savedUser.getId(),
                request
        );

        GoalSystem savedGoalSystem = goalSystemRepository.findById(response.id())
                .orElseThrow();

        assertThat(response.title()).isEqualTo("Morning exercise");
        assertThat(response.frequency()).isEqualTo(Frequency.DAILY);
        assertThat(response.goalId()).isEqualTo(savedGoal.getId());
        assertThat(savedGoalSystem.getGoal().getId()).isEqualTo(savedGoal.getId());
    }

    @Test
    void shouldNotCreateGoalSystemForAnotherUsersGoal() {
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

        CreateGoalSystemRequest request = new CreateGoalSystemRequest(
                "Morning exercise",
                Frequency.DAILY,
                savedGoal.getId()
        );

        assertThatThrownBy(() -> goalSystemService.createGoalSystem(
                savedFirstUser.getId(),
                request
        ))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Goal not found");

        assertThat(goalSystemRepository.findAll()).isEmpty();
    }

    @Test
    void shouldGetGoalSystemsForUser() {
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

        GoalSystem firstGoalSystem = new GoalSystem();
        firstGoalSystem.setTitle("Morning exercise");
        firstGoalSystem.setFrequency(Frequency.DAILY);
        firstGoalSystem.setGoal(savedGoal);

        GoalSystem secondGoalSystem = new GoalSystem();
        secondGoalSystem.setTitle("Weekly meal planning");
        secondGoalSystem.setFrequency(Frequency.WEEKLY);
        secondGoalSystem.setGoal(savedGoal);

        goalSystemRepository.saveAll(List.of(firstGoalSystem, secondGoalSystem));

        List<GoalSystemResponse> responses =
                goalSystemService.getGoalSystemsForUser(savedUser.getId());

        assertThat(responses).hasSize(2);
        assertThat(responses)
                .extracting(GoalSystemResponse::title)
                .containsExactlyInAnyOrder(
                        "Morning exercise",
                        "Weekly meal planning"
                );
    }

    @Test
    void shouldOnlyGetGoalSystemsBelongingToUser() {
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
        firstUsersGoal.setTitle("Improve health");
        firstUsersGoal.setDescription("Build healthy routines");
        firstUsersGoal.setUser(savedFirstUser);
        Goal savedFirstUsersGoal = goalRepository.save(firstUsersGoal);

        Goal secondUsersGoal = new Goal();
        secondUsersGoal.setTitle("Learn Spring Boot");
        secondUsersGoal.setDescription("Build a backend project");
        secondUsersGoal.setUser(savedSecondUser);
        Goal savedSecondUsersGoal = goalRepository.save(secondUsersGoal);

        GoalSystem firstUsersGoalSystem = new GoalSystem();
        firstUsersGoalSystem.setTitle("Morning exercise");
        firstUsersGoalSystem.setFrequency(Frequency.DAILY);
        firstUsersGoalSystem.setGoal(savedFirstUsersGoal);

        GoalSystem secondUsersGoalSystem = new GoalSystem();
        secondUsersGoalSystem.setTitle("Study every evening");
        secondUsersGoalSystem.setFrequency(Frequency.DAILY);
        secondUsersGoalSystem.setGoal(savedSecondUsersGoal);

        goalSystemRepository.saveAll(List.of(
                firstUsersGoalSystem,
                secondUsersGoalSystem
        ));

        List<GoalSystemResponse> responses =
                goalSystemService.getGoalSystemsForUser(savedFirstUser.getId());

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().title()).isEqualTo("Morning exercise");
    }

    @Test
    void shouldUpdateGoalSystemForUser() {
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

        UpdateGoalSystemRequest request = new UpdateGoalSystemRequest(
                "Weekly exercise",
                Frequency.WEEKLY
        );

        GoalSystemResponse response = goalSystemService.updateGoalSystem(
                savedUser.getId(),
                savedGoalSystem.getId(),
                request
        );

        GoalSystem updatedGoalSystem = goalSystemRepository
                .findById(savedGoalSystem.getId())
                .orElseThrow();

        assertThat(response.title()).isEqualTo("Weekly exercise");
        assertThat(response.frequency()).isEqualTo(Frequency.WEEKLY);
        assertThat(updatedGoalSystem.getTitle()).isEqualTo("Weekly exercise");
        assertThat(updatedGoalSystem.getFrequency()).isEqualTo(Frequency.WEEKLY);
    }

    @Test
    void shouldOnlyUpdateProvidedGoalSystemFields() {
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

        UpdateGoalSystemRequest request = new UpdateGoalSystemRequest(
                "Evening exercise",
                null
        );

        GoalSystemResponse response = goalSystemService.updateGoalSystem(
                savedUser.getId(),
                savedGoalSystem.getId(),
                request
        );

        assertThat(response.title()).isEqualTo("Evening exercise");
        assertThat(response.frequency()).isEqualTo(Frequency.DAILY);
    }

    @Test
    void shouldOnlyUpdateGoalSystemFrequency() {
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
        goalSystem.setTitle("Keep this title");
        goalSystem.setFrequency(Frequency.DAILY);
        goalSystem.setGoal(savedGoal);
        GoalSystem savedGoalSystem = goalSystemRepository.save(goalSystem);

        UpdateGoalSystemRequest request = new UpdateGoalSystemRequest(
                null,
                Frequency.WEEKLY
        );

        GoalSystemResponse response = goalSystemService.updateGoalSystem(
                savedUser.getId(),
                savedGoalSystem.getId(),
                request
        );

        assertThat(response.title()).isEqualTo("Keep this title");
        assertThat(response.frequency()).isEqualTo(Frequency.WEEKLY);
    }

    @Test
    void shouldNotUpdateGoalSystemBelongingToAnotherUser() {
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

        UpdateGoalSystemRequest request = new UpdateGoalSystemRequest(
                "Changed by another user",
                Frequency.WEEKLY
        );

        assertThatThrownBy(() -> goalSystemService.updateGoalSystem(
                savedFirstUser.getId(),
                savedGoalSystem.getId(),
                request
        ))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("GoalSystem not found");

        GoalSystem unchangedGoalSystem = goalSystemRepository
                .findById(savedGoalSystem.getId())
                .orElseThrow();

        assertThat(unchangedGoalSystem.getTitle()).isEqualTo("Morning exercise");
        assertThat(unchangedGoalSystem.getFrequency()).isEqualTo(Frequency.DAILY);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingGoalSystem() {
        UpdateGoalSystemRequest request = new UpdateGoalSystemRequest(
                "Weekly exercise",
                Frequency.WEEKLY
        );

        assertThatThrownBy(() -> goalSystemService.updateGoalSystem(
                1L,
                999L,
                request
        ))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("GoalSystem not found");
    }
}
