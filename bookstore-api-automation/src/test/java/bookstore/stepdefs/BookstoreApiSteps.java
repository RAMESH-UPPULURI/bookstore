package bookstore.stepdefs;

import bookstore.config.APIConfig;
import bookstore.utils.FakerDataUtils;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import java.util.HashMap;
import java.util.Map;

public class BookstoreApiSteps {
    private Response response;
    private String accessToken;
    private int createdBookId;
    private String currentEmail;
    private String currentPassword;
    private Map<String, String> userCredentials = new HashMap<>();
    private long operationStartTime;
    private long operationEndTime;

    static {
        APIConfig.resetRestAssured();
        APIConfig.enableConsoleLogging(); // Enable console logging for requests and responses
        APIConfig.enableFileLogging("target/bookstore_api_test.log"); // Enable file logging for requests and responses
        APIConfig.printEnvironmentInfo(); // Print environment information
    }

    @When("I send a GET request to Health Endpoint")
    public void iSendAGETRequestToHealthEndpoint() {
        System.out.println("[STEP] Sending GET request to: " + APIConfig.HEALTH_ENDPOINT);
        response = RestAssured.get(APIConfig.HEALTH_ENDPOINT);
    }

    @Then("the response code should be {int}")
    public void the_response_code_should_be(int code) {
        System.out.println("[STEP] Asserting response code: expected=" + code + ", actual=" + response.getStatusCode());
        if (response.getStatusCode() != code) {
            System.out.println("[ERROR] Response body: " + response.asString());
        }
        Assert.assertEquals("Expected status code " + code + " but got " + response.getStatusCode() + ". Response: " + response.asString(),
                code, response.getStatusCode());
    }

    @And("the response should contain {string} with value {string}")
    public void the_response_should_contain_with_value(String key, String value) {
        System.out.println("[STEP] Asserting response contains key '" + key + "' with value '" + value + "'");
        String actual = response.jsonPath().getString(key);
        if (!value.equals(actual)) {
            System.out.println("[ERROR] Response body: " + response.asString());
        }
        Assert.assertEquals("Expected key '" + key + "' to have value '" + value + "' but got '" + actual + "'. Response: " + response.asString(),
                value, actual);
    }

    @Then("the response should contain {string}")
    public void the_response_should_contain(String key) {
        System.out.println("[STEP] Asserting response contains key '" + key + "'");
        Assert.assertNotNull("Response does not contain key: " + key, response.jsonPath().getString(key));
    }

    @When("I sign up with a random email and password {string}")
    public void i_sign_up_with_random_email_and_password(String password) {
        currentEmail = FakerDataUtils.generateRandomEmail();
        currentPassword = password;

        Map<String, String> body = new HashMap<>();
        body.put("email", currentEmail);
        body.put("password", currentPassword);

        System.out.println("[STEP] Signing up with random email: " + currentEmail);
        System.out.println("[REQUEST BODY] " + body);
        response = RestAssured.given()
                .contentType(APIConfig.CONTENT_TYPE_JSON)
                .body(body)
                .post(APIConfig.SIGNUP_ENDPOINT);

        // Store credentials for later use
        userCredentials.put("email", currentEmail);
        userCredentials.put("password", currentPassword);
    }

    @When("I sign up with email {string} and password {string}")
    public void i_sign_up_with_email_and_password(String email, String password) {
        // Handle RANDOM_EMAIL placeholder
        if ("RANDOM_EMAIL".equals(email)) {
            email = FakerDataUtils.generateRandomEmail();
            currentEmail = email;
        }

        // Handle RANDOM_PASSWORD placeholder
        if ("RANDOM_PASSWORD".equals(password)) {
            password = FakerDataUtils.generateRandomPassword();
            currentPassword = password;
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        System.out.println("[STEP] Signing up with email: " + email);
        System.out.println("[REQUEST BODY] " + body);
        response = RestAssured.given()
                .contentType(APIConfig.CONTENT_TYPE_JSON)
                .body(body)
                .post(APIConfig.SIGNUP_ENDPOINT);

        // Store credentials for later use
        userCredentials.put("email", email);
        userCredentials.put("password", password);
    }

    @When("I sign up with same registered email and password {string}")
    public void iSignUpWithSameRegisteredEmailAndPassword(String password) {
        Map<String, String> body = new HashMap<>();
        body.put("email", currentEmail);
        body.put("password", password);

        System.out.println("[STEP] Signing up with email: " + currentEmail);
        System.out.println("[REQUEST BODY] " + body);
        response = RestAssured.given()
                .contentType(APIConfig.CONTENT_TYPE_JSON)
                .body(body)
                .post(APIConfig.SIGNUP_ENDPOINT);

        // Store credentials for later use
        userCredentials.put("email", currentEmail);
        userCredentials.put("password", password);
    }

    @When("I login with email {string} and password {string}")
    public void i_login_with_email_and_password(String email, String password) {
        // Handle RANDOM_EMAIL placeholder - use stored email if available
        if ("RANDOM_EMAIL".equals(email)) {
            email = userCredentials.getOrDefault("email", FakerDataUtils.generateRandomEmail());
        }

        // Handle RANDOM_PASSWORD placeholder - use stored password if available
        if ("RANDOM_PASSWORD".equals(password)) {
            password = userCredentials.getOrDefault("password", FakerDataUtils.generateRandomPassword());
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        System.out.println("[STEP] Logging in with email: " + email);
        System.out.println("[REQUEST BODY] " + body);
        response = RestAssured.given()
                .contentType(APIConfig.CONTENT_TYPE_JSON)
                .body(body)
                .post(APIConfig.LOGIN_ENDPOINT);

        if (response.getStatusCode() == 200) {
            accessToken = response.jsonPath().getString("access_token");
            System.out.println("[STEP] Received access token: " + accessToken);
        }
    }

    @When("I login with a random email and password")
    public void i_login_with_random_email_and_password() {
        String randomEmail = FakerDataUtils.generateRandomEmail();
        String randomPassword = FakerDataUtils.generateRandomPassword();

        i_login_with_email_and_password(randomEmail, randomPassword);
    }

    @Given("I am logged in with random credentials")
    public void i_am_logged_in_with_random_credentials() {
        operationStartTime = System.currentTimeMillis(); // Start timing
        // Generate random credentials
        String email = FakerDataUtils.generateRandomEmail();
        String password = FakerDataUtils.generateRandomPassword();

        // Sign up first
        i_sign_up_with_email_and_password(email, password);
        Assert.assertEquals("Signup failed", 200, response.getStatusCode());

        // Then login
        i_login_with_email_and_password(email, password);
        Assert.assertNotNull("Login failed - no access token received", accessToken);
    }

    @Then("all operations should complete within acceptable time limits")
    public void all_operations_should_complete_within_acceptable_time_limits() {
        operationEndTime = System.currentTimeMillis();
        long elapsedMillis = operationEndTime - operationStartTime;
        System.out.println("[PERFORMANCE] Total elapsed time: " + elapsedMillis + " ms");
        // Acceptable time limit: 2000 ms (2 seconds)
        Assert.assertTrue("Operations took too long: " + elapsedMillis + " ms", elapsedMillis <= 2000);
    }

    @Given("I am logged in as {string} with password {string}")
    public void i_am_logged_in_as_with_password(String email, String password) {
        // Handle placeholders
        if ("RANDOM_EMAIL".equals(email)) {
            email = FakerDataUtils.generateRandomEmail();
        }
        if ("RANDOM_PASSWORD".equals(password)) {
            password = FakerDataUtils.generateRandomPassword();
        }

        // Always sign up the user before login to ensure the user exists
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        System.out.println("[STEP] (Auto) Signing up with email: " + email);
        RestAssured.given()
                .contentType(APIConfig.CONTENT_TYPE_JSON)
                .body(body)
                .post(APIConfig.SIGNUP_ENDPOINT);

        System.out.println("[STEP] Logging in as: " + email);
        i_login_with_email_and_password(email, password);
        Assert.assertNotNull(accessToken);
    }

    @When("I create a book with title {string} and author {string}")
    public void i_create_a_book_with_title_and_author(String title, String author) {
        // Handle random placeholders
        if ("RANDOM_TITLE".equals(title)) {
            title = FakerDataUtils.generateRandomBookTitle();
        }
        if ("RANDOM_AUTHOR".equals(author)) {
            author = FakerDataUtils.generateRandomAuthor();
        }

        // Create complete book payload matching the working API structure
        Map<String, Object> body = new HashMap<>();
        body.put("name", title); // Using "name" instead of "title" to match working API
        body.put("author", author);
        body.put("published_year", FakerDataUtils.generateRandomYear());
        body.put("book_summary", FakerDataUtils.generateRandomSentence());

        System.out.println("[STEP] Creating book with name: " + title + ", author: " + author);
        System.out.println("[REQUEST BODY] " + body);
        response = RestAssured.given()
                .header(APIConfig.AUTHORIZATION_HEADER, APIConfig.getBearerToken(accessToken))
                .contentType(APIConfig.CONTENT_TYPE_JSON)
                .body(body)
                .post(APIConfig.BOOKS_ENDPOINT);

        if (response.getStatusCode() == 200) {
            createdBookId = response.jsonPath().getInt("id");
            System.out.println("[STEP] Created book with id: " + createdBookId);
        } else {
            System.out.println("[ERROR] Failed to create book. Response: " + response.asString());
        }
    }

    @When("I create a book with random title and author")
    public void i_create_a_book_with_random_title_and_author() {
        String randomTitle = FakerDataUtils.generateRandomBookTitle();
        String randomAuthor = FakerDataUtils.generateRandomAuthor();

        i_create_a_book_with_title_and_author(randomTitle, randomAuthor);
    }

    @When("I get the created book by id")
    public void i_get_the_created_book_by_id() {
        System.out.println("[STEP] Getting book by id: " + createdBookId);
        response = RestAssured.given()
                .header(APIConfig.AUTHORIZATION_HEADER, APIConfig.getBearerToken(accessToken))
                .get(APIConfig.BOOKS_ENDPOINT + createdBookId);
    }

    @When("I update the book title to {string}")
    public void i_update_the_book_title_to(String newTitle) {
        if ("RANDOM_TITLE".equals(newTitle)) {
            newTitle = FakerDataUtils.generateRandomBookTitle();
        }

        Map<String, Object> body = new HashMap<>();
        body.put("name", newTitle); // Using "name" instead of "title"

        System.out.println("[STEP] Updating book id " + createdBookId + " name to: " + newTitle);
        System.out.println("[REQUEST BODY] " + body);
        response = RestAssured.given()
                .header(APIConfig.AUTHORIZATION_HEADER, APIConfig.getBearerToken(accessToken))
                .contentType(APIConfig.CONTENT_TYPE_JSON)
                .body(body)
                .put(APIConfig.BOOKS_ENDPOINT + createdBookId);
    }

    @When("I update the book with random title")
    public void i_update_the_book_with_random_title() {
        String randomTitle = FakerDataUtils.generateRandomBookTitle();
        i_update_the_book_title_to(randomTitle);
    }

    @When("I delete the book")
    public void i_delete_the_book() {
        System.out.println("[STEP] Deleting book id: " + createdBookId);
        response = RestAssured.given()
                .header(APIConfig.AUTHORIZATION_HEADER, APIConfig.getBearerToken(accessToken))
                .delete(APIConfig.BOOKS_ENDPOINT + createdBookId);
    }

    @When("I get the deleted book by id")
    public void i_get_the_deleted_book_by_id() {
        System.out.println("[STEP] Getting (deleted) book by id: " + createdBookId);
        i_get_the_created_book_by_id();
    }

    @When("I try to create a book without authentication")
    public void i_try_to_create_book_without_authentication() {
        String title = FakerDataUtils.generateRandomBookTitle();
        String author = FakerDataUtils.generateRandomAuthor();

        Map<String, Object> body = new HashMap<>();
        body.put("name", title);
        body.put("author", author);
        body.put("published_year", FakerDataUtils.generateRandomYear());
        body.put("book_summary", FakerDataUtils.generateRandomSentence());

        System.out.println("[STEP] Creating book without auth - name: " + title + ", author: " + author);
        System.out.println("[REQUEST BODY] " + body);
        response = RestAssured.given()
                .contentType(APIConfig.CONTENT_TYPE_JSON)
                .body(body)
                .post(APIConfig.BOOKS_ENDPOINT);
    }

    // Method to get stored credentials (for debugging)
    public Map<String, String> getStoredCredentials() {
        return userCredentials;
    }

}

