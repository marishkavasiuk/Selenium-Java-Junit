package com.ui.webdriver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.currentThread;

public class DriverSession {
    private final static Map<Long, DriverHelper> sessions = new ConcurrentHashMap<>();
    private final static Map<Long, String> userIDs = new ConcurrentHashMap<>();
    private final static Set<DriverHelper> availableDrivers = new HashSet<>();

    public static DriverHelper getDriverSession() {
        DriverHelper driver = sessions.get(currentThread().getId());
        return (driver != null && driver.isAlive()) ? driver : null;
    }

    private static DriverHelper getAvailableDriver() {
        DriverHelper driver = null;
        synchronized (availableDrivers) {
            Iterator<DriverHelper> i = availableDrivers.iterator();
            if (i.hasNext()) {
                driver = i.next();
                availableDrivers.remove(driver);
            }
        }
        return driver;
    }

    public static void setDriverSession(DriverHelper driver) {
        sessions.put(currentThread().getId(), driver);
    }

    public static DriverHelper getDriverSessionByContext() {
        if (getDriverSession() == null) {
            DriverHelper driver = getAvailableDriver();
            if (driver != null && driver.isAlive()) {
                setDriverSession(driver);
            }
        }
        return getDriverSession();
    }

    public static void closeDriverSession() {
        DriverHelper driver = getDriverSession();
        if (driver != null) {
            driver.close();
            synchronized (availableDrivers) {
                availableDrivers.remove(driver);
            }
        }
        sessions.remove(currentThread().getId());
    }

    public static void closeAllDriverSessions() {
        sessions.values().stream().filter(Objects::nonNull).forEach(DriverHelper::close);
        sessions.clear();
    }

}
