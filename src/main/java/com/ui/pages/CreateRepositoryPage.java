package com.ui.pages;

import com.ui.webdriver.DriverHelper;
import com.ui.common.Constants;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.concurrent.TimeUnit;

/**
 * Class represents page with the form for creating new repository
 */
public class CreateRepositoryPage extends Page {

	@FindBy(name = "q")
	private WebElement search;

	@FindBy(id = "repository_name")
	private WebElement repNameElement;

	@FindBy(id = "repository_description")
	private WebElement repDescription;

	@FindBy(xpath = "//*[contains (text(), 'Create repository')]")
	private WebElement repSubmit;

	@FindBy(id = "repository-owner")
	private WebElement owner;

	@FindBy(id = "repository_visibility_public")
	private WebElement publicRep;

	@FindBy(id = "repository_visibility_private")
	private WebElement privateRep;

	@FindBy(id = "repository_auto_init")
	private WebElement autoInit;

	@FindBy(id = "repository_gitignore_template_toggle")
	private WebElement addGitignore;

	@FindBy(xpath = "(//*[@class='btn btn-sm select-menu-button'])[1]")
	private WebElement chooseGitignore;

	@FindBy(xpath = "(//*[@class='btn btn-sm select-menu-button'])[2]")
	private WebElement chooseLicense;

	@FindBy(id = "repository_license_template_toggle")
	private WebElement addLicense;

	@FindBy(xpath = "//button[contains(@class, 'sign-out-button')]")
	private WebElement logout;

	@FindBy(xpath = "(//*[@class='select-menu-item'])[1]")
	private WebElement chooseOwner;

	private static final String gitignoreInput= "//span[contains(@class, 'text-normal select-menu-item-text') and text()='%s']";

	/**
	 * Class constructor
	 *
	 * @param driverHelper The driver that will be used for navigation
	 * @throws IllegalStateException If it's not expected page
	 */
	public CreateRepositoryPage(DriverHelper driverHelper) {
		super(driverHelper);
		PageFactory.initElements(driverHelper.getDriver(), this);
	}

	/**
	 * Checks if the form for creating new repository presents on the page
	 *
	 * @return true if all form elements present on the page
	 */
	public boolean isNewRepFormExists() {
		return isElementPresents(repNameElement)
				&& isElementPresents(repDescription)
				&& isElementPresents(repSubmit)
				&& isElementPresents(owner)
				&& isElementPresents(publicRep)
				&& isElementPresents(privateRep)
				&& isElementPresents(autoInit)
				&& isElementPresents(addGitignore)
				&& isElementPresents(addLicense);
	}

	/**
	 * Log out
	 *
	 * @return An instance of {@link StartPage} class
	 */
	public StartPage logout() {
		logout.click();
		return PageNavigator.getPage(StartPage.class);
	}

	/**
	 * Method to get the page with just created repository.
	 *
	 * @param repName        New repository name
	 * @param repDescr Repository description
	 * @param addReadme      If true, initialize repository with a README
	 * @param gitignore      Select gitignore from the dropdown
	 * @return An instance of {@link RepositoryPage} class
	 */
	public RepositoryPage createRepository(String repName,
										   String repDescr, boolean addReadme, String gitignore
										   ) {
		repNameElement.sendKeys(repName);
		repDescription.sendKeys(repDescr);
		if (addReadme)
			autoInit.click();
		if (null != gitignore) {
			addGitignore.click();
			chooseGitignore.click();
			driverHelper.findElement(String.format(gitignoreInput, gitignore)).click();
		}
		driverHelper.waitElementDisplayed(repSubmit, 10);
		repSubmit.click();
		driverHelper.getDriver().manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		Constants.setRepositoryName(repName);
		return PageNavigator.getPage(RepositoryPage.class);
	}

	/**
	 * Method to get expected page title
	 *
	 * @return expected page title
	 */
	@Override
	public String getExpectedTitle() {
		return "Create a New Repository";
	}
}
