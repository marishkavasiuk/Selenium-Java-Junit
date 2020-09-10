package com.ui.pages;

import com.ui.webdriver.DriverHelper;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class IssuePage extends RepositoryAbstractPage {

	@FindBy(id = "issue_title")
	private WebElement title;

	@FindBy(id = "issue_body")
	private WebElement comment;

	@FindBy(xpath = "//button[contains(text(), 'Submit new issue')]")
	private WebElement submitNewIssueBtn;

	@FindBy(xpath = "//*[@class = 'no-wrap'][contains(text(), 'just now')]")
	private WebElement timeJustAdded;

	@FindBy(xpath = "//*[contains(@class, 'State State--green')]")
	private WebElement statusOpen;

	/**
	 * Class constructor
	 * 
	 * @param driverHelper
	 *            The driver that will be used for navigation
	 * @throws IllegalStateException
	 *             If it's not expected page
	 */
	public IssuePage(DriverHelper driverHelper) {
		super(driverHelper);
		PageFactory.initElements(driverHelper.getDriver(), this);
	}

	public boolean areNewIssueElementsPresent() {
		return isElementPresents(title) && isElementPresents(comment)
				&& isElementPresents(submitNewIssueBtn);
	}

	public void addIssue(String titleStr, String commentStr) {
		title.sendKeys(titleStr);
		comment.sendKeys(commentStr);
		submitNewIssueBtn.click();

	}

	public boolean IsIssueJustAdded() {
		System.out.println("status " + statusOpen.getAttribute("innerText"));
		return isElementPresents(statusOpen);
	}

	/**
	 * Method to get expected page title
	 *
	 * @return expected page title
	 */
	@Override
	public String getExpectedTitle() {
		return "New Issue";
	}
}
