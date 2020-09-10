package com.ui.sessions;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class WiniumTest {

    WebDriver driver;

    @BeforeEach
    public void start() throws MalformedURLException {

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("app", "C:\\Windows\\System32\\calc.exe");

        driver = new RemoteWebDriver(new URL("http://localhost:9999"), caps);
    }

    @Test
    public void test() {

        driver.findElement(By.name("One")).click();
        driver.findElement(By.name("Two")).click();
        driver.findElement(By.name("Plus")).click();
        driver.findElement(By.name("Four")).click();
        driver.findElement(By.name("Six")).click();
        driver.findElement(By.name("Two")).click();
        driver.findElement(By.name("Equals")).click();

        String value = driver.findElement(By.id("CalculatorResults")).getAttribute("Name");

        Assertions.assertEquals("Display is 474", value);
    }

    @AfterEach
    public void stop() {
        driver.findElement(By.id("Close")).click();
    }
}
