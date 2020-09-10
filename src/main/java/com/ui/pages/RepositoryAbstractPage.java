package com.ui.pages;

import com.ui.webdriver.DriverHelper;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.concurrent.TimeUnit;


/**
 * Base class for pages that contain functions for working with repository
 */
public abstract class RepositoryAbstractPage extends Page {

	@FindBy(xpath = "//*[@id='js-repo-pjax-container']/div[1]/div/div/h1")
	protected WebElement repNameElement;

	@FindBy(xpath = "//*[text()='Code']")
	protected WebElement code;

	@FindBy(xpath = "//span[text()='Issues']")
	protected WebElement issues;

	@FindBy(xpath = "//*[text()='Pull requests']")
	protected WebElement pullRequests;

	@FindBy(xpath = "//a[contains (@href, '/wiki')]")
	protected WebElement wiki;

	@FindBy(xpath = "//a[contains(@href, 'settings')and not(contains(@class,'dropdown-item'))]")
	protected WebElement settings;

	@FindBy(xpath = "//a[contains (@href, '/projects')]")
	protected WebElement projects;

	@FindBy(xpath = "//a[contains (@href, '/security')]")
	protected WebElement security;

	@FindBy(xpath = "//a[contains (@href, '/pulse')]")
	protected WebElement insights;

	public RepositoryAbstractPage(DriverHelper driverHelper) {
		super(driverHelper);
		PageFactory.initElements(driverHelper.getDriver(), this);
	}

	/**
	 * Check if all sections present on the page
	 * 
	 * @return True if all sections present on the page
	 */
	public boolean areRepSectionsPresent() {
		return isElementPresents(repNameElement) && isElementPresents(code)
				&& isElementPresents(issues) && isElementPresents(pullRequests)
				&& isElementPresents(wiki) && isElementPresents(settings)
		        && isElementPresents(insights)
				&& isElementPresents(projects) && isElementPresents(security);
	}

	/**
	 * Navigate to the page with repository settings
	 * 
	 * @return An instance of {@link OptionsPage} class with repository settings
	 */
	public OptionsPage goToSettings() {
		settings.click();
		driverHelper.getDriver().manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		return PageNavigator.getPage(OptionsPage.class);
	}

	/**
	 * Navigate to the page with issues
	 * 
	 * @return An instance of {@link IssuesSectionPage} class
	 */
	public IssuesSectionPage goToIssues(){
		issues.click();
		System.out.println("Click issues " + issues.getText());
		return PageNavigator.getPage(IssuesSectionPage.class);
	}

	/**
	 * Navigate to the page with projects
	 *
	 * @return An instance of {@link ProjectsSectionPage} class
	 */
	public ProjectsSectionPage goToProjects(){
		projects.click();
		return PageNavigator.getPage(ProjectsSectionPage.class);
	}

	/**
	 * The method to get repository name
	 * 
	 * @return repository name
	 */
	public String getFullRepositoryName() {
		return this.repNameElement.getAttribute("innerText");
	}


}
