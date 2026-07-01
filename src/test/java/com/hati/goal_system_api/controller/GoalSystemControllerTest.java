package com.hati.goal_system_api.controller;

import com.hati.goal_system_api.dto.goalsystem.CreateGoalSystemRequest;
import com.hati.goal_system_api.dto.goalsystem.GoalSystemResponse;
import com.hati.goal_system_api.dto.goalsystem.UpdateGoalSystemRequest;
import com.hati.goal_system_api.model.Frequency;
import com.hati.goal_system_api.service.GoalSystemService;
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

@WebMvcTest(GoalSystemController.class)
@AutoConfigureMockMvc(addFilters = false)
class GoalSystemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GoalSystemService goalSystemService;

    @Test
    void shouldCreateGoalSystemForUsersGoal() throws Exception {
        GoalSystemResponse response = new GoalSystemResponse(
                1L,
                "Morning exercise",
                Frequency.DAILY,
                10L
        );

        Mockito.when(goalSystemService.createGoalSystem(
                        eq(1L),
                        any(CreateGoalSystemRequest.class)
                ))
                .thenReturn(response);

        mockMvc.perform(post("/systems")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Morning exercise",
                                  "frequency": "DAILY",
                                  "goalId": 10
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Morning exercise")))
                .andExpect(jsonPath("$.frequency", is("DAILY")))
                .andExpect(jsonPath("$.goalId", is(10)));
    }

    @Test
    void shouldRejectGoalSystemWithoutTitle() throws Exception {
        mockMvc.perform(post("/systems")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "frequency": "DAILY",
                                  "goalId": 10
                                }
                                """))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(goalSystemService);
    }

    @Test
    void shouldRejectGoalSystemWithoutFrequency() throws Exception {
        mockMvc.perform(post("/systems")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Morning exercise",
                                  "goalId": 10
                                }
                                """))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(goalSystemService);
    }

    @Test
    void shouldRejectGoalSystemWithoutGoalId() throws Exception {
        mockMvc.perform(post("/systems")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Morning exercise",
                                  "frequency": "DAILY"
                                }
                                """))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(goalSystemService);
    }

    @Test
    void shouldRejectUnknownFrequency() throws Exception {
        mockMvc.perform(post("/systems")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Morning exercise",
                                  "frequency": "YEARLY",
                                  "goalId": 10
                                }
                                """))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(goalSystemService);
    }

    @Test
    void shouldGetGoalSystemsForUser() throws Exception {
        List<GoalSystemResponse> responses = List.of(
                new GoalSystemResponse(
                        1L,
                        "Morning exercise",
                        Frequency.DAILY,
                        10L
                ),
                new GoalSystemResponse(
                        2L,
                        "Weekly meal planning",
                        Frequency.WEEKLY,
                        10L
                )
        );

        Mockito.when(goalSystemService.getGoalSystemsForUser(1L))
                .thenReturn(responses);

        mockMvc.perform(get("/systems")
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Morning exercise")))
                .andExpect(jsonPath("$[0].frequency", is("DAILY")))
                .andExpect(jsonPath("$[0].goalId", is(10)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Weekly meal planning")))
                .andExpect(jsonPath("$[1].frequency", is("WEEKLY")))
                .andExpect(jsonPath("$[1].goalId", is(10)));
    }

    @Test
    void shouldUpdateGoalSystemForUser() throws Exception {
        GoalSystemResponse response = new GoalSystemResponse(
                1L,
                "Weekly exercise",
                Frequency.WEEKLY,
                10L
        );

        Mockito.when(goalSystemService.updateGoalSystem(
                        eq(1L),
                        eq(1L),
                        any(UpdateGoalSystemRequest.class)
                ))
                .thenReturn(response);

        mockMvc.perform(patch("/systems/1")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Weekly exercise",
                                  "frequency": "WEEKLY"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Weekly exercise")))
                .andExpect(jsonPath("$.frequency", is("WEEKLY")))
                .andExpect(jsonPath("$.goalId", is(10)));
    }

    @Test
    void shouldDeleteGoalSystemForUser() throws Exception {
        mockMvc.perform(delete("/systems/1")
                        .header("X-User-Id", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(goalSystemService).deleteGoalSystem(1L, 1L);
    }
}
