package bookstore.utils;

import com.github.javafaker.Faker;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FakerDataUtils {

    private static final Faker faker = new Faker();

    // User-related data generation
    public static String generateRandomEmail() {
        return faker.internet().emailAddress();
    }

    public static String generateRandomEmailWithDomain(String domain) {
        return faker.internet().emailAddress().split("@")[0] + "@" + domain;
    }

    public static String generateRandomPassword() {
        return faker.internet().password(8, 16);
    }

    public static String generateSecurePassword() {
        return faker.internet().password(12, 20, true, true, true);
    }

    public static String generateRandomFirstName() {
        return faker.name().firstName();
    }

    public static String generateRandomLastName() {
        return faker.name().lastName();
    }

    public static String generateRandomFullName() {
        return faker.name().fullName();
    }

    // Book-related data generation
    public static String generateRandomBookTitle() {
        return faker.book().title();
    }

    public static String generateRandomAuthor() {
        return faker.book().author();
    }

    public static String generateRandomPublisher() {
        return faker.book().publisher();
    }

    public static String generateRandomGenre() {
        return faker.book().genre();
    }

    public static int generateRandomYear() {
        return faker.number().numberBetween(1900, 2024);
    }

    public static int generateRandomPages() {
        return faker.number().numberBetween(50, 1000);
    }

    public static String generateRandomISBN() {
        return faker.code().isbn13();
    }

    // Complete book data generation
    public static Map<String, Object> generateCompleteBookData() {
        Map<String, Object> bookData = new HashMap<>();
        bookData.put("title", generateRandomBookTitle());
        bookData.put("author", generateRandomAuthor());
        bookData.put("publisher", generateRandomPublisher());
        bookData.put("genre", generateRandomGenre());
        bookData.put("publishedYear", generateRandomYear());
        bookData.put("pages", generateRandomPages());
        bookData.put("isbn", generateRandomISBN());
        bookData.put("summary", generateRandomBookSummary());
        return bookData;
    }

    // Text generation
    public static String generateRandomBookSummary() {
        return faker.lorem().paragraph(3);
    }

    public static String generateRandomSentence() {
        return faker.lorem().sentence();
    }

    public static String generateRandomParagraph() {
        return faker.lorem().paragraph();
    }

    // Address and location data
    public static String generateRandomAddress() {
        return faker.address().fullAddress();
    }

    public static String generateRandomCity() {
        return faker.address().city();
    }

    public static String generateRandomCountry() {
        return faker.address().country();
    }

    public static String generateRandomPhoneNumber() {
        return faker.phoneNumber().phoneNumber();
    }

    // Date and time generation
    public static String generateRandomDateInPast() {
        return faker.date().past(365, TimeUnit.DAYS).toString();
    }

    public static String generateRandomDateInFuture() {
        return faker.date().future(365, TimeUnit.DAYS).toString();
    }

    // Internet and technology
    public static String generateRandomUrl() {
        return faker.internet().url();
    }

    public static String generateRandomDomainName() {
        return faker.internet().domainName();
    }

    public static String generateRandomUserAgent() {
        return faker.internet().userAgentAny();
    }

    // Complete user registration data
    public static Map<String, String> generateCompleteUserData() {
        Map<String, String> userData = new HashMap<>();
        userData.put("email", generateRandomEmail());
        userData.put("password", generateRandomPassword());
        userData.put("firstName", generateRandomFirstName());
        userData.put("lastName", generateRandomLastName());
        userData.put("fullName", generateRandomFullName());
        userData.put("phone", generateRandomPhoneNumber());
        userData.put("address", generateRandomAddress());
        userData.put("city", generateRandomCity());
        userData.put("country", generateRandomCountry());
        return userData;
    }

    // Validation test data
    public static String generateInvalidEmail() {
        return faker.lorem().word() + "invalid-email";
    }

    public static String generateShortPassword() {
        return faker.internet().password(1, 3);
    }

    public static String generateLongPassword() {
        return faker.internet().password(100, 150);
    }

    // SQL Injection test data (for negative testing)
    public static String generateSQLInjectionString() {
        return "'; DROP TABLE users; --";
    }

    public static String generateXSSString() {
        return "<script>alert('XSS')</script>";
    }

    // Utility methods
    public static Faker getFakerInstance() {
        return faker;
    }

    public static String generateCustomData(String pattern) {
        return faker.expression(pattern);
    }

    // Test data for specific scenarios
    public static Map<String, String> generateLoginCredentials() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", generateRandomEmail());
        credentials.put("password", generateRandomPassword());
        return credentials;
    }

    public static Map<String, Object> generateMinimalBookData() {
        Map<String, Object> bookData = new HashMap<>();
        bookData.put("title", generateRandomBookTitle());
        bookData.put("author", generateRandomAuthor());
        return bookData;
    }

    // Print methods for debugging
    public static void printGeneratedUserData() {
        Map<String, String> userData = generateCompleteUserData();
        System.out.println("=== Generated User Data ===");
        userData.forEach((key, value) -> System.out.println(key + ": " + value));
        System.out.println("===========================");
    }

    public static void printGeneratedBookData() {
        Map<String, Object> bookData = generateCompleteBookData();
        System.out.println("=== Generated Book Data ===");
        bookData.forEach((key, value) -> System.out.println(key + ": " + value));
        System.out.println("===========================");
    }
}