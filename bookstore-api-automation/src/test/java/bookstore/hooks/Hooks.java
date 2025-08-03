package bookstore.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.LogConfig;

public class Hooks {

    @Before
    public void setUp(Scenario scenario) {
        System.out.println("==================================================");
        System.out.println("STARTING SCENARIO: " + scenario.getName());
        System.out.println("==================================================");

        // Configure RestAssured
        RestAssured.baseURI = "http://localhost:8000";
        RestAssured.config = RestAssuredConfig.config()
                .logConfig(LogConfig.logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails()
                        .enablePrettyPrinting(true));

        // Reset RestAssured filters and settings for each scenario
        RestAssured.reset();
        RestAssured.baseURI = "http://localhost:8000";
    }

    @After
    public void tearDown(Scenario scenario) {
        System.out.println("==================================================");
        System.out.println("SCENARIO COMPLETED: " + scenario.getName());
        System.out.println("STATUS: " + (scenario.isFailed() ? "FAILED" : "PASSED"));

        if (scenario.isFailed()) {
            System.out.println("SCENARIO FAILED - Check logs above for details");
            // You can add screenshot capture here if needed for UI tests
            // byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            // scenario.attach(screenshot, "image/png", "Screenshot");
        }

        System.out.println("==================================================");

        // Clean up RestAssured
        RestAssured.reset();
    }

    @Before("@database")
    public void setUpDatabase() {
        System.out.println("[HOOK] Setting up database for database-related scenarios");
        // Add database setup logic here if needed
    }

    @After("@database")
    public void cleanUpDatabase() {
        System.out.println("[HOOK] Cleaning up database after database-related scenarios");
        // Add database cleanup logic here if needed
    }

    @Before("@api")
    public void setUpAPI() {
        System.out.println("[HOOK] Setting up API configuration");
        // Additional API-specific setup can go here
    }
}