package com.hati.goal_system_api.controller;

import com.hati.goal_system_api.dto.goal.CreateGoalRequest;
import com.hati.goal_system_api.dto.goal.GoalResponse;
import com.hati.goal_system_api.dto.goal.UpdateGoalRequest;
import com.hati.goal_system_api.exception.ResourceNotFoundException;
import com.hati.goal_system_api.service.GoalService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GoalController.class)
@AutoConfigureMockMvc(addFilters = false)
class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GoalService goalService;

    @Test
    void shouldCreateGoal() throws Exception {
        CreateGoalRequest request = new CreateGoalRequest(
                "Learn Spring Boot",
                "Build a personal backend project"
        );

        GoalResponse response = new GoalResponse(
                1L,
                "Learn Spring Boot",
                "Build a personal backend project"
        );

        Mockito.when(goalService.createGoal(eq(1L), any(CreateGoalRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/goals")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Learn Spring Boot",
                                  "description": "Build a personal backend project"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Learn Spring Boot")))
                .andExpect(jsonPath("$.description", is("Build a personal backend project")));
    }

    @Test
    void shouldGetGoalsForUser() throws Exception {
        List<GoalResponse> responses = List.of(
                new GoalResponse(1L, "Learn Spring Boot", "Build a backend project"),
                new GoalResponse(2L, "Exercise regularly", "Improve physical health")
        );

        Mockito.when(goalService.getGoalsForUser(1L))
                .thenReturn(responses);

        mockMvc.perform(get("/goals")
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Learn Spring Boot")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Exercise regularly")));
    }

    @Test
    void shouldGetGoalByIdForUser() throws Exception {
        GoalResponse response = new GoalResponse(
                1L,
                "Learn Spring Boot",
                "Build a backend project"
        );

        Mockito.when(goalService.getGoalById(1L, 1L))
                .thenReturn(response);

        mockMvc.perform(get("/goals/1")
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Learn Spring Boot")))
                .andExpect(jsonPath("$.description", is("Build a backend project")));
    }

    @Test
    void shouldReturnNotFoundWhenGoalIsUnavailable() throws Exception {
        Mockito.when(goalService.getGoalById(1L, 999L))
                .thenThrow(new ResourceNotFoundException("Goal not found"));

        mockMvc.perform(get("/goals/999")
                        .header("X-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Goal not found")));
    }

    @Test
    void shouldUpdateGoalForUser() throws Exception {
        GoalResponse response = new GoalResponse(
                1L,
                "Learn Spring Boot",
                "Build a backend project"
        );

        Mockito.when(goalService.updateGoal(
                        eq(1L),
                        eq(1L),
                        any(UpdateGoalRequest.class)
                ))
                .thenReturn(response);

        mockMvc.perform(patch("/goals/1")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Learn Spring Boot",
                                  "description": "Build a backend project"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Learn Spring Boot")))
                .andExpect(jsonPath("$.description", is("Build a backend project")));
    }

    @Test
    void shouldDeleteGoalForUser() throws Exception {
        mockMvc.perform(delete("/goals/1")
                        .header("X-User-Id", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(goalService).deleteGoal(1L, 1L);
    }
}
