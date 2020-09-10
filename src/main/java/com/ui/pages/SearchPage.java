package com.ui.pages;

import com.ui.webdriver.DriverHelper;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

/**
 * Class represents page with search results
 */
public class SearchPage extends Page {

	@FindBy(xpath = "//*[@class='repo-list']")
	private List<WebElement> searchResults;

	@FindBy(xpath = "//div[@class='blankslate ']")
	private WebElement errorNoResult;

	/**
	 * Class constructor
	 * 
	 * @param driverHelper
	 *            The driver that will be used for navigation
	 * @throws IllegalStateException
	 *             If it's not expected page
	 */
	public SearchPage(DriverHelper driverHelper) {
		super(driverHelper);
		PageFactory.initElements(driverHelper.getDriver(), this);
	}

	/**
	 * Search on the site
	 *
	 * @param query
	 *            Search query
	 * @return An instance of {@link SearchPage} class with search result
	 */
	public SearchPage search(String query) {
		search.clear();
		search.sendKeys(query);
		search.submit();
		return PageNavigator.getPage(SearchPage.class);
	}

	/**
	 * Method to get the list of links with search results
	 * 
	 * @return List of links with search results
	 */

	public List<WebElement> getSearchResults() {
		return searchResults;
	}

	/**
	 * Check if error message presents
	 *
	 * @return True if error message presents
	 */
	public boolean isErrorNoResultPresents() {
		return isElementPresents(errorNoResult);
	}

	/**
	 * Method to get expected page title
	 *
	 * @return expected page title
	 */
	@Override
	public String getExpectedTitle() {
		return "Search";
	}

	/**
	 * Method to check if search results contain search query
	 * 
	 * @param searchQuery
	 *            Search Query
	 *
	 * @return True if search results contain search query
	 */
	public boolean isResultContains(String searchQuery) {
		List<WebElement> searchResults = getSearchResults();
		boolean contains = false;
		for (WebElement result : searchResults) {
			if (result.getAttribute("innerText").toLowerCase().contains(searchQuery)) {
				contains = true;
			}
		}
		return contains;
	}

	/**
	 * Method to check if search results appear correct: if some results found,
	 * they contain search query or if nothing found, error message appears
	 * 
	 * @param hasResult
	 *            If true, search results must appear, if false, then error
	 *            message
	 * @param searchQuery
	 *            Search Query
	 *
	 * @return True if search results appear correct: if some results found,
	 *         they contain search query or if nothing found, error message
	 *         appears
	 */
	public boolean isResultMatch(boolean hasResult, String searchQuery) {
		if (hasResult) {
			return isResultContains(searchQuery);
		} else {
			return isErrorNoResultPresents();
		}
	}

}
