package com.ui.pages;

import com.ui.webdriver.DriverHelper;
import com.ui.common.Config;
import com.ui.common.Constants;
import org.openqa.selenium.support.PageFactory;

/**
 * Class represents page with repository
 */
public class RepositoryPage extends RepositoryAbstractPage {

	/**
	 * Class constructor
	 * 
	 * @param driverHelper
	 *            The driver that will be used for navigation
	 * @throws IllegalStateException
	 *             If it's not expected page
	 */
	public RepositoryPage(DriverHelper driverHelper) {
		super(driverHelper);
		PageFactory.initElements(driverHelper.getDriver(), this);
	}

	/**
	 * Method to get expected page title
	 *
	 * @return expected page title
	 */
	@Override
	public String getExpectedTitle() {
		return Config.USER + "/" + Constants.repName + ": NewDescription";
	}

	public HomePage deleteRepository(String repName, RepositoryPage repositoryPage) {
		OptionsPage optionsPage = repositoryPage.goToSettings();
		HomePage homePage = optionsPage.deleteRepository(repName);
		return homePage;
	}
}
