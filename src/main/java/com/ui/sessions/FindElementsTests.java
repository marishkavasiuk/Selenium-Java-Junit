package com.ui.sessions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class FindElementsTests {

    WebDriver drv;

    @BeforeEach
    public void start() {
        drv = new ChromeDriver();

    }


    @Test
    void findElement() {

        drv.get("https://www.google.com/");

        By search = By.cssSelector("#tsf > div:nth-child(2) > div.A8SBwf > div.RNNXgb > div > div.a4bIc > input");

        drv.findElement(search).sendKeys("sdsdsds");


        if (areElementsPresent(drv, By.xpath("#tsf > div:nth-child(2) > div.A8SBwf > div.RNNXgb > div > div.a4bIc > input")))
            System.out.printf("Element is present");
        else
            System.out.printf("Element not found");


    }

    @Test
    public void jsExecutor() {
        drv.get("https://www.w3.org/");

        List<WebElement> links = (List<WebElement>) ((JavascriptExecutor) drv)
                .executeScript("return document.getElementsByClassName('headline')");

        for (WebElement item : links) {
            System.out.println(item.getTagName());
        }

        drv.get("http://www.google.com");

        List<WebElement> input = (List<WebElement>) ((JavascriptExecutor) drv).executeScript("return document.getElementsByName('q')");
        input.get(0).sendKeys("webdriver");

    }

    @AfterEach
    public void stop() {
        drv.quit();

    }

    private boolean isElementPresent(WebDriver driver, By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException ex) {
            return false;
        }
    }

    private boolean areElementsPresent(WebDriver driver, By locator) {
        return driver.findElements(locator).size() > 0;
    }

}
