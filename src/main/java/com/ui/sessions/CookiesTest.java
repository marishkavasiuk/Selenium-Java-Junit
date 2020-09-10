package com.ui.sessions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.ie.InternetExplorerDriver;


public class CookiesTest {

    WebDriver drv1,drv2;

    @BeforeEach
    public  void start()
    {
        drv1 = new InternetExplorerDriver();//ChromeDriver();
        drv2 = new InternetExplorerDriver();//ChromeDriver();
    }


    @Test
    public void cookieTest() {

      ///  drv1.manage().addCookie(new Cookie("test", "test"));

        drv1.get("http://google.com");
        drv1.manage().addCookie(new Cookie("test", "passed"));

        drv2.get("http://google.com");

        System.out.println("Drv1 before: " + drv1.manage().getCookies());
        System.out.println("Drv2 before: " + drv2.manage().getCookies());

        drv1.manage().addCookie(new Cookie("Security_ID", "KJILSDuf984jmmcoidshnfrdoiut98e"));
        System.out.println("Drv1 after: " + drv1.manage().getCookies());
        System.out.println("Drv1 NID: " + drv1.manage().getCookieNamed("NID"));

        drv1.manage().deleteCookieNamed("NID");
        System.out.println("Drv1 NID deleted: " + drv1.manage().getCookies());

        drv1.manage().deleteAllCookies();
        System.out.println("Drv1 delete: " + drv1.manage().getCookies());
        System.out.println("Drv2 delete: " + drv2.manage().getCookies());
    }

    @AfterEach
    public void stop()
    {
        drv1.quit();
        drv2.quit();
    }

}
