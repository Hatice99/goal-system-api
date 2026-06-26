package com.hati.goal_system_api.repository;

import com.hati.goal_system_api.model.Frequency;
import com.hati.goal_system_api.model.Goal;
import com.hati.goal_system_api.model.GoalSystem;
import com.hati.goal_system_api.model.SystemTask;
import com.hati.goal_system_api.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SystemTaskRepositoryTest {

    @Autowired
    private SystemTaskRepository systemTaskRepository;

    @Autowired
    private GoalSystemRepository goalSystemRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveSystemTaskWithGoalSystem() {
        GoalSystem savedGoalSystem = createGoalSystem();

        SystemTask systemTask = new SystemTask();
        systemTask.setTitle("Do 20 pushups");
        systemTask.setGoalSystem(savedGoalSystem);

        SystemTask savedSystemTask = systemTaskRepository.save(systemTask);

        assertThat(savedSystemTask.getId()).isNotNull();
        assertThat(savedSystemTask.isCompleted()).isFalse();
        assertThat(savedSystemTask.getGoalSystem().getId()).isEqualTo(savedGoalSystem.getId());
    }

    @Test
    void shouldFindSystemTasksByGoalSystemId() {
        GoalSystem savedGoalSystem = createGoalSystem();

        SystemTask firstTask = new SystemTask();
        firstTask.setTitle("Do 20 pushups");
        firstTask.setGoalSystem(savedGoalSystem);

        SystemTask secondTask = new SystemTask();
        secondTask.setTitle("Stretch for 10 minutes");
        secondTask.setGoalSystem(savedGoalSystem);

        systemTaskRepository.save(firstTask);
        systemTaskRepository.save(secondTask);

        List<SystemTask> tasks = systemTaskRepository.findByGoalSystemId(savedGoalSystem.getId());

        assertThat(tasks)
                .extracting(SystemTask::getTitle)
                .containsExactlyInAnyOrder("Do 20 pushups", "Stretch for 10 minutes");
    }

    private GoalSystem createGoalSystem() {
        User user = new User();
        user.setUsername("hati");
        user.setEmail("hati@example.com");
        user.setPassword("hashed-password");
        User savedUser = userRepository.save(user);

        Goal goal = new Goal();
        goal.setTitle("Get fit");
        goal.setDescription("Build a sustainable fitness routine");
        goal.setUser(savedUser);
        Goal savedGoal = goalRepository.save(goal);

        GoalSystem goalSystem = new GoalSystem();
        goalSystem.setTitle("Morning Workout");
        goalSystem.setFrequency(Frequency.DAILY);
        goalSystem.setGoal(savedGoal);
        return goalSystemRepository.save(goalSystem);
    }
}
