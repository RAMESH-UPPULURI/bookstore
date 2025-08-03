@api @bookstore
Feature: Enhanced Bookstore API Testing with Faker Data
  As a QA Engineer
  I want to test the Bookstore API with dynamic, realistic test data
  So that I can ensure the system works reliably with various data combinations

#  Background:
#    Given the API is accessible at the configured endpoint

  @smoke @health
  Scenario: API Health Check Verification
    When I send a GET request to "/health"
    Then the response code should be 200
    And the response should contain "status" with value "up"

  @smoke @auth @positive
  Scenario: Successful User Registration with Random Data
    When I sign up with email "RANDOM_EMAIL" and password "RANDOM_PASSWORD"
    Then the response code should be 200
    And the response should contain "message" with value "User created successfully"

  @smoke @auth @positive
  Scenario: Successful User Login with Generated Credentials
    When I sign up with a random email and password "testpass123"
    Then the response code should be 200
    When I login with email "RANDOM_EMAIL" and password "testpass123"
    Then the response code should be 200
    And the response should contain "access_token"

  @regression @auth @negative
  Scenario: Prevent Duplicate User Registration
    When I sign up with email "RANDOM_EMAIL" and password "testpass123"
    Then the response code should be 200
    When I sign up with email "RANDOM_EMAIL" and password "testpass123"
    Then the response code should be 400
    And the response should contain "detail" with value "Email already registered"

  @regression @auth @negative
  Scenario Outline: Login Validation with Invalid Credentials
    When I login with email "<email>" and password "<password>"
    Then the response code should be 400
    And the response should contain "detail" with value "Incorrect email or password"

    Examples:
      | email        | password    |
      | RANDOM_EMAIL | wrongpass   |
      |              | testpass123 |
      | RANDOM_EMAIL |             |

  @smoke @books @crud @positive
  Scenario: Complete Book Lifecycle Management
    Given I am logged in with random credentials
    When I create a book with random title and author
    Then the response code should be 200
    And the response should contain "id"
    And the response should contain "name"
    And the response should contain "author"
    And the response should contain "published_year"
    And the response should contain "book_summary"
    When I get the created book by id
    Then the response code should be 200
    And the response should contain "id"
    When I update the book with random title
    Then the response code should be 200
    And the response should contain "name"
    When I delete the book
    Then the response code should be 200
    And the response should contain "message" with value "Book deleted successfully"
    When I get the deleted book by id
    Then the response code should be 404
    And the response should contain "detail" with value "Book not found"

  @regression @books @negative
  Scenario: Unauthorized Book Operations
    When I try to create a book without authentication
    Then the response code should be 403
    And the response should contain "detail" with value "Not authenticated"

  @regression @books @boundary
  Scenario Outline: Book Creation with Various Data Types
    Given I am logged in as "RANDOM_EMAIL" with password "RANDOM_PASSWORD"
    When I create a book with title "<title>" and author "<author>"
    Then the response code should be <expectedStatus>

    Examples:
      | title                                                                                         | author            | expectedStatus |
      | RANDOM_TITLE                                                                                  | RANDOM_AUTHOR     | 200            |
      | Very Long Title That Exceeds Normal Length Limits And Should Test The API Boundary Conditions | RANDOM_AUTHOR     | 200            |
      | Short                                                                                         | RANDOM_AUTHOR     | 200            |
      | Title With Numbers 123                                                                        | Author123         | 200            |
      | Title-With-Dashes                                                                             | Author_Underscore | 200            |

  @data @multiple
  Scenario: Multiple User Registration Stress Test
    When I sign up with email "RANDOM_EMAIL" and password "RANDOM_PASSWORD"
    Then the response code should be 200
    When I sign up with email "RANDOM_EMAIL" and password "RANDOM_PASSWORD"
    Then the response code should be 200
    When I sign up with email "RANDOM_EMAIL" and password "RANDOM_PASSWORD"
    Then the response code should be 200

  @regression @books @multiple
  Scenario: Multiple Book Operations by Single User
    Given I am logged in with random credentials
    When I create a book with title "RANDOM_TITLE" and author "RANDOM_AUTHOR"
    Then the response code should be 200
    When I create a book with title "RANDOM_TITLE" and author "RANDOM_AUTHOR"
    Then the response code should be 200
    When I create a book with title "RANDOM_TITLE" and author "RANDOM_AUTHOR"
    Then the response code should be 200




  @security @negative
  Scenario Outline: Security Testing with Malicious Input
    Given I am logged in with random credentials
    When I create a book with title "<maliciousTitle>" and author "<maliciousAuthor>"
    Then the response code should be <expectedStatus>

    Examples:
      | maliciousTitle                | maliciousAuthor              | expectedStatus |
      | <script>alert('xss')</script> | Normal Author                | 200            |
      | '; DROP TABLE books; --       | SQL Injection Author         | 200            |
      | Normal Title                  | <img src=x onerror=alert(1)> | 200            |

  @performance @load
  Scenario: Basic Load Testing Simulation
    Given I am logged in with random credentials
    When I create a book with random title and author
    And I get the created book by id
    And I update the book with random title
    And I get the created book by id
    And I delete the book
    ##Then all operations should complete within acceptable time limits

  @edge-cases @boundary
  Scenario: Empty and Null Value Handling
    Given I am logged in with random credentials
    When I create a book with title "" and author ""
    Then the response code should be 400

  @integration @workflow
  Scenario: End-to-End User Journey
    When I sign up with email "RANDOM_EMAIL" and password "RANDOM_PASSWORD"
    Then the response code should be 200
    When I login with email "RANDOM_EMAIL" and password "RANDOM_PASSWORD"
    Then the response code should be 200
    When I create a book with title "My Journey Book" and author "Journey Author"
    Then the response code should be 200
    When I get the created book by id
    Then the response code should be 200
    When I update the book title to "My Updated Journey Book"
    Then the response code should be 200
    When I get the created book by id
    Then the response code should be 200
    And the response should contain "title" with value "My Updated Journey Book"
    When I delete the book
    Then the response code should be 200
    When I get the deleted book by id
    Then the response code should be 404