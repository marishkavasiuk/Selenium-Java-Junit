package com.ui.pages;

import com.ui.webdriver.DriverHelper;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static com.ui.common.Constants.*;
import static com.ui.pages.PageNavigator.getPage;

/**
 * Base class for all pages
 */
public abstract class Page {
	protected DriverHelper driverHelper;
	private static final Logger logger = LoggerFactory.getLogger(Page.class);

	/**
	 * Class constructor
	 * 
	 * @param driverHelper
	 *            The driver that will be used for navigation
	 * @throws IllegalStateException
	 *             If it's not expected page
	 */
	public Page(DriverHelper driverHelper) {
		this.driverHelper = driverHelper;
		String title = driverHelper.getTitle().trim();
		String expTitle = (String) getExpectedTitle();
		System.out.println("title: " + title);
		if (null != expTitle && !title.contains(expTitle)) {
			throw new IllegalStateException("This is not the " + expTitle
					+ ", this is " + title);
		}
		PageFactory.initElements(driverHelper.getDriver(), this);
	}

	public abstract CharSequence getExpectedTitle();

	/**
	 * presents in new design
	 */
	@FindBy(xpath = "//img[@class = 'avatar avatar-user ']")
	protected WebElement avatarLink;

	@FindBy(name = "q")
	protected WebElement search;

	@FindBy(xpath = "//*[@class='dropdown-item dropdown-signout']")
	protected WebElement logout;

	@FindBy(xpath = "//a[@class='Header-link']")
	private WebElement homeIcon;

	/**
	 * Checks if the element presents on the page and visible
	 * 
	 * @param element
	 *
	 *            element to check
	 *
	 * @return true if the element presents on the page and visible
	 */
	protected boolean isElementPresents(WebElement element) {
		try {
			return element.isDisplayed();
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	/**
	 * The method to logout
	 *
	 * @return An instance of {@link Page} class after logout
	 */
	public Page logout() {
		if (isElementPresents(avatarLink)) {
			avatarLink.click();
			logout.click();
			return this;
		} else {
			System.out.println("Logout is failed");
			return this;
		}
	}

	/**
	 * Method to search
	 *
	 * @param query
	 *            Search query
	 * @return An instance of {@link Page} class with search result
	 */
	public SearchPage search(String query) {
		search.clear();
		search.sendKeys(query);
		search.submit();
		return getPage(SearchPage.class);
	}

	public String getTextField(String xpath) {
		String res = EMPTY;
		try {
			res = trimValue(driverHelper.findElement(xpath).getText());
		} catch (WebDriverException ignored) {
		} catch (RuntimeException e) {
            logger.warn("Unable to get text of element", e);
			res = NOT_FOUND;
		}

		res = res.isEmpty() ? driverHelper.getAttribute(xpath, "value") : res;
		res = res.isEmpty() ? driverHelper.getAttribute(xpath, "textContent") : res;
		res = res.isEmpty() && driverHelper.isDisplayed(xpath + "//input") ? driverHelper.getAttribute(xpath + "//input", "value") : res;

		return res;
	}

	public HomePage backToHomePage() {
		homeIcon.click();
		return PageNavigator.getPage(HomePage.class);
	}

}
