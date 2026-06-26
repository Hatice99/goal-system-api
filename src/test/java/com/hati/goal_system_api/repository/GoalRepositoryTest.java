package com.hati.goal_system_api.repository;

import com.hati.goal_system_api.model.Goal;
import com.hati.goal_system_api.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class GoalRepositoryTest {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveGoalWithUser() {
        User user = new User();
        user.setUsername("hati");
        user.setEmail("hati@example.com");
        user.setPassword("hashed-password");
        User savedUser = userRepository.save(user);

        Goal goal = new Goal();
        goal.setTitle("Learn Spring Boot");
        goal.setDescription("Build a personal backend project");
        goal.setUser(savedUser);

        Goal savedGoal = goalRepository.save(goal);

        assertThat(savedGoal.getId()).isNotNull();
        assertThat(savedGoal.getCreatedAt()).isNotNull();
        assertThat(savedGoal.getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void shouldFindGoalsByUserId() {
        User user = new User();
        user.setUsername("hati");
        user.setEmail("hati@example.com");
        user.setPassword("hashed-password");
        User savedUser = userRepository.save(user);

        Goal firstGoal = new Goal();
        firstGoal.setTitle("Learn Spring Boot");
        firstGoal.setDescription("Build a personal backend project");
        firstGoal.setUser(savedUser);

        Goal secondGoal = new Goal();
        secondGoal.setTitle("Exercise consistently");
        secondGoal.setDescription("Build a fitness system");
        secondGoal.setUser(savedUser);

        goalRepository.save(firstGoal);
        goalRepository.save(secondGoal);

        List<Goal> goals = goalRepository.findByUserId(savedUser.getId());

        assertThat(goals)
                .extracting(Goal::getTitle)
                .containsExactlyInAnyOrder("Learn Spring Boot", "Exercise consistently");
    }
}
