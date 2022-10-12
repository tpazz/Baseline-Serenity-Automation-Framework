# Basic test to ensure that everything is working
@theInternet
Feature: The Internet Examples

  Scenario: Basic Test
    Given I navigate to "https://the-internet.herokuapp.com"
    Then Verify the heading reads "Welcome to the-internet"