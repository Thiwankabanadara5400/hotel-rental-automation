Feature: User Profile Management
  As a registered user
  I want to manage my profile information
  So that I can keep my account details up to date

  Background:
    Given I am logged in as a user

  Scenario: View user profile
    When I navigate to my profile page
    Then I should see my profile information:
      | username | testuser         |
      | email    | test@example.com |
      | country  | USA             |
      | city     | New York        |

  Scenario: Update profile successfully
    Given I am on my profile page
    When I update my city to "Boston"
    And I save the changes
    Then I should see a success message "Profile updated successfully"
    And my city should be updated to "Boston"