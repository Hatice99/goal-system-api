package com.hati.goal_system_api.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SystemTaskTest {

    @Test
    void shouldNotBeCompletedByDefault() {
        SystemTask task = new SystemTask();

        assertThat(task.isCompleted()).isFalse();
    }

    @Test
    void shouldCompleteTask() {
        SystemTask task = new SystemTask();

        task.complete();

        assertThat(task.isCompleted()).isTrue();
    }
}