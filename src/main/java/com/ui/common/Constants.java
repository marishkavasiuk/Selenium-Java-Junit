package com.ui.common;

import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Thread.currentThread;

public class Constants {
    public static final String EMPTY = "";
    public static final List<String> EMPTY_LIST = Collections.emptyList();
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String ANY = "ANY";
    public static final String DEFAULT = "Default";
    public static final String AUTO = "AUTO";
    public static final String WILDCARD = "%";
    public static final String DEFAULT_PASSWORD = "Password1234!@#$";
    public static final String DATE_FORMAT_1 = "MM/dd/yyyy";
    public static final String DATE_FORMAT_2 = "yyyy-MM-dd";
    public static final String DATE_FORMAT_3 = "dd-MM-yyyy"; // to insert into DB
    public static final String DATE_FORMAT_4 = "yyyy-MM-dd HH:mm";
    public static final String NOT_FOUND = "_#element_not_found#_";
    public static final String LOCALHOST = "localhost";
    public static final String PLG_TEST_FILES = "pluginscripts/";
    public static final String ERR_ELM_NOT_FOUND = "Element with %s not found";
    public static final String ERR_OBJ_VALUE_NOT_FOUND = "Value was not found: %s";
    public static final String ERR_LOGIN_FAILED = "Incorrect username or password.";
    public static final String repDescription = "NewDescription";
    public static final boolean ADD_README = true;
    public static final String GIT_IGNORE = "Java";
    public static final String LICENSE = null;
    public static String repName;
    public static final String TITLE = "New issue";
    public static final String COMMENT = "Comment";
    public static final String PROJECT_NAME = "New project";
    public static final String PROJECT_BODY = "Body";


    public static String trimValue(String value) {
        if (value == null) {
            return EMPTY;
        }
        String space = String.valueOf((char) 160);
        return value.trim().replaceAll(space, EMPTY).replaceAll("&nbsp", "").trim();
    }

    public static String getCamelCase(String str) {
        StringBuffer sb = new StringBuffer(str);
        String res;
        Random random = new Random();
        do {
            for (int i = 0; i < sb.length(); i++) {
                char ch = sb.charAt(i);
                if (Character.isLetter(ch) && random.nextBoolean()) {
                    sb.setCharAt(i, Character.isLowerCase(ch) ?
                            Character.toUpperCase(ch) :
                            Character.toLowerCase(ch));
                }
            }
            res = sb.toString();
        } while (res.equals(str));
        return res;
    }

    public static String randomString() {
        return randomString(AUTO, 10);
    }

    public static String randomString(int size) {
        return RandomStringUtils.randomAlphabetic(size);
    }

    public static String randomString(String prefix, int size) {
        String res = prefix.toUpperCase() + new BigInteger(500, new Random()).toString();
        return res.substring(0, size);
    }

    public static String randomLongString(int size) {
        StringBuilder sb = new StringBuilder(AUTO);
        for (int i = 0; i < size - 4; i++) {
            sb.append(new Random().nextInt(10));
        }
        return sb.toString();
    }

    public static String randomInt() {
        return String.valueOf(new Random().nextInt(1000));
    }

    public static String randomInt(int num) {
        return String.valueOf(new Random().nextInt(num));
    }

    public static String randomDate() {
        //method returns random date in range [10 days before - today] in string format
        Random random = new Random();
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_1);

        // generate random date
        long dateBefore = Math.abs(random.nextInt(10) * 24 * 60 * 60 * 1000);
        long dateToday = new Date().getTime();
        return df.format(new Date(dateToday - dateBefore)).toUpperCase();
    }

    public static String randomDateTimeStamp() {
        //method returns random date in range [10 days before - today] in string format
        Random random = new Random();
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_4);

        // generate random date
        long dateBefore = Math.abs(random.nextInt(10) * 24 * 60 * 60 * 1000);
        long dateToday = new Date().getTime();
        return df.format(new Date(dateToday - dateBefore)).toUpperCase();
    }


    public static boolean isMethodRunning(String method) {
        for (StackTraceElement el : currentThread().getStackTrace()) {
            if (el.getMethodName().contains(method)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isClassLoader(Class<?> clazz) {
        for (StackTraceElement el : Thread.currentThread().getStackTrace()) {
            if (el.getClassName().startsWith(clazz.getName())) {
                return true;
            }
        }
        return false;
    }

    public enum Grid {
        LOCALHOST("localhost"),
        DOCKER_GRID("gmo");

        private String value;

        Grid(String value) {
            this.value = value;
        }

        public static Grid fromValue(String name) {
            for (Grid gn : Grid.values()) {
                if (name.toLowerCase().contains(gn.value)) {
                    return gn;
                }
            }
            return LOCALHOST;
        }
    }
    public static void setRepositoryName(String repName) {
        Constants.repName = repName;
    }
}