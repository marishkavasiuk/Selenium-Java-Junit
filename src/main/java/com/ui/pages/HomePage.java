package com.ui.pages;

import com.ui.webdriver.DriverHelper;
import com.ui.common.Config;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

/**
 * Class represents Home page that opens after successful login
 */
public class HomePage extends Page {

	@FindBy(name = "q")
	private WebElement search;

	@FindBy(id = "dashboard-repos-filter-left")
	private WebElement searchRepos;

	@FindBy(id = "your_repos")
	private WebElement repositories;

	@FindBy(xpath = "//*[contains(@class, 'btn btn-sm btn-primary')]")
	private WebElement newRepoButton;

	@FindBy(xpath = "//ul[@class='list-style-none']")
	private List<WebElement> repoList;

	@FindBy(xpath = "//a[@href='/notifications']")
	private WebElement notificationsIcon;

	/**
	 * Class constructor
	 * 
	 * @param driverHelper
	 *            The driver that will be used for navigation
	 * @throws IllegalStateException
	 *             If it's not expected page
	 */
	public HomePage(DriverHelper driverHelper) {
		super(driverHelper);
		PageFactory.initElements(driverHelper.getDriver(), this);
	}

	/**
	 * Checks that login was successful
	 *
	 * @return true if welcome message, user links and logout icon presents on
	 *         the page
	 */
	public boolean isLoginSuccessed() {
		return isElementPresents(searchRepos)
				&& isElementPresents(avatarLink);
	}

	/**
	 * Checks that button for creating new repository presents on the page
	 *
	 * @return true if button for creating new repository presents on the page
	 */
	public boolean isNewRepoButtonPresents() {
		return isElementPresents(newRepoButton);
	}

	/**
	 * Clicks the button for creating new repository
	 *
	 * @return An instance of {@link CreateRepositoryPage} class
	 */
	public CreateRepositoryPage createNewRepository() {
		newRepoButton.click();
		return PageNavigator.getPage(CreateRepositoryPage.class);
	}

	/**
	 * Check that repository was just successfully deleted
	 *
	 * @param repName
	 *            The name of deleted repository
	 * @return true is message about successful deleting presents and if deleted
	 *         repository is not presented in the list of existing repositories
	 */
	public boolean isRepositoryJustDeleted(String repName) {
		boolean oldRepoDeleted = true;
		for (WebElement element : repoList) {
			if (element.getAttribute("innerText").equals(Config.USER + "/" + repName)) {
				oldRepoDeleted = false;
			}
		}
		return oldRepoDeleted;
	}

	/**
	 * Checks if notifications icon presents on the page
	 *
	 * @return True if notifications icon presents on the page
	 */
	public boolean isNotificationsIconPresents() {
		return isElementPresents(notificationsIcon);
	}

	/**
	 * Navigates to the Notifications page
	 *
	 * @return An instance of {@link NotificationsPage} class
	 */
	public NotificationsPage seeNotifications() {
		notificationsIcon.click();
		return PageNavigator.getPage(NotificationsPage.class);
	}

	/**
	 * Check if search field presents on he page
	 *
	 * @return True if search field presents on he page
	 */
	public boolean isSearchPresents() {
		return isElementPresents(search);
	}

	/**
	 * Method to get expected page title
	 *
	 * @return expected page title
	 */
	@Override
	public String getExpectedTitle() {
		return "GitHub";
	}
}
