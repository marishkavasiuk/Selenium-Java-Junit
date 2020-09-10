package com.qa.ui.test.tests;

import com.ui.common.AbstractBaseSeleniumTest;
import com.ui.pages.*;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import static com.ui.pages.PageNavigator.getPage;
import static java.lang.Boolean.parseBoolean;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Feature("Git basic functionality")
@DisplayName("Git basic functionality")
@Tag("Regression")
public class GitTest extends AbstractBaseSeleniumTest {
	private SearchPage searchPage;
	private boolean passedWithCertainData;
	private boolean passedSearchTest = true;
	private LoginPage loginPage;
	private NotificationsPage notificationsPage;
	private HomePage homePage;

	@BeforeEach
	public void beforeTest() {
		homePage = getPage(HomePage.class);
	}

	/**
	 * Tests correct login.
	 * <p>
	 * 1. Go to the login page <br>
	 * 2. Check login form is presented <br>
	 * 3. Try to login with incorrect data and check that error message appears
	 * <br>
	 * 4. Login with correct data, check that login was successful <br>
	 * 5. Logout, check that logout was successful
	 */

	@Test
	@Description("Verify login")
	@Severity(SeverityLevel.CRITICAL)
	public void testLogin() {
		homePage.logout();
		loginPage = getPage(StartPage.class).navigateToLogin();
		loginPage.isLoginFormPresents();
		loginPage.loginAsExpectingError("qqq", "qqq");
        loginPage.login();
	}

	/**
	 * Tests notification.
	 * <p>
	 * 1. Log in, check that notifications icon presents on the home page <br>
	 * 2. Click the notifications icon, check that all sections present <br>
	 */
	@Test
	@Description("Verify notifications")
	@Severity(SeverityLevel.CRITICAL)
	public void testNotifications() {
		notificationsPage = homePage.seeNotifications();
		notificationsPage.isNoticicationsPresent();
		notificationsPage.backToHomePage();
	}

	/**
	 * Tests correct search.
	 * <p>
	 * 1. Go to the login page and check that it contains search field <br>
	 * 2. Fill the search field with the query and check that search result
	 * appears (if no search result, then message appears)<br>
	 */
	@ParameterizedTest
	@Description("Verify search")
	@Severity(SeverityLevel.CRITICAL)
	@CsvFileSource(resources = "/data.csv", numLinesToSkip = 0)
	public void testSearch(String searchQuery, String hasResultStr) {
		passedWithCertainData = homePage.isSearchPresents();
		searchPage = homePage.search(searchQuery);
		passedWithCertainData = searchPage.isResultMatch(parseBoolean(hasResultStr), searchQuery);
		if (!passedWithCertainData) {
			passedSearchTest = false;
		}
		assertTrue(passedSearchTest);
		searchPage.backToHomePage();
	}
}
