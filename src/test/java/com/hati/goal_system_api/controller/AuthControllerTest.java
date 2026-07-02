package com.hati.goal_system_api.controller;

import com.hati.goal_system_api.dto.auth.RegisterRequest;
import com.hati.goal_system_api.dto.auth.RegisterResponse;
import com.hati.goal_system_api.exception.ResourceAlreadyExistsException;
import com.hati.goal_system_api.security.SecurityConfig;
import com.hati.goal_system_api.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void shouldRegisterUserWithoutAuthentication() throws Exception {
        RegisterResponse response = new RegisterResponse(
                1L,
                "hati",
                "hati@example.com",
                LocalDateTime.of(2026, 7, 2, 12, 0)
        );

        Mockito.when(authService.register(any(RegisterRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "hati",
                                  "email": "hati@example.com",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("hati")))
                .andExpect(jsonPath("$.email", is("hati@example.com")))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void shouldRejectRegistrationWithoutUsername() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "hati@example.com",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(authService);
    }

    @Test
    void shouldRejectRegistrationWithInvalidEmail() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "hati",
                                  "email": "invalid-email",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(authService);
    }

    @Test
    void shouldRejectRegistrationWithShortPassword() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "hati",
                                  "email": "hati@example.com",
                                  "password": "short"
                                }
                                """))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(authService);
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        Mockito.when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new ResourceAlreadyExistsException("Email already exists"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alex",
                                  "email": "hati@example.com",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Email already exists")));
    }
}
