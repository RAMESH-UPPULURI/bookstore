package bookstore.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"bookstore.stepdefs"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports.html",
                "json:target/cucumber-reports/Cucumber.json",
                "junit:target/cucumber-reports/Cucumber.xml"
        },
        monochrome = true,
        publish = false,
        tags = "@smoke or @regression or @multiple or @performance or @security or @boundary or @workflow"
)
public class TestRunner {
    // This class will remain empty, it is used only as a holder for the above annotations
}