package com.ui.sessions;

import com.ui.sessions.listeners.Listener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.util.List;

public class EventsListenerTest {

    EventFiringWebDriver edr;
    WebDriverWait wait;

    @BeforeEach
    public void start() {
        edr = new EventFiringWebDriver(new ChromeDriver());
        edr.register(new Listener());
        wait = new WebDriverWait(edr, 5);
    }

    @AfterEach
    public void stop() {
        edr.quit();
    }

    @Test
    public void listenerTest() {
        WebDriverWait wait = new WebDriverWait(edr, 5);

        edr.get("https://habr.com");

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#navbar-links")));

        for (int i = 0; i < getMenuItems().size(); i++) {
            getMenuItems().get(i).click();
            Assertions.assertTrue(isHeaderPresent(),"Header is absent");

        }
    }

    private boolean isHeaderPresent() {
        return edr.findElements((By.cssSelector(".page-header"))).size() > 0;
    }

    private List<WebElement> getMenuItems() {
        return edr.findElements(By.cssSelector(".nav-links__item"));
    }


}
