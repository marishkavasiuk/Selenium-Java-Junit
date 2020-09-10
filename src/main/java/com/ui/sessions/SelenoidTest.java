package com.ui.sessions;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;


public class SelenoidTest {

    static RemoteWebDriver drv;

    @BeforeAll
    public static void start() throws MalformedURLException {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName("chrome");
        capabilities.setVersion("81.0");
        capabilities.setCapability("enableVNC", true);
        capabilities.setCapability("enableVideo", false);

         drv = new RemoteWebDriver(
                URI.create("http://192.168.56.102:4444/wd/hub").toURL(),
                capabilities
        );

    }

    @AfterAll
    public static void stop() {
        drv.quit();
    }

    @Test
    public void remoteTest() {
        drv.get("http://google.com");
        drv.findElementByName("q").sendKeys("Remote WebDriver" + Keys.ENTER);
        Assertions.assertTrue(drv.findElement(By.tagName("h3")).getText().contains("RemoteWebDriver"));
    }



}
