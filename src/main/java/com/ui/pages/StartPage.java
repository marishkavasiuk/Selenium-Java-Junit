package com.ui.pages;

import com.ui.webdriver.DriverHelper;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Class represents Start page
 */
public class StartPage extends Page {

	@FindBy(xpath = "//a[@href='/login']")
	private WebElement login;

	/**
	 * Class constructor
	 * 
	 * @param driverHelper
	 *            The driver that will be used for navigation
	 * @throws IllegalStateException
	 *             If it's not expected page
	 */
	public StartPage(DriverHelper driverHelper) {
		super(driverHelper);
		PageFactory.initElements(driverHelper.getDriver(), this);
	}

	/**
	 * Method to get the page with login form
	 *
	 * @return An instance of {@link LoginPage} class
	 */
	public LoginPage navigateToLogin() {
		if(isLoginPresents())
		login.click();
		return PageNavigator.getPage(LoginPage.class);
	}

	/**
	 * Check if link to the Login page presents on the page
	 *
	 * @return True if link to the Login page presents
	 */
	public boolean isLoginPresents() {
		return isElementPresents(login);
	}

	/**
	 * Method to get expected page title
	 *
	 * @return expected page title
	 */
	@Override
	public CharSequence getExpectedTitle() {
		return "GitHub";
	}
}
