package com.hotelrental.logingpage.service;

import com.hotelrental.logingpage.dto.LoginRequest;
import com.hotelrental.logingpage.dto.RegisterRequest;
import com.hotelrental.logingpage.model.User;
import com.hotelrental.logingpage.repository.UserRepository;
import com.hotelrental.logingpage.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserValidationService userValidationService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, userValidationService, jwtTokenProvider);
    }

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        // Given
        LoginRequest loginRequest = new LoginRequest("john_doe", "password123");
        User mockUser = createMockUser();
        String mockToken = "mock.jwt.token";

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
        when(jwtTokenProvider.generateToken("john_doe")).thenReturn(mockToken);

        // When
        Map<String, Object> response = authService.login(loginRequest);

        // Then
        assertTrue((Boolean) response.get("success"));
        assertEquals("Login successful", response.get("message"));
        assertEquals(mockToken, response.get("token"));
        assertNotNull(response.get("user"));
    }

    @Test
    void shouldFailLoginWithInvalidUsername() {
        // Given
        LoginRequest loginRequest = new LoginRequest("nonexistent", "password123");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        Map<String, Object> response = authService.login(loginRequest);

        // Then
        assertFalse((Boolean) response.get("success"));
        assertEquals("Invalid username or password", response.get("message"));
        assertNull(response.get("token"));
    }

    @Test
    void shouldFailLoginWithInvalidPassword() {
        // Given
        LoginRequest loginRequest = new LoginRequest("john_doe", "wrong_password");
        User mockUser = createMockUser();

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrong_password", "encoded_password")).thenReturn(false);

        // When
        Map<String, Object> response = authService.login(loginRequest);

        // Then
        assertFalse((Boolean) response.get("success"));
        assertEquals("Invalid username or password", response.get("message"));
        assertNull(response.get("token"));
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest(
                "new_user", "new@example.com", "password123", "USA", "New York", "1234567890"
        );
        User savedUser = createMockUser();
        savedUser.setUsername("new_user");
        savedUser.setEmail("new@example.com");

        when(userValidationService.validateRegistrationRequest(registerRequest)).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        Map<String, Object> response = authService.register(registerRequest);

        // Then
        assertTrue((Boolean) response.get("success"));
        assertEquals("Registration successful", response.get("message"));
        assertNotNull(response.get("user"));
    }

    @Test
    void shouldFailRegistrationWithInvalidData() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest(
                "existing_user", "invalid-email", "123", "USA", "New York", "invalid-phone"
        );

        when(userValidationService.validateRegistrationRequest(registerRequest))
                .thenReturn("Invalid registration data");

        // When
        Map<String, Object> response = authService.register(registerRequest);

        // Then
        assertFalse((Boolean) response.get("success"));
        assertEquals("Invalid registration data", response.get("message"));
        assertNull(response.get("user"));
    }

    @Test
    void shouldHandleLoginException() {
        // Given
        LoginRequest loginRequest = new LoginRequest("john_doe", "password123");

        when(userRepository.findByUsername("john_doe")).thenThrow(new RuntimeException("Database error"));

        // When
        Map<String, Object> response = authService.login(loginRequest);

        // Then
        assertFalse((Boolean) response.get("success"));
        assertEquals("An error occurred during login", response.get("message"));
    }

    @Test
    void shouldHandleRegistrationException() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest(
                "new_user", "new@example.com", "password123", "USA", "New York", "1234567890"
        );

        when(userValidationService.validateRegistrationRequest(registerRequest)).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenThrow(new RuntimeException("Encoding error"));

        // When
        Map<String, Object> response = authService.register(registerRequest);

        // Then
        assertFalse((Boolean) response.get("success"));
        assertEquals("An error occurred during registration", response.get("message"));
    }

    private User createMockUser() {
        User user = new User();
        user.setId("1");
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setPassword("encoded_password");
        user.setCountry("USA");
        user.setCity("New York");
        user.setPhone("1234567890");
        user.setIsAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
}
