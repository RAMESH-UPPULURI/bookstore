package bookstore.stepdefs;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import com.github.javafaker.Faker;
import java.util.HashMap;
import java.util.Map;

public class BookstoreApiSteps {
    private Response response;
    private String accessToken;
    private int createdBookId;
    private Faker faker = new Faker();
    private String currentEmail;
    private String currentPassword;
    private Map<String, String> userCredentials = new HashMap<>();

    static {
        RestAssured.baseURI = "http://localhost:8000";
    }

    @When("I send a GET request to {string}")
    public void i_send_a_get_request_to(String endpoint) {
        System.out.println("[STEP] Sending GET request to: " + endpoint);
        response = RestAssured.get(endpoint);
    }

    @Then("the response code should be {int}")
    public void the_response_code_should_be(int code) {
        System.out.println("[STEP] Asserting response code: expected=" + code + ", actual=" + response.getStatusCode());
        if (response.getStatusCode() != code) {
            System.out.println("[ERROR] Response body: " + response.asString());
        }
        Assert.assertEquals(response.getStatusCode(), code, "Expected status code " + code + " but got " + response.getStatusCode() + ". Response: " + response.asString());
    }

    @And("the response should contain {string} with value {string}")
    public void the_response_should_contain_with_value(String key, String value) {
        System.out.println("[STEP] Asserting response contains key '" + key + "' with value '" + value + "'");
        String actual = response.jsonPath().getString(key);
        if (!value.equals(actual)) {
            System.out.println("[ERROR] Response body: " + response.asString());
        }
        Assert.assertEquals(actual, value, "Expected key '" + key + "' to have value '" + value + "' but got '" + actual + "'. Response: " + response.asString());
    }

    @Then("the response should contain {string}")
    public void the_response_should_contain(String key) {
        System.out.println("[STEP] Asserting response contains key '" + key + "'");
        Assert.assertNotNull(response.jsonPath().getString(key), "Response does not contain key: " + key);
    }

    @When("I sign up with a random email and password {string}")
    public void i_sign_up_with_random_email_and_password(String password) {
        currentEmail = faker.internet().emailAddress();
        currentPassword = password;

        Map<String, String> body = new HashMap<>();
        body.put("email", currentEmail);
        body.put("password", currentPassword);

        System.out.println("[STEP] Signing up with random email: " + currentEmail);
        System.out.println("[REQUEST BODY] " + body);
        response = RestAssured.given().contentType("application/json").body(body).post("/signup");

        // Store credentials for later use
        userCredentials.put("email", currentEmail);
        userCredentials.put("password", currentPassword);
    }

    @When("I sign up with email {string} and password {string}")
    public void i_sign_up_with_email_and_password(String email, String password) {
        // Handle RANDOM_EMAIL placeholder
        if ("RANDOM_EMAIL".equals(email)) {
            email = faker.internet().emailAddress();
            currentEmail = email;
        }

        // Handle RANDOM_PASSWORD placeholder
        if ("RANDOM_PASSWORD".equals(password)) {
            password = faker.internet().password(8, 16);
            currentPassword = password;
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        System.out.println("[STEP] Signing up with email: " + email);
        System.out.println("[REQUEST BODY] " + body);
        response = RestAssured.given().contentType("application/json").body(body).post("/signup");

        // Store credentials for later use
        userCredentials.put("email", email);
        userCredentials.put("password", password);
    }

    @When("I login with email {string} and password {string}")
    public void i_login_with_email_and_password(String email, String password) {
        // Handle RANDOM_EMAIL placeholder - use stored email if available
        if ("RANDOM_EMAIL".equals(email)) {
            email = userCredentials.getOrDefault("email", faker.internet().emailAddress());
        }

        // Handle RANDOM_PASSWORD placeholder - use stored password if available
        if ("RANDOM_PASSWORD".equals(password)) {
            password = userCredentials.getOrDefault("password", faker.internet().password(8, 16));
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        System.out.println("[STEP] Logging in with email: " + email);
        System.out.println("[REQUEST BODY] " + body);
        response = RestAssured.given().contentType("application/json").body(body).post("/login");

        if (response.getStatusCode() == 200) {
            accessToken = response.jsonPath().getString("access_token");
            System.out.println("[STEP] Received access token: " + accessToken);
        }
    }

    @When("I login with a random email and password")
    public void i_login_with_random_email_and_password() {
        String randomEmail = faker.internet().emailAddress();
        String randomPassword = faker.internet().password(8, 16);

        i_login_with_email_and_password(randomEmail, randomPassword);
    }

    @Given("I am logged in with random credentials")
    public void i_am_logged_in_with_random_credentials() {
        // Generate random credentials
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(8, 16);

        // Sign up first
        i_sign_up_with_email_and_password(email, password);
        Assert.assertEquals(response.getStatusCode(), 200, "Signup failed");

        // Then login
        i_login_with_email_and_password(email, password);
        Assert.assertNotNull(accessToken, "Login failed - no access token received");
    }

    @Given("I am logged in as {string} with password {string}")
    public void i_am_logged_in_as_with_password(String email, String password) {
        // Handle placeholders
        if ("RANDOM_EMAIL".equals(email)) {
            email = faker.internet().emailAddress();
        }
        if ("RANDOM_PASSWORD".equals(password)) {
            password = faker.internet().password(8, 16);
        }

        // Always sign up the user before login to ensure the user exists
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        System.out.println("[STEP] (Auto) Signing up with email: " + email);
        RestAssured.given().contentType("application/json").body(body).post("/signup");

        System.out.println("[STEP] Logging in as: " + email);
        i_login_with_email_and_password(email, password);
        Assert.assertNotNull(accessToken);
    }

    @When("I create a book with title {string} and author {string}")
    public void i_create_a_book_with_title_and_author(String title, String author) {
        // Handle random placeholders
        if ("RANDOM_TITLE".equals(title)) {
            title = faker.book().title();
        }
        if ("RANDOM_AUTHOR".equals(author)) {
            author = faker.book().author(); // Fixed: was setting title instead of author
        }

        // Create complete book payload matching the working API structure
        Map<String, Object> body = new HashMap<>();
        body.put("name", title); // Changed from "title" to "name" to match working API
        body.put("author", author);
        body.put("published_year", faker.number().numberBetween(1900, 2024)); // Added missing field
        body.put("book_summary", faker.lorem().sentence(10)); // Added missing field

        System.out.println("[STEP] Creating book with name: " + title + ", author: " + author);
        System.out.println("[REQUEST BODY] " + body);
        response = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body(body)
                .post("/books/");

        if (response.getStatusCode() == 200) {
            createdBookId = response.jsonPath().getInt("id");
            System.out.println("[STEP] Created book with id: " + createdBookId);
        } else {
            System.out.println("[ERROR] Failed to create book. Response: " + response.asString());
        }
    }

    @When("I create a book with random title and author")
    public void i_create_a_book_with_random_title_and_author() {
        String randomTitle = faker.book().title();
        String randomAuthor = faker.book().author();

        i_create_a_book_with_title_and_author(randomTitle, randomAuthor);
    }

    @When("I get the created book by id")
    public void i_get_the_created_book_by_id() {
        System.out.println("[STEP] Getting book by id: " + createdBookId);
        response = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .get("/books/" + createdBookId);
    }

    @When("I update the book title to {string}")
    public void i_update_the_book_title_to(String newTitle) {
        if ("RANDOM_TITLE".equals(newTitle)) {
            newTitle = faker.book().title();
        }

        Map<String, Object> body = new HashMap<>();
        body.put("name", newTitle); // Changed from "title" to "name" to match API

        System.out.println("[STEP] Updating book id " + createdBookId + " name to: " + newTitle);
        System.out.println("[REQUEST BODY] " + body);
        response = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body(body)
                .put("/books/" + createdBookId);
    }

    @When("I update the book with random title")
    public void i_update_the_book_with_random_title() {
        String randomTitle = faker.book().title();
        i_update_the_book_title_to(randomTitle);
    }

    @When("I delete the book")
    public void i_delete_the_book() {
        System.out.println("[STEP] Deleting book id: " + createdBookId);
        response = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .delete("/books/" + createdBookId);
    }

    @When("I get the deleted book by id")
    public void i_get_the_deleted_book_by_id() {
        System.out.println("[STEP] Getting (deleted) book by id: " + createdBookId);
        i_get_the_created_book_by_id();
    }

    @When("I try to create a book without authentication")
    public void i_try_to_create_book_without_authentication() {
        String title = faker.book().title();
        String author = faker.book().author();

        Map<String, Object> body = new HashMap<>();
        body.put("name", title); // Changed from "title" to "name"
        body.put("author", author);
        body.put("published_year", faker.number().numberBetween(1900, 2024)); // Added missing field
        body.put("book_summary", faker.lorem().sentence(10)); // Added missing field

        System.out.println("[STEP] Creating book without auth - name: " + title + ", author: " + author);
        System.out.println("[REQUEST BODY] " + body);
        response = RestAssured.given()
                .contentType("application/json")
                .body(body)
                .post("/books/");
    }

    // Utility method to get current faker instance (for debugging)
    public Faker getFaker() {
        return faker;
    }

    // Method to get stored credentials (for debugging)
    public Map<String, String> getStoredCredentials() {
        return userCredentials;
    }
}