package com.ui.sessions.steps;


import com.codeborne.selenide.Condition;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.*;


public class MyStepdefs {
    @Given("^Google search is opened in my browser$")
    public void googleSearchIsOpenInMyBrowser() throws Throwable {
        open("http://www.google.com");
    }

    @When("^I enter search term \"([^\"]*)\"$")
    public void iEnterSearchTerm(String arg0) throws Throwable {
        $("[name=q]").sendKeys(arg0 + Keys.ENTER);
    }

    @Then("^Search results contains links for \"([^\"]*)\" related resources$")
    public void searchResultsContainsLinksForRelatedResources(String arg0) throws Throwable {
        $("h3").shouldHave(Condition.text(arg0));
    }

    @When("^I click on first search results$")
    public void iClickOnFirstSearchResults() throws Throwable {
       $("h3").click();
    }

    @Then("^Opened page should contain info about \"([^\"]*)\"$")
    public void openedPageShouldContainInfoAbout(String arg0) throws Throwable {
        $("div.news div.news-line").shouldHave(Condition.text(arg0));
    }


    @And("Search results page contains {int} results")
    public void searchResultsPageContainsResults(int arg0) {
        $$("h3").shouldHaveSize(arg0);
    }

    @Then("Word {string} is present on the page")
    public void wordIsPresentOnThePage(String arg0) {
        $("h1").shouldHave(Condition.text(arg0));
    }
}
