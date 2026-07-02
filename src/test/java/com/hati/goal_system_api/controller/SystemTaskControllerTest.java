package com.hati.goal_system_api.controller;

import com.hati.goal_system_api.dto.systemtask.CreateSystemTaskRequest;
import com.hati.goal_system_api.dto.systemtask.SystemTaskResponse;
import com.hati.goal_system_api.dto.systemtask.UpdateSystemTaskRequest;
import com.hati.goal_system_api.service.SystemTaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SystemTaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class SystemTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SystemTaskService systemTaskService;

    @Test
    void shouldAddTaskToUsersGoalSystem() throws Exception {
        SystemTaskResponse response = new SystemTaskResponse(
                1L,
                "Run for 20 minutes",
                false
        );

        Mockito.when(systemTaskService.createSystemTask(
                        eq(1L),
                        eq(10L),
                        any(CreateSystemTaskRequest.class)
                ))
                .thenReturn(response);

        mockMvc.perform(post("/systems/10/tasks")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Run for 20 minutes"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Run for 20 minutes")))
                .andExpect(jsonPath("$.completed", is(false)));
    }

    @Test
    void shouldRejectTaskWithoutTitle() throws Exception {
        mockMvc.perform(post("/systems/10/tasks")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(systemTaskService);
    }

    @Test
    void shouldGetTaskByIdForUser() throws Exception {
        SystemTaskResponse response = new SystemTaskResponse(
                1L,
                "Run for 20 minutes",
                false
        );

        Mockito.when(systemTaskService.getSystemTaskById(1L, 1L))
                .thenReturn(response);

        mockMvc.perform(get("/tasks/1")
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Run for 20 minutes")))
                .andExpect(jsonPath("$.completed", is(false)));
    }

    @Test
    void shouldUpdateTaskTitleForUser() throws Exception {
        SystemTaskResponse response = new SystemTaskResponse(
                1L,
                "Run for 20 minutes",
                false
        );

        Mockito.when(systemTaskService.updateSystemTask(
                        eq(1L),
                        eq(1L),
                        any(UpdateSystemTaskRequest.class)
                ))
                .thenReturn(response);

        mockMvc.perform(patch("/tasks/1")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Run for 20 minutes"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Run for 20 minutes")))
                .andExpect(jsonPath("$.completed", is(false)));
    }

    @Test
    void shouldRejectTaskUpdateWithoutTitle() throws Exception {
        mockMvc.perform(patch("/tasks/1")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(systemTaskService);
    }
}
