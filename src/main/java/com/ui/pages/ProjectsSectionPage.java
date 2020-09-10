package com.ui.pages;

import com.ui.webdriver.DriverHelper;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProjectsSectionPage extends RepositoryAbstractPage{

    @FindBy(xpath = "//*[contains(text(), 'Create a project')]")
    private WebElement btnAddProject;

    @FindBy(xpath = "//*[@class='link-gray-dark mr-1']")
    private WebElement addedProject;

    /**
     * Class constructor
     *
     * @param driverHelper
     *            The driver that will be used for navigation
     * @throws IllegalStateException
     *             If it's not expected page
     */
    public ProjectsSectionPage(DriverHelper driverHelper) {
        super(driverHelper);
    }

    public boolean areAllElementsPresent() {
        return isElementPresents(btnAddProject);
    }

    public ProjectPage addNewProject() {
        btnAddProject.click();
        return PageNavigator.getPage(ProjectPage.class);
    }

    public boolean isProjectPresent(String name) {
        if(isElementPresents(addedProject)) {
            return addedProject.getAttribute("text").equalsIgnoreCase(name);
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
        return "Projects";
    }
}

