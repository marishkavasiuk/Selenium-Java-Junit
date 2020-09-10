package com.ui.pages;

import com.ui.webdriver.DriverHelper;
import com.ui.webdriver.DriverSession;

public class PageNavigator {
    public static <T extends Page> T getPage(Class<T> pageClass) {
        DriverHelper driver = DriverSession.getDriverSession();
        try {
            return pageClass.getConstructor(DriverHelper.class).newInstance(driver);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get Page instance of " + pageClass, e);
        }
    }

    public static <T extends Page> T getDialogPage(Class<T> pageClass) {
        DriverHelper driver = DriverSession.getDriverSession();
        try {
            return pageClass.getConstructor(DriverHelper.class, Boolean.class).newInstance(driver, true);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get Page instance of " + pageClass, e);
        }
    }


}
