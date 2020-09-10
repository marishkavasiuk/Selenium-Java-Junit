package com.ui.sessions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class SelenideTest {

    @BeforeEach
    public void start() {

        System.setProperty("selenide.browser", "chrome");
    }

    @Test
    public void searchAndOpenSelenideSite() {

        open("http://google.com");
        $(By.name("q")).sendKeys("selenide" + Keys.ENTER);
        $("h3").click();
        $("div.news div.news-line").shouldHave(text("Вышла Selenide 5.12.2"));

    }
}
