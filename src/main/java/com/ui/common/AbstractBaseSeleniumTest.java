package com.ui.common;


import com.ui.pages.HomePage;
import com.ui.pages.LoginPage;
import com.ui.pages.Page;
import com.ui.pages.StartPage;
import com.ui.webdriver.DriverHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ui.pages.PageNavigator.getPage;
import static com.ui.webdriver.DriverSession.*;


public abstract class AbstractBaseSeleniumTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractBaseSeleniumTest.class);

    @BeforeAll()
    public static void setupTests() {
        startNewBrowserSession();
        getPage(StartPage.class).navigateToLogin();
        login();
    }

    @AfterAll()
    public static void tearDownTests() {
        logout();
        closeAllDriverSessions();
    }

    protected static boolean startNewBrowserSession() {
        if (getDriverSessionByContext() == null) {
            startBrowser();
            return true;
        }
        return false;
    }

    protected static StartPage startBrowser() {
        setDriverSession(new DriverHelper(Config.BROWSER));
        getDriverSession().openURL();
        return getPage(StartPage.class);
    }

    protected static HomePage login() { return getPage(LoginPage.class).login();
    }

    protected static Page logout() {
        try {
            return getPage(Page.class).logout();
        } catch (Exception e) {
            logger.error("Unable to logout", e);
            closeDriverSession();
        }
        return null;
    }
}

