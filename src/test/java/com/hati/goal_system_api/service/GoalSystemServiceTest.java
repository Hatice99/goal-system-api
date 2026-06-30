package com.hati.goal_system_api.service;

import com.hati.goal_system_api.dto.goalsystem.CreateGoalSystemRequest;
import com.hati.goal_system_api.dto.goalsystem.GoalSystemResponse;
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
}
