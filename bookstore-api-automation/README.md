# Bookstore API Automation - BDD Cucumber with Faker

## 📁 Project Structure

```
bookstore-api-automation/
├── pom.xml
├── cucumber.properties
├── src/
│   ├── main/
│   │   └── java/
│   │       └── bookstore/
│   │           ├── config/
│   │           │   └── APIConfig.java
│   │           └── utils/
│   │               └── FakerDataUtils.java
│   └── test/
│       ├── java/
│       │   └── bookstore/
│       │       ├── runners/
│       │       │   └── TestRunner.java
│       │       ├── stepdefs/
│       │       │   └── BookstoreApiSteps.java
│       │       └── hooks/
│       │           └── Hooks.java
│       └── resources/
│           └── features/
│               └── bookstore.feature
└── target/
    └── cucumber-reports/
```

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher
- Your Bookstore API running on `http://localhost:8000`

### Installation
1. Clone or create the project with the provided files
2. Run `mvn clean install` to download dependencies
3. Ensure your Bookstore API is running

##  Running Tests

### Basic Execution
```bash
# Run all tests with JUnit runner (default)
mvn clean test

# Run specific feature file
mvn clean test -Dcucumber.features=src/test/resources/features/bookstore.feature
```

### Tag-based Execution
```bash
# Run smoke tests only
mvn clean test -Psmoke

# Run regression tests
mvn clean test -Pregression

# Run specific tags
mvn clean test -Dcucumber.filter.tags="@smoke and @auth"

# Run multiple tag combinations
mvn clean test -Dcucumber.filter.tags="@smoke or @regression"
```

### Parallel Execution
```bash
# Run tests in parallel
mvn clean test -Pparallel
```

### Environment Configuration
```bash
# Run against different environment
mvn clean test -Dbase.uri=http://staging-api.com -Dport=8080

# Run with custom configuration
mvn clean test -Dbase.uri=https://api.example.com -Dbase.path=/v1
```

## Reports

### Cucumber HTML Reports
- Location: `target/cucumber-reports/cucumber.html`
- Generated automatically after test execution

### JSON Reports
- Location: `target/cucumber-reports/cucumber.json`
- Can be integrated with CI/CD tools

## Faker Integration

### Random Data Generation
The framework uses JavaFaker to generate realistic test data:

```gherkin
# In feature files, use these placeholders:
RANDOM_EMAIL     # Generates: john.doe@example.com
RANDOM_PASSWORD  # Generates: randomPass123
RANDOM_TITLE     # Generates: "The Great Gatsby"
RANDOM_AUTHOR    # Generates: "F. Scott Fitzgerald"
```

### Custom Data Utilities
```java
// Available in FakerDataUtils class:
String email = FakerDataUtils.generateRandomEmail();
String password = FakerDataUtils.generateSecurePassword();
String bookTitle = FakerDataUtils.generateRandomBookTitle();
String author = FakerDataUtils.generateRandomAuthor();
Map<String, Object> completeBookData = FakerDataUtils.generateCompleteBookData();
```

## Configuration

### API Configuration (APIConfig.java)
```java
// Default settings - can be overridden via system properties
BASE_URI = "http://localhost:8000"
CONNECTION_TIMEOUT = 30000ms
SOCKET_TIMEOUT = 60000ms
```

### Cucumber Configuration (cucumber.properties)
```properties