package com.hati.goal_system_api.service;

import com.hati.goal_system_api.dto.auth.RegisterRequest;
import com.hati.goal_system_api.dto.auth.RegisterResponse;
import com.hati.goal_system_api.exception.ResourceAlreadyExistsException;
import com.hati.goal_system_api.model.User;
import com.hati.goal_system_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterUser() {
        RegisterRequest request = new RegisterRequest(
                "hati",
                "hati@example.com",
                "password123"
        );

        RegisterResponse response = authService.register(request);

        User savedUser = userRepository.findByEmail("hati@example.com")
                .orElseThrow();

        assertThat(response.id()).isEqualTo(savedUser.getId());
        assertThat(response.username()).isEqualTo("hati");
        assertThat(response.email()).isEqualTo("hati@example.com");
        assertThat(response.createdAt()).isNotNull();
        assertThat(savedUser.getPassword()).isNotEqualTo("password123");
        assertThat(passwordEncoder.matches("password123", savedUser.getPassword()))
                .isTrue();
    }

    @Test
    void shouldNotRegisterDuplicateEmail() {
        User existingUser = new User();
        existingUser.setUsername("hati");
        existingUser.setEmail("hati@example.com");
        existingUser.setPassword("hashed-password");
        userRepository.save(existingUser);

        RegisterRequest request = new RegisterRequest(
                "alex",
                "hati@example.com",
                "password123"
        );

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Email already exists");

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldNotRegisterDuplicateUsername() {
        User existingUser = new User();
        existingUser.setUsername("hati");
        existingUser.setEmail("hati@example.com");
        existingUser.setPassword("hashed-password");
        userRepository.save(existingUser);

        RegisterRequest request = new RegisterRequest(
                "hati",
                "alex@example.com",
                "password123"
        );

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Username already exists");

        assertThat(userRepository.count()).isEqualTo(1);
    }
}
