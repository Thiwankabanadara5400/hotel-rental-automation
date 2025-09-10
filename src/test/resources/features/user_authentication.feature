Feature: User Authentication
  As a user of the hotel rental system
  I want to be able to register and login
  So that I can access the application features

  Background:
    Given the hotel rental system is running

  Scenario: Successful user registration
    Given I am on the registration page
    When I fill in the registration form with valid details:
      | username | john_doe           |
      | email    | john@example.com   |
      | password | password123        |
      | country  | USA               |
      | city     | New York          |
      | phone    | +1234567890       |
    And I submit the registration form
    Then I should see a success message "Registration successful"
    And my account should be created in the system

  Scenario: Registration with duplicate username
    Given a user with username "existing_user" already exists
    When I try to register with username "existing_user"
    Then I should see an error message "Invalid registration data"
    And my account should not be created

  Scenario: Successful user login
    Given a user exists with credentials:
      | username | testuser    |
      | password | password123 |
    When I login with username "testuser" and password "password123"
    Then I should be logged in successfully
    And I should see a welcome message

  Scenario: Failed login with invalid credentials
    Given a user exists with username "testuser"
    When I login with username "testuser" and password "wrongpassword"
    Then I should see an error message "Invalid username or password"
    And I should not be logged in

  Scenario Outline: Registration input validation
    When I try to register with "<field>" as "<value>"
    Then I should see validation error for "<field>"

    Examples:
      | field    | value           |
      | email    | invalid-email   |
      | password | 123             |
      | phone    | invalid-phone   |
      | username |                 |
