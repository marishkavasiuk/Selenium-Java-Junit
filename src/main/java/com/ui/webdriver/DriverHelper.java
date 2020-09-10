package com.ui.webdriver;

import com.ui.common.Config;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import static com.ui.common.Config.*;
import static com.ui.common.Constants.*;
import static java.awt.Toolkit.getDefaultToolkit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


public class DriverHelper {
    private final WebDriver driver;
    private JavascriptExecutor js;
    private RemoteWebDriver remoteDriver;
    private Actions actions;
    private Robot robot;

    private static final Logger logger = LoggerFactory.getLogger(DriverHelper.class);

    public DriverHelper(String type) {
        driver = DriverFactory.getDriverInstance(type);
        setDriverParameters();
    }

    private void logDriver(String message, Exception e) {
        StringTokenizer st = new StringTokenizer(e.getMessage(), "\n");
        logger.info(String.format("%s: %s", message, st.nextToken()));
    }

    private void setDriverParameters() {
        // setup driver options
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);

        // get remote WebDriver
        try {
            Field drvField = WebDriver.class.getDeclaredField("driver");
            drvField.setAccessible(true);
            remoteDriver = (RemoteWebDriver) drvField.get(driver);
        } catch (Exception e) {
            logger.info("Unable to init Remote Driver", e);
        }

        // get Javascript of WebDriver
        try {
            Field jsField = WebDriver.class.getDeclaredField("js");
            jsField.setAccessible(true);
            js = (JavascriptExecutor) jsField.get(driver);
        } catch (Exception e) {
            logger.error("Unable to init JavaScript", e);
        }

        try {
            robot = new Robot();
        } catch (AWTException e) {
            logger.warn("Unable to init robot", e);
        }

        actions = new Actions(driver);

        try {
            driver.manage().window().maximize();
        } catch (Exception e) {
            try {
                driver.manage().window().setSize(new Dimension(1600, 800));
                delay(3);
            } catch (Exception e1) {
                logger.warn("Unable to maximize Browser window", e);
            }
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public JavascriptExecutor getJs() {
        return js;
    }

    public RemoteWebDriver getRemoteDriver() {
        return remoteDriver;
    }

    public boolean isAlive() {
        return !driver.toString().contains("null");
    }

    public static void delay(double sec) {
        try {
            Thread.sleep((long) (sec * 1000));
        } catch (Exception e) {
            logger.error("Unexpected error", e);
        }
    }

    // *** driver methods
    public void openURL() {
        for (int i = 0; i < 3; i++) {
            driver.get(Config.URL);
            waitPageReady();
        if (driver.getCurrentUrl().contains(Config.URL)) {
            break;
        }
        else logger.warn("Unable to navigate on main URL, retry...");
        }
    }

    public void close() {
        DriverProxy.stopProxy();
        try {
            driver.quit();
            acceptAlert();
        } catch (Exception e) {
            logger.warn("Unable to quit browser", e);
        }
    }

    public void refresh() {
        closeAlert();
        try {
            driver.navigate().refresh();
            acceptAlert();
            waitPageReady();
        } catch (Exception e) {
            logger.warn("Unable to refresh Browser", e);
        }
    }

    public String getCurrentURL() {
        return driver.getCurrentUrl();
    }

    public void openPage(String url) {
        driver.navigate().to(url);
    }

    private void closeAlert() {
        try {
            driver.switchTo().alert().dismiss();
        } catch (Exception ignored) {
        }
    }

    public void acceptAlert() {
        try {
            driver.switchTo().alert().accept();
        } catch (Exception ignored) {
        }
    }

    // *** find methods
    public WebElement findElement(String xpath) {
        try {
            return driver.findElement(By.xpath(String.valueOf(xpath)));
        } catch (Exception e) {
            throw new RuntimeException(String.format(ERR_ELM_NOT_FOUND, xpath));
        }
    }

    public List<WebElement> findElements(String xpath) {
        if (driver == null) {
            return Collections.emptyList();
        }
        return driver.findElements(By.xpath(xpath));
    }

    public List<WebElement> findElementsByCss(String sclocator) {
        return driver.findElements(By.cssSelector(sclocator));
    }

    public WebElement findElementByCss(String sclocator) {
        return driver.findElement(By.cssSelector(sclocator));
    }

    public List<WebElement> findVisibleElements(String xpath) {
        List<WebElement> elements = new ArrayList<>();
        for (WebElement el : findElements(xpath)) {
            try {
                if (el.isDisplayed()) {
                    elements.add(el);
                }
            } catch (Exception e) {
                logDriver("Element not displayed", e);
            }
        }
        return elements;
    }

    public WebElement findFirstVisibleElement(String xpath) {
        List<WebElement> els = findVisibleElements(xpath);
        if (!els.isEmpty()) {
            return els.get(0);
        } else {
            throw new RuntimeException(String.format(ERR_ELM_NOT_FOUND, xpath));
        }
    }

    public int elementsCount(String xpath) {
        return findElements(xpath).size();
    }

    public String getVisibleElement(String xpath, int index) {
        int cnt = elementsCount(xpath);
        for (int i = 1, n = 1; i <= cnt; i++) {
            String xp = String.format("(%s)[%s]", xpath, i);
            if (setElementVisible(xp) && isDisplayed(xp) && (n++ == index)) {
                return xp;
            }
        }
        return xpath;       // original will be returned
    }

    public String getVisibleElement(String xpath) {
        int cnt = elementsCount(xpath);
        for (int i = cnt; i >= 1; i--) {
            String xp = String.format("(%s)[%s]", xpath, i);
            if (setElementVisible(xp) && isDisplayed(xp)) {
                return xp;
            }
        }
        return xpath;       // original will be returned
    }

    public String getVisibleEnabledElement(String xpath) {
        String xp;
        int cnt = elementsCount(xpath);
        for (int i = cnt; i >= 1; i--) {
            xp = String.format("(%s)[%s]", xpath, i);
            if (setElementVisible(xp) && isDisplayed(xp)) {
                String enable = getAttribute(xp + "/..", "class");
                if (!enable.contains("Disabled")) {
                    return xp;
                }
            }
        }
        return xpath;
    }

    public String getElementByZIndex(String xpath) {
        int cnt = elementsCount(xpath);
        int zi = 0, n = 0, index = cnt;
        for (int i = cnt; i >= 1; i--) {
            String xp = String.format("(%s)[%s]", xpath, i);
            if (!isDisplayed(xp)) {
                continue;
            }
            try {
                n = Integer.parseInt(getCssValue(xp, "z-index"));
            } catch (Exception e) {
                logDriver("Unable to get 'z-index'", e);
            }
            if (zi < n) {
                zi = n;
                index = i;
            }
        }
        return String.format("(%s)[%s]", xpath, index);
    }

    // *** wait methods
    public boolean isDisplayed(String xpath) {
        try {
            return findElement(xpath).isDisplayed();
        } catch (Exception e) {
            logDriver("Element not displayed", e);
        }
        return false;
    }

    public boolean isElementDisplayed(String xpath) {
        xpath = getVisibleElement(xpath);
        try {
            new WebDriverWait(driver, 0).until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            return true;
        } catch (Exception e) {
            logDriver("Element not displayed", e);
        }
        return false;
    }

    public boolean waitElementDisplayed(String xpath, int sec) {
        xpath = getVisibleElement(xpath);
        try {
            new WebDriverWait(driver, sec).until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            return true;
        } catch (Exception e) {
            logDriver("Element not displayed", e);
        }
        return false;
    }

    public boolean waitElementDisplayed(WebElement element , int sec) {
        try {
            new WebDriverWait(driver, sec).until(ExpectedConditions.elementToBeClickable(element));
            return true;
        } catch (Exception e) {
            logDriver("Element not displayed", e);
        }
        return false;
    }

    public boolean waitElementPresent(String xpath, int sec) {
        try {
            new WebDriverWait(driver, (long) sec).until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            return true;
        } catch (Exception e) {
            logDriver("Element not displayed", e);
        }
        return false;
    }

    public boolean waitElementVisibleWithJs(String cssSelector, int sec) {
        try {
            new WebDriverWait(driver, (long) sec).until(ExpectedConditions
                    .javaScriptThrowsNoExceptions("document.querySelector(\"" + cssSelector + "\").scrollIntoView(true)"));
            return true;
        } catch (Exception e) {
            logDriver("Element is not appeared", e);
        }
        return false;
    }

    public void waitElementsDisplayedNumberToBe(String xpath, int number) {
        try {
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.numberOfElementsToBe(By.xpath(xpath), number));
        } catch (Exception e) {
            logDriver("Number of elements displayed mismatch: ", e);
            throw new RuntimeException("Number of elements displayed mismatch: " + e.getMessage(), e);
        }
    }

    public boolean waitTextDisplayed(String xpath, String text) {
        for (int i = 0; i < TIMEOUT; i++) {
            if (getTextField(xpath).contains(text)) return true;
            delay(1);
        }
        return false;
    }

    public void waitTextNotDisplayed(String xpath, String text) {
        try {
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions
                    .not(ExpectedConditions.textToBePresentInElementLocated(By.xpath(xpath), text)));
        } catch (Exception e) {
            logDriver("Text is still displayed: ", e);
            throw new RuntimeException("Text is still displayed: " + e.getMessage(), e);
        }
    }

    public boolean waitElementDisplayed(String xpath) {
        return waitElementDisplayed(xpath, TIMEOUT);
    }

    public int waitElementsDisplayed(List<String> xpathes) {
        for (int time = 0; time < TIMEOUT * 3; time++) {
            for (int i = 0; i < xpathes.size(); i++) {
                if (waitElementDisplayed(xpathes.get(i), 0)) {
                    return i + 1;
                }
            }
        }
        return 0;
    }

    public boolean waitElementHidden(String xpath, int wait) {
        boolean waitProgress = xpath.contains("progress") || xpath.contains("loading");

        // wait until hidden
        for (int i = 0; i < wait; i++) {
            if (isElementDisplayed(xpath)) {
                delay(1);
            } else {
                if (i > 60 && waitProgress) {
                    logger.warn(String.format("Loading page is completed (%ss)", i), (Throwable) null);
                }
                return true;
            }
        }

        if (waitProgress) {
            logger.warn(String.format("Loading page took more than %s sec!", wait), (Throwable) null);
        }
        return false;
    }

    public boolean waitElementHidden(String xpath) {
        return waitElementHidden(xpath, TIMEOUT * 6);
    }

    public void waitElementClickable(String xpath) {
        try {
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        } catch (Exception e) {
            logDriver("Element not clickable", e);
        }
    }

    public String getAttribute(String xpath, String attribute) {
        try {
            return trimValue(findElement(xpath).getAttribute(attribute));
        } catch (WebDriverException ignored) {
        } catch (RuntimeException e) {
            logger.warn(String.format("Unable to get attribute '%s'", attribute), e);
        }
        return EMPTY;
    }


    public String getCssValue(String xpath, String value) {
        try {
            return findElement(xpath).getCssValue(value);
        } catch (WebDriverException ignored) {
        } catch (RuntimeException e) {
            logger.error(String.format("Unable to get css-value '%s'", value), e);
        }
        return EMPTY;
    }

    /**
     * setTextClipboardInEditor - Sets a value for an attribute of type Clob in the editor
     *
     * @param value to set the Clob field to
     */
    public String setTextClipboardInEditor(String xpath, String value) {
        // set the clipboard with the value of the clob field
        getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(value), null);

        // enabled editing in the editor
        doubleClickElement(xpath);
        pasteTextClipboard();

        // use robot in case of 1st attempt failed
        if (EMPTY.equals(getTextField(xpath))) {
            setClipboardTextByRobot(value);
        }

        return getTextField(xpath);
    }

    public void sendKeys(CharSequence keys) {
        actions.sendKeys(keys).build().perform();
    }

    public void sendCtrlKeys(CharSequence keys) {
        actions.keyDown(Keys.CONTROL).sendKeys(keys).keyUp(Keys.CONTROL).build().perform();
    }

    public void sendKeys(String xpath, CharSequence keys) {
        findElement(xpath).sendKeys(keys);
    }

    /**
     * getTextClipboardInClobEditor - Gets the text in the Clob attribute editor
     *
     * @author echreph (Chris McCormack)
     */
    private String getTextClipboardInClobEditor(String xpath) {

        //enable editing in the editor
        doubleClickElement(xpath);
        //select all text and copy
        copyTextClipboard();

        //return the contents of the clipboard
        Transferable transferable = getDefaultToolkit().getSystemClipboard().getContents(null);
        String clipboardText;
        try {
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                clipboardText = (String) transferable
                        .getTransferData(DataFlavor.stringFlavor);
                return clipboardText.replaceAll("[\n\r]", "");
            }
        } catch (Exception e) {
            logger.warn("Unexpected clipboard error", e);
        }
        return null;
    }

    /**
     * copyTextClipboard - copies the text in the Clob attribute editor
     */
    private void copyTextClipboard() {
        sendCtrlKeys("a");
        sendCtrlKeys("c");
    }

    private void pasteTextClipboard() {
        sendCtrlKeys("a");
        sendCtrlKeys("v");
    }

    public void clearField(String xpath) {
        WebElement el = findElement(xpath);
        el.clear();
    }

    public void setTextField(String xpath, String value) {
        try {
            WebElement el = findElement(xpath);
            el.clear();
            if (StringUtils.isNotEmpty(value)) {
                js.executeScript("arguments[0].value='" + value.substring(0, value.length() - 1) + "'", el);
                delay(0.1);
                el.sendKeys(value.substring(value.length() - 1));
                logger.info("Text field was set: " + value);
            } else {
                el.sendKeys(Keys.DELETE);
                logger.info("Text field was set to empty.");
            }
        } catch (WebDriverException ignored) {
        } catch (RuntimeException e) {
            logger.warn(String.format("Unable to set value '%s'", value), e);
        }
    }

    public String getInputFieldValue(String xpath) {
        String value = "";
        String id = findElement(xpath).getAttribute("id");
        try {
            value = (String) js.executeScript(String.format("return document.getElementById('%s').value", id));
        } catch (Exception e) {
            logDriver("Unable to set element visible", e);
        }
        return value;
    }


    public void setTextFieldOneCharAtATime(String xpath, String value) {
        try {
            WebElement el = findElement(xpath);
            el.clear();
            el.sendKeys(value);
        } catch (WebDriverException ignored) {
        } catch (RuntimeException e) {
            logger.warn(String.format("Unable to set value '%s'", value), e);
        }
    }


    public String getTextField(String xpath) {
        String res = EMPTY;
        try {
            res = trimValue(findElement(xpath).getText());
        } catch (WebDriverException ignored) {
        } catch (RuntimeException e) {
//            logWarning("Unable to get text of element", e);
            res = NOT_FOUND;
        }

        res = res.isEmpty() ? getAttribute(xpath, "value") : res;
        res = res.isEmpty() ? getAttribute(xpath, "textContent") : res;
        res = res.isEmpty() && isDisplayed(xpath + "//input") ? getAttribute(xpath + "//input", "value") : res;

        return res;
    }

    public boolean setElementVisible(String xpath) {
        try {
            js.executeScript("arguments[0].scrollIntoView(true);", findElement(xpath));
            return true;
        } catch (Exception e) {
            logDriver("Unable to set element visible", e);
        }
        return false;
    }

    public boolean setElementVisibleDown(String xpath) {
        try {
            js.executeScript("arguments[0].scrollIntoView(false);", findElement(xpath));
            return true;
        } catch (Exception e) {
            logDriver("Unable to set element visible", e);
        }
        return false;
    }

    public List<String> moveToElementUntilDisplayed(String xpathEl, int x, int y, String xpathUntil) {
        List<String> res = new ArrayList<>();
        int width = findElement(xpathEl).getSize().getWidth();
        for (int offset = -10; offset < width; offset += 10) {
            try {
                actions.moveToElement(findElement(xpathEl), x + offset, y).build().perform();
                delay(Config.DELAY);
                if (isDisplayed(xpathUntil)) {
                    res.add(getTextField(xpathUntil));         // save tooltip details
                    res.add(String.valueOf(x + offset));       // save X positions
                    break;
                }
            } catch (WebDriverException ignored) {
            } catch (RuntimeException e) {
                throw new RuntimeException("Unable to move and click element - " + xpathEl, e);
            }
        }

        return res;
    }

    public void moveToElement(String xpath, int x, int y) {
        try {
            actions.moveToElement(findElement(xpath), x, y).build().perform();
        } catch (WebDriverException ignored) {
        } catch (RuntimeException e) {
            throw new RuntimeException("Unable to move to element - " + xpath, e);
        }
    }

    public void moveToAndClickElement(String xpath, int x, int y, double delay) {
        try {
            actions.moveToElement(findElement(xpath), x, y).click().build().perform();
            logger.info("Element was clicked: " + xpath);
            delay(delay);
        } catch (WebDriverException ignored) {
        } catch (RuntimeException e) {
            throw new RuntimeException("Unable to move and click element - " + xpath, e);
        }
    }

    public void moveToAndClickElement(WebElement element) {
        try {
            actions.moveToElement(element).click().build().perform();
        } catch (WebDriverException ignored) {
        } catch (RuntimeException e) {
            throw new RuntimeException("Unable to move and click element - " + element.getLocation().toString(), e);
        }
    }

    public void moveToAndDoubleClickElement(String xpath, int x, int y, double delay) {
        try {
            actions.moveToElement(findElement(xpath), x, y).build().perform();
            actions.click().build().perform();
            actions.click().build().perform();
            delay(delay);
        } catch (WebDriverException ignored) {
        } catch (RuntimeException e) {
            throw new RuntimeException("Unable to move and click element - " + xpath, e);
        }
    }

    public void moveToAndClickElement(String xpath) {
        moveToAndClickElement(xpath, 1, 1, Config.DELAY);
    }

    public void dragElement(String xpath, int x, int y) {
        actions.moveToElement(findElement(xpath), 2, 1).clickAndHold().moveByOffset(x, y).release().build().perform();
        delay(2);
    }

    public void dragToElement(String xpath1, String xpath2) {
        if (BROWSER_FF) dragToElement(xpath1, xpath2, -10, 100);
        else dragToElement(xpath1, xpath2, 10, 100);
    }

    public void dragToElement(String xpath1, String xpath2, int x, int y) {
        int xx = -1, yy = 1;
        if (BROWSER_FF) yy = 10;
        actions.moveToElement(findElement(xpath1), xx, yy)
                .clickAndHold()
                .moveToElement(findElement(xpath2), x, y)
                .moveToElement(findElement(xpath2), x, y)
                .release().build().perform();
        delay(3);
    }

    public void dragToElement(String xpath1, String xpath2, int x, int y, int xx, int yy) {
        if (BROWSER_FF) yy = 10;
        actions.moveToElement(findElement(xpath1), xx, yy)
                .clickAndHold()
                .moveToElement(findElement(xpath2), x, y)
                .moveToElement(findElement(xpath2), x, y)
                .release().build().perform();
        delay(3);
    }

    public void dragToTrailElement(String xpath1, String xpath2) {
        actions.moveToElement(findElement(xpath1), 2, 2)
                .clickAndHold()
                .moveToElement(findElement(xpath2), 2, 10)
                .release().build().perform();
        delay(3);
    }

    public void scrollToHome() {
        sendKeys(Keys.HOME);
    }

    public void scrollToEnd() {
        sendKeys(Keys.END);
    }

    private String getPageSource() {
        try {
            return driver.getPageSource();
        } catch (Exception e) {
            logDriver("Unable to get page source", e);
        }
        return EMPTY;
    }

    private boolean scrollPageDown() {
        String state = getPageSource();
        sendKeys(Keys.PAGE_DOWN); // do action
        return !getPageSource().equals(state); // verify state is changed: false - no changed (page is the same)
    }

    private boolean scrollPageDown(String xpath) {
        String state = getPageSource();  // get current state
        sendKeys(xpath, Keys.PAGE_DOWN); // do action
        return !getPageSource().equals(state); // verify state is changed: false - no changed (page is the same)
    }

    public boolean scrollDownToElement(String xpath, boolean home) {
        if (setElementVisible(xpath)) {
            return true;
        }
        if (home) {
            scrollToHome();
        }
        for (int i = 1; i < TIMEOUT * 3; i++) {
            if (setElementVisible(xpath)) {
                return true;
            }
            if (!scrollPageDown()) {
                break;   // break if page has not been changed
            }
        }
        return false;
    }

    public boolean scrollDownToElement(String xpath) {
        return scrollDownToElement(xpath, true);
    }

    public void scrollToObjectInDA(String xpath, String objectPath) {
        String path = xpath + "//div[contains(@eventproxy, 'scroll_thumb')]//img[contains(@src, 'blank')]";
        waitElementClickable(path);
        WebElement draggablePartOfScrollbar;
        for (int i = 1; i < 100; i++) {
            draggablePartOfScrollbar = driver.findElement(By.xpath(path));
            actions.moveToElement(draggablePartOfScrollbar).clickAndHold().moveByOffset(0, 10).release().perform();
            if (isElementDisplayed(objectPath)) {
                break;
            }
        }
    }

    public void scrollBack(String xpath) {
        String path = xpath + "//div[contains(@eventproxy, 'scroll_thumb')]//img[contains(@src, 'blank')]";
        waitElementClickable(path);
        WebElement draggablePartOfScrollbar = driver.findElement(By.xpath(path));
        actions.moveToElement(draggablePartOfScrollbar).clickAndHold().moveByOffset(0, -300).release().perform();
    }

    public void doubleClickElement(String xpath) {
        try {
            actions.doubleClick(findElement(xpath)).build().perform();
        } catch (WebDriverException ignored) {
        } catch (RuntimeException e) {
            throw new RuntimeException("Unable to double click element - " + xpath, e);
        }
    }

    //alternative double click, for cases when doubleClick command doesn't work.
    public void doubleClickElementAlternative(String xpath) {
        try {
            WebElement element = findElement(xpath);
            actions.moveToElement(element).build().perform();
            actions.click(findElement(xpath)).build().perform();
            delay(0.1);
            actions.click(findElement(xpath)).build().perform();
        } catch (WebDriverException ignored) {
        } catch (RuntimeException e) {
            throw new RuntimeException("Unable to double click element - " + xpath, e);
        }
    }

    public void contextClick(String xpath) {
        try {
            actions.moveToElement(findElement(xpath), 2, 1).contextClick().build().perform();
            delay(DELAY);
        } catch (WebDriverException ignored) {
        } catch (RuntimeException e) {
            throw new RuntimeException("Unable to context click element - " + xpath, e);
        }
    }

    public void contextClickWithOffset(String xpath, int xOffset, int yOffset) {
        try {
            actions.moveToElement(findElement(xpath), xOffset, yOffset).contextClick().build().perform();
            delay(DELAY);
        } catch (WebDriverException ignored) {
        } catch (RuntimeException e) {
            throw new RuntimeException("Unable to context click element - " + xpath, e);
        }
    }

    public void contextClickElement(String xpath) {
        actions.contextClick(findElement(xpath)).build().perform();
    }

    public void clickElement(String xpath) {
        clickElement(xpath, Config.DELAY);
    }

    public void clickElement(WebElement element) {
        element.click();
    }

    public void clickElement(String xpath, double delay) {
        List<WebElement> els = findElements(xpath);
        if (els.isEmpty()) {
            throw new RuntimeException(String.format(ERR_ELM_NOT_FOUND, xpath));
        }
        for (WebElement el : els) {
            try {
                el.click();
                delay(delay);
                break;
            } catch (WebDriverException ignored) {
            } catch (Exception e) {
                logger.warn("Unable to click element", e);
            }
        }
    }

    public boolean clickDisplayedElement(String xpath) {
        if (!isElementDisplayed(xpath)) {
            return false;
        }
        clickElement(getVisibleElement(xpath), 1);
        return true;
    }

    public boolean clickDisplayedElement(String xpath, int wait) {
        return waitElementDisplayed(xpath, wait) && clickDisplayedElement(xpath);
    }

    // Waits for presence of specified element as confirmation of successful click
    // retries 3 times before fail
    public void clickConfirmed(String clickOn, String waitFor) {
        clickConfirmed(clickOn, waitFor, 3);
    }

    private void clickConfirmed(String clickOn, String waitFor, int i) {
        try {
            actions.moveToElement(findElement(clickOn)).click().build().perform();
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.presenceOfElementLocated(By.xpath(waitFor)));
        } catch (Exception e) {
            if (i > 0) {
                clickConfirmed(clickOn, waitFor, i--);
            } else {
                fail("Click failed - " + clickOn);
            }
        }
    }

    public void clickVisibleElement(String xpath) {
        String xp = getVisibleElement(xpath);
        if (!xpath.equals(xp)) {
            clickElement(xp);
        } else {
            throw new RuntimeException(String.format(ERR_ELM_NOT_FOUND, xpath));
        }
    }

    public void selectMultipleElements(List<String> xpathes) {
        selectMultipleElements(xpathes, -1, 1);
    }

    public void selectMultipleElements(List<String> xpathes, int x, int y) {
        if (BROWSER_FF) y = 10;
        if (xpathes.isEmpty()) return;
        String checkBoxXpath = "//span[contains(@style,'unchecked')]";

        // elements with checkbox to select
        setElementVisible(xpathes.get(0));
        if (isDisplayed(xpathes.get(0) + checkBoxXpath)) {
            for (String xp : xpathes) {
                clickElement(xp + checkBoxXpath);
            }
        } else {
            moveToAndClickElement(xpathes.get(0), x, y, Config.DELAY);
            if (xpathes.size() > 1) {
                pressControl();
                for (int i = 1; i < xpathes.size(); i++) {
                    moveToAndClickElement(xpathes.get(i), x, y, Config.DELAY);
                }
                releaseControl();
            }
        }
    }

    public void selectRangeElements(String start, String end) {
        setElementVisible(start);
        moveToAndClickElement(start, 2, 2, 0.5);
        actions.keyDown(Keys.SHIFT).build().perform();

        setElementVisible(end);
        moveToAndClickElement(end, 2, 2, 0.5);
        actions.keyUp(Keys.SHIFT).build().perform();
    }

    public void pressControl() {
        //need to use different method to select Control on IE - PB 1/29/16
        actions.keyDown(Keys.CONTROL).build().perform();
    }

    public void releaseControl() {
        //need to use different method to select Control on IE - PB 1/29/16
        actions.keyUp(Keys.CONTROL).build().perform();
    }

    public void pressShift() {
        //need to use different method to select Control on IE - PB 1/29/16
        actions.keyDown(Keys.SHIFT).build().perform();
    }

    public void releaseShift() {
        //need to use different method to select Control on IE - PB 1/29/16
        actions.keyUp(Keys.SHIFT).build().perform();
    }

    public void selectMultipleElements(String xpath) {
        int cnt = elementsCount(xpath);
        if (cnt == 0) {
            return;
        }

        // select first
        scrollDownToElement(xpath + "[" + 1 + "]");
        clickElement(xpath + "[" + 1 + "]");

        // select last
        actions.keyDown(Keys.SHIFT).build().perform();
        scrollDownToElement(xpath + "[" + cnt + "]");
        clickElement(xpath + "[" + cnt + "]");
        actions.keyUp(Keys.SHIFT).build().perform();
    }

    public void setClipboardTextByRobot(String text) {
        getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        //added to handle Firefox
        delay(3);

        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    private boolean isSystemDone() {
        return (Boolean) js.executeScript("return isc.AutoTest.isSystemDone(true)");
    }

    // Returns whether the loaded page is in a consistent state with no pending operations.
    public void waitForDone() {
        for (int i = 0; i < 100; i++) {
            if (isSystemDone()) {
                return;
            }
            delay(0.1);
        }
        fail("Wait for done failed!");
    }

    public void verifyNewBrowserTab(String pageTitle) {
        List<String> browserTabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(browserTabs.get(1));
        assertEquals(driver.getTitle(), pageTitle);
        driver.close();
        driver.switchTo().window(browserTabs.get(0));
    }

    public void switchToWindow(String url) {
        for (String win : driver.getWindowHandles()) {
            driver.switchTo().window(win);
            if (driver.getCurrentUrl().contains(url)) {
                break;
            }
        }
    }

    public void closeWindowsExceptOf(String... urls) {
        String mainWnd = "";
        for (String win : driver.getWindowHandles()) {
            WebDriver temp = driver.switchTo().window(win);
            String url = driver.getCurrentUrl();

            boolean res = false;
            for (String u : urls) {
                if (url.contains(u)) {
                    res = true;
                    break;
                }
            }

            // close current window when no matches
            if (!res) {
                temp.close();
            } else {
                mainWnd = win;
            }
        }

        driver.switchTo().window(mainWnd);
    }

    public void closeWindow(String url) {
        String mainWnd = "";
        for (String win : driver.getWindowHandles()) {
            WebDriver temp = driver.switchTo().window(win);
            if (driver.getCurrentUrl().contains(url)) {
                temp.close();
            } else {
                mainWnd = win;
            }
        }

        driver.switchTo().window(mainWnd);
    }

    public void switchToFrame(String frameName) {
        driver.switchTo().frame(frameName);
    }

    public void waitPageReady() {
        if (driver == null) {
            return;
        }

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.jsReturnsValue("return document.readyState==\"complete\";"));
    }
}