package com.ui.sessions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.stream.Stream;

public class DriverProviderTest {

    private static Stream<WebDriver> browsers() {
        return Stream.of(
                new ChromeDriver(),
                new FirefoxDriver());
    }

    /// https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests

    @ParameterizedTest(name = "{index} Running in: {0}")
    @MethodSource("browsers")
    public void SimpleTest(WebDriver browser) {
        try {
            browser.get("http://google.com");
            browser.findElement(By.name("q")).sendKeys("SeleniumTests");
            browser.findElement(By.name("q")).submit();
        } finally {
            browser.quit();
        }
    }


}
