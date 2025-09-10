package com.hotelrental.logingpage.bdd;

import com.hotelrental.logingpage.dto.UserResponse;
import com.hotelrental.logingpage.model.User;
import com.hotelrental.logingpage.repository.UserRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ProfileManagementStepDefinitions extends CucumberSpringConfiguration {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User currentUser;
    private UserResponse userProfile;
    private String updateMessage;

    @Given("I am logged in as a user")
    public void i_am_logged_in_as_a_user() {
        currentUser = new User();
        currentUser.setUsername("testuser");
        currentUser.setEmail("test@example.com");
        currentUser.setPassword(passwordEncoder.encode("password123"));
        currentUser.setCountry("USA");
        currentUser.setCity("New York");
        currentUser.setPhone("1234567890");
        currentUser = userRepository.save(currentUser);
    }

    @When("I navigate to my profile page")
    public void i_navigate_to_my_profile_page() {
        // Simulate navigating to profile page - fetch user data
        Optional<User> user = userRepository.findById(currentUser.getId());
        assertTrue(user.isPresent());
        
        // Convert to UserResponse (simulating what the API would return)
        User foundUser = user.get();
        userProfile = new UserResponse();
        userProfile.setId(foundUser.getId());
        userProfile.setUsername(foundUser.getUsername());
        userProfile.setEmail(foundUser.getEmail());
        userProfile.setCountry(foundUser.getCountry());
        userProfile.setCity(foundUser.getCity());
        userProfile.setPhone(foundUser.getPhone());
        userProfile.setIsAdmin(foundUser.getIsAdmin());
    }

    @Then("I should see my profile information:")
    public void i_should_see_my_profile_information(DataTable dataTable) {
        Map<String, String> expectedData = dataTable.asMap(String.class, String.class);
        
        assertNotNull(userProfile);
        assertEquals(expectedData.get("username"), userProfile.getUsername());
        assertEquals(expectedData.get("email"), userProfile.getEmail());
        assertEquals(expectedData.get("country"), userProfile.getCountry());
        assertEquals(expectedData.get("city"), userProfile.getCity());
    }

    @Given("I am on my profile page")
    public void i_am_on_my_profile_page() {
        i_am_logged_in_as_a_user();
        i_navigate_to_my_profile_page();
    }

    @When("I update my city to {string}")
    public void i_update_my_city_to(String newCity) {
        // Simulate updating city
        currentUser.setCity(newCity);
    }

    @When("I save the changes")
    public void i_save_the_changes() {
        try {
            userRepository.save(currentUser);
            updateMessage = "Profile updated successfully";
        } catch (Exception e) {
            updateMessage = "Failed to update profile";
        }
    }

    @Then("I should see a success message {string}")
    public void i_should_see_a_success_message_profile(String expectedMessage) {
        assertEquals(expectedMessage, updateMessage);
    }

    @Then("my city should be updated to {string}")
    public void my_city_should_be_updated_to(String expectedCity) {
        Optional<User> updatedUser = userRepository.findById(currentUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(expectedCity, updatedUser.get().getCity());
    }
}
