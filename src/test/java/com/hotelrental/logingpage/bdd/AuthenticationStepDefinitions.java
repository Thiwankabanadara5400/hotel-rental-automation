package com.hotelrental.logingpage.bdd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelrental.logingpage.dto.ApiResponse;
import com.hotelrental.logingpage.dto.LoginRequest;
import com.hotelrental.logingpage.dto.RegisterRequest;
import com.hotelrental.logingpage.dto.UserResponse;
import com.hotelrental.logingpage.model.User;
import com.hotelrental.logingpage.repository.UserRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationStepDefinitions extends CucumberSpringConfiguration {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private ResponseEntity<ApiResponse<UserResponse>> lastResponse;
    private String baseUrl;

    @Given("the hotel rental system is running")
    public void the_hotel_rental_system_is_running() {
        baseUrl = "http://localhost:" + port + "/api/auth";
        // System is running if this step passes
        assertNotNull(restTemplate);
    }

    @Given("I am on the registration page")
    public void i_am_on_the_registration_page() {
        // Simulate being on registration page
        registerRequest = new RegisterRequest();
    }

    @When("I fill in the registration form with valid details:")
    public void i_fill_in_the_registration_form_with_valid_details(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        registerRequest = new RegisterRequest(
            data.get("username"),
            data.get("email"),
            data.get("password"),
            data.get("country"),
            data.get("city"),
            data.get("phone")
        );
    }

    @When("I submit the registration form")
    public void i_submit_the_registration_form() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        
        HttpEntity<RegisterRequest> request = new HttpEntity<>(registerRequest, headers);
        
        lastResponse = restTemplate.exchange(
            baseUrl + "/register",
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<ApiResponse<UserResponse>>() {}
        );
    }

    @Then("I should see a success message {string}")
    public void i_should_see_a_success_message(String expectedMessage) {
        assertNotNull(lastResponse);
        assertNotNull(lastResponse.getBody());
        assertTrue(lastResponse.getBody().isSuccess());
        assertEquals(expectedMessage, lastResponse.getBody().getMessage());
    }

    @Then("my account should be created in the system")
    public void my_account_should_be_created_in_the_system() {
        assertNotNull(lastResponse.getBody().getData());
        UserResponse user = lastResponse.getBody().getData();
        assertNotNull(user.getId());
        assertEquals(registerRequest.getUsername(), user.getUsername());
        assertEquals(registerRequest.getEmail(), user.getEmail());
    }

    @Given("a user with username {string} already exists")
    public void a_user_with_username_already_exists(String username) {
        User existingUser = new User();
        existingUser.setUsername(username);
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        existingUser.setCountry("USA");
        existingUser.setCity("New York");
        existingUser.setPhone("1234567890");
        userRepository.save(existingUser);
    }

    @When("I try to register with username {string}")
    public void i_try_to_register_with_username(String username) {
        registerRequest = new RegisterRequest(
            username,
            "new@example.com",
            "password123",
            "USA",
            "Boston",
            "0987654321"
        );
        i_submit_the_registration_form();
    }

    @Then("I should see an error message {string}")
    public void i_should_see_an_error_message(String expectedMessage) {
        assertNotNull(lastResponse);
        assertNotNull(lastResponse.getBody());
        assertFalse(lastResponse.getBody().isSuccess());
        assertEquals(expectedMessage, lastResponse.getBody().getMessage());
    }

    @Then("my account should not be created")
    public void my_account_should_not_be_created() {
        assertNull(lastResponse.getBody().getData());
    }

    @Given("a user exists with credentials:")
    public void a_user_exists_with_credentials(DataTable dataTable) {
        Map<String, String> credentials = dataTable.asMap(String.class, String.class);
        
        User testUser = new User();
        testUser.setUsername(credentials.get("username"));
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode(credentials.get("password")));
        testUser.setCountry("USA");
        testUser.setCity("New York");
        testUser.setPhone("1234567890");
        userRepository.save(testUser);
    }

    @When("I login with username {string} and password {string}")
    public void i_login_with_username_and_password(String username, String password) {
        loginRequest = new LoginRequest(username, password);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);
        
        lastResponse = restTemplate.exchange(
            baseUrl + "/login",
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<ApiResponse<UserResponse>>() {}
        );
    }

    @Then("I should be logged in successfully")
    public void i_should_be_logged_in_successfully() {
        assertNotNull(lastResponse);
        assertNotNull(lastResponse.getBody());
        assertTrue(lastResponse.getBody().isSuccess());
        assertEquals("Login successful", lastResponse.getBody().getMessage());
    }

    @Then("I should see a welcome message")
    public void i_should_see_a_welcome_message() {
        assertNotNull(lastResponse.getBody().getData());
        UserResponse user = lastResponse.getBody().getData();
        assertNotNull(user.getUsername());
    }

    @Given("a user exists with username {string}")
    public void a_user_exists_with_username(String username) {
        User testUser = new User();
        testUser.setUsername(username);
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setCountry("USA");
        testUser.setCity("New York");
        testUser.setPhone("1234567890");
        userRepository.save(testUser);
    }

    @Then("I should not be logged in")
    public void i_should_not_be_logged_in() {
        assertNull(lastResponse.getBody().getData());
    }

    @When("I try to register with {string} as {string}")
    public void i_try_to_register_with_field_as_value(String field, String value) {
        registerRequest = new RegisterRequest();
        
        // Set default values
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setCountry("USA");
        registerRequest.setCity("New York");
        registerRequest.setPhone("1234567890");
        
        // Override the specific field
        switch (field) {
            case "email":
                registerRequest.setEmail(value);
                break;
            case "password":
                registerRequest.setPassword(value);
                break;
            case "phone":
                registerRequest.setPhone(value);
                break;
            case "username":
                registerRequest.setUsername(value);
                break;
        }
        
        i_submit_the_registration_form();
    }

    @Then("I should see validation error for {string}")
    public void i_should_see_validation_error_for_field(String field) {
        assertNotNull(lastResponse);
        assertNotNull(lastResponse.getBody());
        assertFalse(lastResponse.getBody().isSuccess());
        // The validation error message should indicate invalid data
        assertTrue(lastResponse.getBody().getMessage().contains("Invalid") || 
                  lastResponse.getBody().getMessage().contains("error"));
    }
}
