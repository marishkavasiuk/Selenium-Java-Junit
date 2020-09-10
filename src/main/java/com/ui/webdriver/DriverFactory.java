package com.ui.webdriver;

import com.ui.common.Config;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static com.ui.common.Config.USE_GRID;
import static com.ui.common.Constants.LOCALHOST;
import static com.ui.helpers.FileHelper.getDownloadDir;
import static com.ui.webdriver.DriverHelper.delay;
import static com.ui.webdriver.DriverProxy.startProxy;
import static com.google.common.io.Resources.getResource;

public class DriverFactory {
    private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);
    public static WebDriver getDriverInstance(String type) {
        boolean proxy = type.contains("PROXY");
        if (type.startsWith("CH")) {
            return getChromeInstance(proxy, false);
        } else if (type.startsWith("FF")) {
            return getFirefoxInstance(proxy);
        } else if (type.startsWith("HL")) {
            return getChromeInstance(proxy, true);
        }
        throw new RuntimeException("Unsupported browser type - " + type);
    }

    public static String getNodeIP(RemoteWebDriver driver) {
        HttpHost host = new HttpHost(Config.GRID_HUB_IP, 4444);
        String url = host + "/grid/api/testsession?session=";
        CloseableHttpClient client = HttpClientBuilder.create().build();

        try {
            URL session = new URL(url + driver.getSessionId());
            BasicHttpEntityEnclosingRequest req = new BasicHttpEntityEnclosingRequest("POST", session.toExternalForm());
            HttpResponse response = client.execute(host, req);

            JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
            String proxyID = (String) object.get("proxyId");
            return proxyID.split("//")[1].split(":")[0];
        } catch (Exception e) {
            return LOCALHOST;
        }
    }

    private static RemoteWebDriver startGrid(DesiredCapabilities capabilities) {
        URL url;
        try {
            url = new URL("http://" + Config.GRID_HUB_IP + ":4444/wd/hub");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Incorrect Grid Hub URL", e);
        }

        RemoteWebDriver rwd = new RemoteWebDriver(url, capabilities);
        logger.info(String.format("Browser %s has been started on Grid Node (%s)", Config.BROWSER, getNodeIP(rwd)));
        delay(1);
        return rwd;
    }

    private static WebDriver getChromeInstance(boolean isProxy, boolean isHeadLess) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        if (isHeadLess) options.addArguments("headless");

        Map<String, Object> pref = new HashMap<String, Object>();
        pref.put("download.default_directory", getDownloadDir());
        options.setExperimentalOption("prefs", pref);

        // performance capabilities
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

        if (isProxy) {
            options.setCapability(CapabilityType.PROXY, startProxy());
        }

        String osName = System.getProperty("os.name", "Linux");
        String driverPath;
        if ("Linux".equals(osName)) {
            driverPath = getResource("chromedriverLinux").getPath();
            new File(driverPath).setExecutable(true);
//        } else {
//            WebDriverManager.chromedriver().setup();
//        }

        } else {
            driverPath = getResource("chromedriver.exe").getPath();
        }
        System.setProperty("webdriver.chrome.driver", driverPath);
        String chPort = Config.properties.getProperty("CH_PORT");

        try {
            if (USE_GRID) {
                DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                capabilities.setCapability(ChromeOptions.CAPABILITY, options);
//                capabilities.setVersion("83.0");
//                capabilities.setCapability("enableVNC", true);
//                capabilities.setCapability("enableVideo", false);
                return startGrid(capabilities);
            } else {
                if (chPort != null) {
                    ChromeDriverService.Builder builder = new ChromeDriverService.Builder();
                    ChromeDriverService service = builder.usingPort(Integer.parseInt(chPort)).build();
                    return new ChromeDriver(service, options);
                } else {
                    return new ChromeDriver(options);
                }
            }
        } catch (Exception e) {
            logger.info("Unable to start Chrome browser", e);
            throw new RuntimeException("Unable to start Chrome browser", e);
        }
    }

    private static WebDriver getFirefoxInstance(boolean isProxy) {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("extensions.firebug.currentVersion", "2.0.12");//"1.11.1");
        profile.setPreference("dom.max_chrome_script_run_time", 0);
        profile.setPreference("dom.max_script_run_time", 0);

        // set download dir
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.dir", getDownloadDir());
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.helperApps.alwaysAsk.force", false);
        profile.setPreference("browser.helperApps.neverAsk.openFile", "application/csv, application/gzip");
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/csv, application/gzip");

        // SSL
        profile.setAcceptUntrustedCertificates(true);

        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability(FirefoxDriver.PROFILE, profile);
        capabilities.setAcceptInsecureCerts(true);

        if (isProxy) {
            capabilities.setCapability(CapabilityType.PROXY, startProxy());
        }

        String osName = System.getProperty("os.name", "Linux");
        String driverPath;
        if ("Linux".equals(osName)) {
            driverPath = getResource("geckodriver").getPath();
            new File(driverPath).setExecutable(true);
        } else {
            driverPath = getResource("geckodriver.exe").getPath();
        }
        System.setProperty("webdriver.gecko.driver", driverPath);

        try {
            if (USE_GRID) {
                return startGrid(capabilities);
            } else {
                FirefoxOptions options = new FirefoxOptions().setProfile(profile);
                options.merge(capabilities);
                return new FirefoxDriver(options);
            }
        } catch (Exception e) {
            logger.info("Unable to start Firefox browser", e);
            throw new RuntimeException("Unable to start Firefox browser", e);
        }
    }
}