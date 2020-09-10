package com.ui.pages;

import com.ui.webdriver.DriverHelper;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class IssuesSectionPage extends RepositoryAbstractPage {

	@FindBy(xpath = "(//span[contains(text(), 'New issue')])")
	private WebElement btnAddIssue;

	@FindBy(xpath = "//*[@class='btn select-menu-button']")
	private WebElement filters;

	@FindBy(id = "js-issues-search")
	private WebElement searchField;

	@FindBy(xpath = "//*[@class = 'js-navigation-container js-active-navigation-container']")
	private List<WebElement> issuesList;

	@FindBy(xpath = "//*[@id='issue_1_link']")
	private WebElement addedIssue;

	/**
	 * Class constructor
	 * 
	 * @param driverHelper
	 *            The driver that will be used for navigation
	 * @throws IllegalStateException
	 *             If it's not expected page
	 */
	public IssuesSectionPage(DriverHelper driverHelper) {
		super(driverHelper);
	}

	public boolean areAllElementsPresent() {
		return isElementPresents(btnAddIssue) && isElementPresents(filters)
				&& isElementPresents(searchField);
	}

	public IssuePage addNewIssue() {
		btnAddIssue.click();
		return PageNavigator.getPage(IssuePage.class);
	}

	public boolean isIssuePresent(String title) {
		if(isElementPresents(addedIssue)) {
			return addedIssue.getAttribute("text").equalsIgnoreCase(title);
		}
		return false;
	}

	/**
	 * Method to get expected page title
	 *
	 * @return expected page title
	 */
	@Override

	public String getExpectedTitle() {
		return  null;
	}
}
