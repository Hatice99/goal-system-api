package com.hati.goal_system_api.repository;

import com.hati.goal_system_api.model.Frequency;
import com.hati.goal_system_api.model.Goal;
import com.hati.goal_system_api.model.GoalSystem;
import com.hati.goal_system_api.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class GoalSystemRepositoryTest {

    @Autowired
    private GoalSystemRepository goalSystemRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveGoalSystemWithGoal() {
        Goal savedGoal = createGoal();

        GoalSystem goalSystem = new GoalSystem();
        goalSystem.setTitle("Morning Workout");
        goalSystem.setFrequency(Frequency.DAILY);
        goalSystem.setGoal(savedGoal);

        GoalSystem savedGoalSystem = goalSystemRepository.save(goalSystem);

        assertThat(savedGoalSystem.getId()).isNotNull();
        assertThat(savedGoalSystem.getCreatedAt()).isNotNull();
        assertThat(savedGoalSystem.getFrequency()).isEqualTo(Frequency.DAILY);
        assertThat(savedGoalSystem.getGoal().getId()).isEqualTo(savedGoal.getId());
    }

    @Test
    void shouldFindGoalSystemsByGoalId() {
        Goal savedGoal = createGoal();

        GoalSystem firstGoalSystem = new GoalSystem();
        firstGoalSystem.setTitle("Morning Workout");
        firstGoalSystem.setFrequency(Frequency.DAILY);
        firstGoalSystem.setGoal(savedGoal);

        GoalSystem secondGoalSystem = new GoalSystem();
        secondGoalSystem.setTitle("Weekly Review");
        secondGoalSystem.setFrequency(Frequency.WEEKLY);
        secondGoalSystem.setGoal(savedGoal);

        goalSystemRepository.save(firstGoalSystem);
        goalSystemRepository.save(secondGoalSystem);

        List<GoalSystem> goalSystems = goalSystemRepository.findByGoalId(savedGoal.getId());

        assertThat(goalSystems)
                .extracting(GoalSystem::getTitle)
                .containsExactlyInAnyOrder("Morning Workout", "Weekly Review");
    }

    private Goal createGoal() {
        User user = new User();
        user.setUsername("hati");
        user.setEmail("hati@example.com");
        user.setPassword("hashed-password");
        User savedUser = userRepository.save(user);

        Goal goal = new Goal();
        goal.setTitle("Get fit");
        goal.setDescription("Build a sustainable fitness routine");
        goal.setUser(savedUser);
        return goalRepository.save(goal);
    }
}
