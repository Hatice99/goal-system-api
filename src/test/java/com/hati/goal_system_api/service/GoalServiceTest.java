package com.hati.goal_system_api.service;

import com.hati.goal_system_api.dto.goal.CreateGoalRequest;
import com.hati.goal_system_api.dto.goal.GoalResponse;
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
}
