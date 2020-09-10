package com.ui.webdriver;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ui.webdriver.DriverHelper.delay;



public class DriverProxy {
    private static BrowserMobProxy proxy;

    private static final Logger logger = LoggerFactory.getLogger(DriverProxy.class);

    public static BrowserMobProxy getProxy() {
        return proxy;
    }

    public static void stopProxy() {
        if (proxy != null && proxy.isStarted()) {
            try {
                proxy.stop();
            } catch (Exception e) {
                logger.info("Unable to stop proxy");
            }
        }
    }

    public static Proxy startProxy() {
        proxy = new BrowserMobProxyServer();
        proxy.start(0);
        proxy.setHarCaptureTypes(CaptureType.getAllContentCaptureTypes());
        proxy.enableHarCaptureTypes(CaptureType.getAllContentCaptureTypes());
        try {
            return ClientUtil.createSeleniumProxy(proxy);
        } catch (Exception e) {
            delay(5);
            return ClientUtil.createSeleniumProxy(proxy);
        }
    }
}
