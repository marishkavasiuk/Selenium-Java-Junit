package com.ui.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import static com.ui.common.Constants.LOCALHOST;
import static java.lang.Thread.currentThread;

public class Config {
    public static final String URL = "https://github.com/";
    public static final boolean BROWSER_CH;
    public static final boolean BROWSER_FF;
    public static final String THREADS;
    public static final String GRID_HUB_IP;

    public static String BROWSER;
    public static Boolean USE_GRID = false;

    public static final String USER;
    public static final String PASSWORD;

    public static final String HOST;

    public static final String SSH_HOST;
    public static final String SSH_PORT = "22";

    public static double DELAY = 0.3;
    public static final int TIMEOUT = 10;


    public static Properties properties;

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    static {
        // use to run on Jenkins OR local PC
        String prop = (System.getProperty("env") != null ? System.getProperty("env") : System.getenv("env"));
        prop = (prop != null) ? prop : LOCALHOST;

        // read properties
        properties = new Properties();
        logger.info(String.format("Loading '%s.properties' file", prop));
        try {
            properties.load(currentThread().getContextClassLoader().getResourceAsStream("properties/" + prop + ".properties"));
        } catch (Exception e1) {
            try {
                properties.load(currentThread().getContextClassLoader().getResourceAsStream(prop + ".properties"));
            } catch (Exception e2) {
                logger.error("Unable to read properties file", e2);
            }
        }

        // get Driver type
        String browser = System.getProperty("browser") != null ? System.getProperty("browser") : System.getenv("browser"); // use by Jenkins
        browser = (browser != null) ? browser : properties.getProperty("browser");
        BROWSER = (browser != null) ? browser : "CH";

        // set browser type
        BROWSER_CH = "ch".equalsIgnoreCase(Config.BROWSER);
        BROWSER_FF = "ff".equalsIgnoreCase(Config.BROWSER);

        // get testNG threads count
        String threads = System.getProperty("threads") != null ? System.getProperty("threads") : System.getenv("threads");
        THREADS = (threads != null) ? threads : "1";

        // get Selenium Grid settings
        String gridHub = !StringUtils.isEmpty(System.getProperty("grid.hub.ip")) ?
                System.getProperty("grid.hub.ip") : System.getenv("grid.hub.ip");
        GRID_HUB_IP = !StringUtils.isEmpty(gridHub) ? gridHub : LOCALHOST;

        // some additional parameters for Grid
        if (!LOCALHOST.equals(GRID_HUB_IP)) {
            USE_GRID = true;
            DELAY = 0.1;
        }

        // get global properties
        HOST = properties.getProperty("host");
        SSH_HOST = HOST;

        // read GIM properties
        USER = properties.getProperty("gim.user")!= null ? properties.getProperty("gim.user") : System.getenv("gim.user");
        PASSWORD = properties.getProperty("gim.pass")!= null ? properties.getProperty("gim.pass") : System.getenv("gim.pass");

        // get running VM
        Constants.Grid grid = Constants.Grid.LOCALHOST;
        try {
            String vm = InetAddress.getByName(Config.GRID_HUB_IP).getHostName();
            grid = Constants.Grid.fromValue(vm);
        } catch (UnknownHostException e) {
            logger.warn("Unable to get Grid name", e);
        }

    }
}