package com.ui.pages;

import com.ui.webdriver.DriverHelper;
import com.ui.common.Config;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Class represents page with Repository settings
 */
public class OptionsPage extends RepositoryAbstractPage {

	@FindBy(xpath = "//summary[contains(@class, 'danger') and contains(., 'Delete this repository')]")
	private WebElement delete;

	@FindBy(xpath = "//input[contains(@type, 'text')and contains(@aria-label, 'Type in the name of the repository to confirm that you want to delete this repository.')]")
	private WebElement deleteField;

	/**
	 * Class constructor
	 * 
	 * @param driverHelper
	 *            The driver that will be used for navigation
	 * @throws IllegalStateException
	 *             If it's not expected page
	 */
	public OptionsPage(DriverHelper driverHelper) {
		super(driverHelper);
		PageFactory.initElements(driverHelper.getDriver(), this);
	}

	/**
	 * Checks if delete option presents
	 *
	 * @return True if delete option presents
	 */
	public boolean isDeletePresents() {
		return isElementPresents(delete);
	}

	/**
	 * Method to delete repository
	 *
	 * @param repName
	 *            repository name
	 *
	 * @return An instance of {@link HomePage} class
	 */
	public HomePage deleteRepository(String repName) {
		driverHelper.waitElementDisplayed(delete, 10);
		delete.click();
		System.out.println(isElementPresents(deleteField));
		deleteField.sendKeys(Config.USER + "/" + repName);
		deleteField.submit();
		return PageNavigator.getPage(HomePage.class);
	}

	/**
	 * Method to get expected page title
	 *
	 * @return expected page title
	 */
	@Override
	public String getExpectedTitle() {
//		return USER + "/" + repName + ": NewDescription";
		return null;
	}
}
