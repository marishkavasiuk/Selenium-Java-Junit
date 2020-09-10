package com.ui.pages;

import com.ui.webdriver.DriverHelper;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class ProjectPage extends RepositoryAbstractPage {

    @FindBy(id = "project_name")
    private WebElement project_name;

    @FindBy(id = "project_body")
    private WebElement project_body;

    @FindBy(xpath = "//*[@class='btn select-menu-button text-center flex-auto']")
    private WebElement choose_template;

    @FindBy(xpath = "(//*[@class='select-menu-list'])[2]")
    private List<WebElement> list_templates;

    @FindBy(xpath = "//*[contains(text(), 'Create project')]")
    private WebElement create_button;

    @FindBy(xpath = "//h3[contains(@class,'mb-1') and contains(., 'have any columns or cards.')]")
    private WebElement success_message;


    /**
     * Class constructor
     *
     * @param driverHelper
     *            The driver that will be used for navigation
     * @throws IllegalStateException
     *             If it's not expected page
     */
    public ProjectPage(DriverHelper driverHelper) {
        super(driverHelper);
    }

    public boolean areNewProjectElementsPresent() {
        return isElementPresents(project_name) && isElementPresents(project_body)
                && isElementPresents(choose_template) && isElementPresents(create_button);
    }

    public void addProject(String name, String body) {
        project_name.sendKeys(name);
        project_body.sendKeys(body);
        create_button.click();

    }

    public boolean IsProjectJustAdded() {
        System.out.println(success_message.getAttribute("outerText"));
        return isElementPresents(success_message);
    }

    /**
     * Method to get expected page title
     *
     * @return expected page title
     */
    @Override
    public String getExpectedTitle() {
        return "New Project";
    }
}
