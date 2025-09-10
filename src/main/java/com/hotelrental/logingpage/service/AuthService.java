/*
package com.hotelrental.logingpage.service;

import com.hotelrental.logingpage.dto.*;
import com.hotelrental.logingpage.model.User;
import com.hotelrental.logingpage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidationService userValidationService;

    public ApiResponse<UserResponse> login(LoginRequest loginRequest) {
        try {
            log.info("Login attempt for username: {}", loginRequest.getUsername());

            Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

            if (userOptional.isEmpty()) {
                return ApiResponse.error("Invalid username or password");
            }

            User user = userOptional.get();

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ApiResponse.error("Invalid username or password");
            }

            UserResponse userResponse = mapToUserResponse(user);
            return ApiResponse.success("Login successful", userResponse);

        } catch (Exception e) {
            log.error("Error during login: ", e);
            return ApiResponse.error("An error occurred during login");
        }
    }

    public ApiResponse<UserResponse> register(RegisterRequest registerRequest) {
        try {
            log.info("Registration attempt for username: {}", registerRequest.getUsername());

            if (!userValidationService.isValidRegistrationRequest(registerRequest)) {
                return ApiResponse.error("Invalid registration data");
            }

            User user = new User(
                    registerRequest.getUsername(),
                    registerRequest.getEmail(),
                    passwordEncoder.encode(registerRequest.getPassword()),
                    registerRequest.getCountry(),
                    registerRequest.getCity(),
                    registerRequest.getPhone()
            );

            User savedUser = userRepository.save(user);
            UserResponse userResponse = mapToUserResponse(savedUser);

            return ApiResponse.success("Registration successful", userResponse);

        } catch (Exception e) {
            log.error("Error during registration: ", e);
            return ApiResponse.error("An error occurred during registration");
        }
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCountry(user.getCountry());
        response.setCity(user.getCity());
        response.setPhone(user.getPhone());
        response.setIsAdmin(user.getIsAdmin());
        response.setImg(user.getImg());
        return response;
    }
}*/

package com.hotelrental.logingpage.service;

import com.hotelrental.logingpage.dto.*;
import com.hotelrental.logingpage.model.User;
import com.hotelrental.logingpage.repository.UserRepository;
import com.hotelrental.logingpage.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidationService userValidationService;
    private final JwtTokenProvider jwtTokenProvider;

    public Map<String, Object> login(LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("Login attempt for username: {}", loginRequest.getUsername());

            Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

            if (userOptional.isEmpty()) {
                log.warn("User not found: {}", loginRequest.getUsername());
                response.put("success", false);
                response.put("message", "Invalid username or password");
                return response;
            }

            User user = userOptional.get();

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                log.warn("Invalid password for user: {}", loginRequest.getUsername());
                response.put("success", false);
                response.put("message", "Invalid username or password");
                return response;
            }

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(user.getUsername());

            log.info("Login successful for user: {}", loginRequest.getUsername());

            response.put("success", true);
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("user", mapToUserResponse(user));

        } catch (Exception e) {
            log.error("Error during login for user: {}", loginRequest.getUsername(), e);
            response.put("success", false);
            response.put("message", "An error occurred during login");
        }

        return response;
    }

    public Map<String, Object> register(RegisterRequest registerRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("Registration attempt for username: {}", registerRequest.getUsername());

            // Validate registration request
            String validationError = userValidationService.validateRegistrationRequest(registerRequest);
            if (validationError != null) {
                log.warn("Registration validation failed for {}: {}", registerRequest.getUsername(), validationError);
                response.put("success", false);
                response.put("message", validationError);
                return response;
            }

            // Create new user
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setCountry(registerRequest.getCountry());
            user.setCity(registerRequest.getCity());
            user.setPhone(registerRequest.getPhone());
            user.setIsAdmin(false);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            User savedUser = userRepository.save(user);

            log.info("Registration successful for user: {}", registerRequest.getUsername());

            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("user", mapToUserResponse(savedUser));

        } catch (Exception e) {
            log.error("Error during registration for user: {}", registerRequest.getUsername(), e);
            response.put("success", false);
            response.put("message", "An error occurred during registration");
        }

        return response;
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCountry(user.getCountry());
        response.setCity(user.getCity());
        response.setPhone(user.getPhone());
        response.setIsAdmin(user.getIsAdmin());
        response.setImg(user.getImg());
        return response;
    }
}