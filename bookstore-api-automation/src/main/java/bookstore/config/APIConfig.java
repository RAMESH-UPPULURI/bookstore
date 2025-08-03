package bookstore.config;

import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.ConnectionConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.mapper.ObjectMapperType;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

public class APIConfig {

    // API Configuration Constants
    public static final String BASE_URI = System.getProperty("base.uri", "http://localhost:8000");
    public static final String BASE_PATH = System.getProperty("base.path", "");
    public static final int DEFAULT_PORT = Integer.parseInt(System.getProperty("port", "8000"));
    public static final int CONNECTION_TIMEOUT = 30000; // 30 seconds
    public static final int SOCKET_TIMEOUT = 60000; // 60 seconds

    // Endpoints
    public static final String HEALTH_ENDPOINT = "/health";
    public static final String SIGNUP_ENDPOINT = "/signup";
    public static final String LOGIN_ENDPOINT = "/login";
    public static final String BOOKS_ENDPOINT = "/books/";

    // Headers
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";

    // Test Data Constants
    public static final String DEFAULT_PASSWORD = "testpass123";
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 16;

    static {
        setupRestAssured();
    }

    /**
     * Configure RestAssured with default settings
     */
    public static void setupRestAssured() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.basePath = BASE_PATH;
        RestAssured.port = DEFAULT_PORT;

        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", CONNECTION_TIMEOUT)
                        .setParam("http.socket.timeout", SOCKET_TIMEOUT))
                .connectionConfig(ConnectionConfig.connectionConfig()
                        .closeIdleConnectionsAfterEachResponseAfter(30, TimeUnit.SECONDS))
                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                        .defaultObjectMapperType(ObjectMapperType.JACKSON_2))
                .logConfig(LogConfig.logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails()
                        .enablePrettyPrinting(true));
    }

    /**
     * Enable request and response logging to console
     */
    public static void enableConsoleLogging() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    /**
     * Enable request and response logging to file
     */
    public static void enableFileLogging(String fileName) {
        try {
            PrintStream fileOutPutStream = new PrintStream(new FileOutputStream(fileName, true));
            RestAssured.filters(
                    new RequestLoggingFilter(fileOutPutStream),
                    new ResponseLoggingFilter(fileOutPutStream)
            );
        } catch (FileNotFoundException e) {
            System.err.println("Could not create log file: " + fileName);
            e.printStackTrace();
        }
    }

    /**
     * Get authorization header value
     */
    public static String getBearerToken(String accessToken) {
        return BEARER_TOKEN_PREFIX + accessToken;
    }

    /**
     * Reset RestAssured configuration to defaults
     */
    public static void resetRestAssured() {
        RestAssured.reset();
        setupRestAssured();
    }

    /**
     * Get environment info
     */
    public static void printEnvironmentInfo() {
        System.out.println("=== API Configuration ===");
        System.out.println("Base URI: " + BASE_URI);
        System.out.println("Base Path: " + BASE_PATH);
        System.out.println("Port: " + DEFAULT_PORT);
        System.out.println("Connection Timeout: " + CONNECTION_TIMEOUT + "ms");
        System.out.println("Socket Timeout: " + SOCKET_TIMEOUT + "ms");
        System.out.println("=========================");
    }
}