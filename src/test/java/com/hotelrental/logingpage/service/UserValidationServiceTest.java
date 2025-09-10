/*package com.hotelrental.logingpage.service;

import com.hotelrental.logingpage.dto.RegisterRequest;
import com.hotelrental.logingpage.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidationServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserValidationService userValidationService;

    @BeforeEach
    void setUp() {
        userValidationService = new UserValidationService(userRepository);
    }

    @Test
    void shouldReturnTrueForValidRegistrationRequest() {
        // Given
        RegisterRequest request = new RegisterRequest(
            "john_doe", 
            "john@example.com", 
            "password123", 
            "USA", 
            "New York", 
            "1234567890"
        );
        
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);

        // When
        boolean isValid = userValidationService.isValidRegistrationRequest(request);

        // Then
        assertTrue(isValid);
    }

    @Test
    void shouldReturnFalseForDuplicateUsername() {
        // Given
        RegisterRequest request = new RegisterRequest(
            "existing_user", 
            "new@example.com", 
            "password123", 
            "USA", 
            "New York", 
            "1234567890"
        );
        
        when(userRepository.existsByUsername("existing_user")).thenReturn(true);

        // When
        boolean isValid = userValidationService.isValidRegistrationRequest(request);

        // Then
        assertFalse(isValid);
    }

    @Test
    void shouldReturnFalseForInvalidEmail() {
        // Given
        RegisterRequest request = new RegisterRequest(
            "john_doe", 
            "invalid-email", 
            "password123", 
            "USA", 
            "New York", 
            "1234567890"
        );

        // When
        boolean isValid = userValidationService.isValidRegistrationRequest(request);

        // Then
        assertFalse(isValid);
    }

    @Test
    void shouldReturnFalseForShortPassword() {
        // Given
        RegisterRequest request = new RegisterRequest(
            "john_doe", 
            "john@example.com", 
            "123", 
            "USA", 
            "New York", 
            "1234567890"
        );

        // When
        boolean isValid = userValidationService.isValidRegistrationRequest(request);

        // Then
        assertFalse(isValid);
    }

    @Test
    void shouldReturnFalseForInvalidPhoneNumber() {
        // Given
        RegisterRequest request = new RegisterRequest(
            "john_doe", 
            "john@example.com", 
            "password123", 
            "USA", 
            "New York", 
            "invalid-phone"
        );

        // When
        boolean isValid = userValidationService.isValidRegistrationRequest(request);

        // Then
        assertFalse(isValid);
    }
}*/

package com.hotelrental.logingpage.service;

import com.hotelrental.logingpage.dto.RegisterRequest;
import com.hotelrental.logingpage.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidationServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserValidationService userValidationService;

    @BeforeEach
    void setUp() {
        userValidationService = new UserValidationService(userRepository);
    }

    @Test
    void shouldReturnNullForValidRegistrationRequest() {
        // Given
        RegisterRequest request = new RegisterRequest(
                "john_doe",
                "john@example.com",
                "password123",
                "USA",
                "New York",
                "1234567890"
        );

        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);

        // When
        String validationError = userValidationService.validateRegistrationRequest(request);

        // Then
        assertNull(validationError);
    }

    @Test
    void shouldReturnTrueForValidRegistrationRequestBackwardCompatibility() {
        // Given
        RegisterRequest request = new RegisterRequest(
                "john_doe",
                "john@example.com",
                "password123",
                "USA",
                "New York",
                "1234567890"
        );

        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);

        // When
        boolean isValid = userValidationService.isValidRegistrationRequest(request);

        // Then
        assertTrue(isValid);
    }

    @Test
    void shouldReturnErrorForDuplicateUsername() {
        // Given
        RegisterRequest request = new RegisterRequest(
                "existing_user",
                "new@example.com",
                "password123",
                "USA",
                "New York",
                "1234567890"
        );

        when(userRepository.existsByUsername("existing_user")).thenReturn(true);

        // When
        String validationError = userValidationService.validateRegistrationRequest(request);

        // Then
        assertEquals("Username already exists", validationError);
        assertFalse(userValidationService.isValidRegistrationRequest(request));
    }

    @Test
    void shouldReturnErrorForDuplicateEmail() {
        // Given
        RegisterRequest request = new RegisterRequest(
                "john_doe",
                "existing@example.com",
                "password123",
                "USA",
                "New York",
                "1234567890"
        );

        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When
        String validationError = userValidationService.validateRegistrationRequest(request);

        // Then
        assertEquals("Email already registered", validationError);
        assertFalse(userValidationService.isValidRegistrationRequest(request));
    }

    @Test
    void shouldReturnErrorForInvalidEmail() {
        // Given
        RegisterRequest request = new RegisterRequest(
                "john_doe",
                "invalid-email",
                "password123",
                "USA",
                "New York",
                "1234567890"
        );

        // When
        String validationError = userValidationService.validateRegistrationRequest(request);

        // Then
        assertEquals("Invalid email format", validationError);
        assertFalse(userValidationService.isValidRegistrationRequest(request));
    }

    @Test
    void shouldReturnErrorForShortPassword() {
        // Given
        RegisterRequest request = new RegisterRequest(
                "john_doe",
                "john@example.com",
                "123",
                "USA",
                "New York",
                "1234567890"
        );

        // When
        String validationError = userValidationService.validateRegistrationRequest(request);

        // Then
        assertEquals("Password must be at least 6 characters long", validationError);
        assertFalse(userValidationService.isValidRegistrationRequest(request));
    }

    @Test
    void shouldReturnErrorForShortUsername() {
        // Given
        RegisterRequest request = new RegisterRequest(
                "ab",
                "john@example.com",
                "password123",
                "USA",
                "New York",
                "1234567890"
        );

        // When
        String validationError = userValidationService.validateRegistrationRequest(request);

        // Then
        assertEquals("Username must be at least 3 characters long", validationError);
        assertFalse(userValidationService.isValidRegistrationRequest(request));
    }

    @Test
    void shouldReturnErrorForInvalidPhoneNumber() {
        // Given
        RegisterRequest request = new RegisterRequest(
                "john_doe",
                "john@example.com",
                "password123",
                "USA",
                "New York",
                "invalid-phone"
        );

        // When
        String validationError = userValidationService.validateRegistrationRequest(request);

        // Then
        assertEquals("Invalid phone number format", validationError);
        assertFalse(userValidationService.isValidRegistrationRequest(request));
    }

    @Test
    void shouldReturnErrorForNullRequest() {
        // When
        String validationError = userValidationService.validateRegistrationRequest(null);

        // Then
        assertEquals("Registration request cannot be null", validationError);
        assertFalse(userValidationService.isValidRegistrationRequest(null));
    }

    @Test
    void shouldReturnErrorForEmptyUsername() {
        // Given
        RegisterRequest request = new RegisterRequest(
                "",
                "john@example.com",
                "password123",
                "USA",
                "New York",
                "1234567890"
        );

        // When
        String validationError = userValidationService.validateRegistrationRequest(request);

        // Then
        assertEquals("Username is required", validationError);
    }

    @Test
    void shouldReturnErrorForEmptyEmail() {
        // Given
        RegisterRequest request = new RegisterRequest(
                "john_doe",
                "",
                "password123",
                "USA",
                "New York",
                "1234567890"
        );

        // When
        String validationError = userValidationService.validateRegistrationRequest(request);

        // Then
        assertEquals("Email is required", validationError);
    }

    @Test
    void shouldReturnErrorForEmptyPassword() {
        // Given
        RegisterRequest request = new RegisterRequest(
                "john_doe",
                "john@example.com",
                "",
                "USA",
                "New York",
                "1234567890"
        );

        // When
        String validationError = userValidationService.validateRegistrationRequest(request);

        // Then
        assertEquals("Password is required", validationError);
    }

    @Test
    void shouldReturnErrorForEmptyPhone() {
        // Given
        RegisterRequest request = new RegisterRequest(
                "john_doe",
                "john@example.com",
                "password123",
                "USA",
                "New York",
                ""
        );

        // When
        String validationError = userValidationService.validateRegistrationRequest(request);

        // Then
        assertEquals("Phone number is required", validationError);
    }
}
