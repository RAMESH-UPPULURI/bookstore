package bookstore.utils;

import com.github.javafaker.Faker;

public class FakerDataUtils {

    private static final Faker faker = new Faker();

    // User-related data generation
    public static String generateRandomEmail() {
        return faker.internet().emailAddress();
    }

    public static String generateRandomPassword() {
        return faker.internet().password(8, 16);
    }

    // Book-related data generation
    public static String generateRandomBookTitle() {
        return faker.book().title();
    }

    public static String generateRandomAuthor() {
        return faker.book().author();
    }

    public static int generateRandomYear() {
        return faker.number().numberBetween(1900, 2024);
    }

    // Text generation
    public static String generateRandomSentence() {
        return faker.lorem().sentence();
    }

}