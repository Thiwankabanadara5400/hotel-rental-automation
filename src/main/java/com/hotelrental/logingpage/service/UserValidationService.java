/*package com.hotelrental.logingpage.service;

import com.hotelrental.logingpage.dto.RegisterRequest;
import com.hotelrental.logingpage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final UserRepository userRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[\\d\\s\\-\\+\\(\\)]{10,15}$"
    );

    public boolean isValidRegistrationRequest(RegisterRequest request) {
        if (request == null) return false;

        // Check for null or empty fields
        if (isNullOrEmpty(request.getUsername()) ||
                isNullOrEmpty(request.getEmail()) ||
                isNullOrEmpty(request.getPassword()) ||
                isNullOrEmpty(request.getPhone())) {
            return false;
        }

        // Check password length
        if (request.getPassword().length() < 6) {
            return false;
        }

        // Check email format
        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            return false;
        }

        // Check phone format
        if (!PHONE_PATTERN.matcher(request.getPhone()).matches()) {
            return false;
        }

        // Check for existing username
        if (userRepository.existsByUsername(request.getUsername())) {
            return false;
        }

        // Check for existing email
        if (userRepository.existsByEmail(request.getEmail())) {
            return false;
        }

        return true;
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}*/
package com.hotelrental.logingpage.service;

import com.hotelrental.logingpage.dto.RegisterRequest;
import com.hotelrental.logingpage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {

    private final UserRepository userRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[\\d\\s\\-\\+\\(\\)]{10,15}$"
    );

    public String validateRegistrationRequest(RegisterRequest request) {
        if (request == null) {
            return "Registration request cannot be null";
        }

        // Check for null or empty fields
        if (isNullOrEmpty(request.getUsername())) {
            return "Username is required";
        }

        if (isNullOrEmpty(request.getEmail())) {
            return "Email is required";
        }

        if (isNullOrEmpty(request.getPassword())) {
            return "Password is required";
        }

        if (isNullOrEmpty(request.getPhone())) {
            return "Phone number is required";
        }

        // Check username length
        if (request.getUsername().length() < 3) {
            return "Username must be at least 3 characters long";
        }

        // Check password length
        if (request.getPassword().length() < 6) {
            return "Password must be at least 6 characters long";
        }

        // Check email format
        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            return "Invalid email format";
        }

        // Check phone format
        if (!PHONE_PATTERN.matcher(request.getPhone()).matches()) {
            return "Invalid phone number format";
        }

        // Check for existing username
        if (userRepository.existsByUsername(request.getUsername())) {
            return "Username already exists";
        }

        // Check for existing email
        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already registered";
        }

        return null; // No validation errors
    }

    // Keep the old method for backward compatibility
    public boolean isValidRegistrationRequest(RegisterRequest request) {
        return validateRegistrationRequest(request) == null;
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
