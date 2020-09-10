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

import static com.ui.common.Config.USER;
import static com.ui.common.Constants.*;
import static com.ui.pages.PageNavigator.getPage;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Feature("Git repository functionality")
@DisplayName("Git repository functionality")
@Tag("Regression")
public class RepositoryTest extends AbstractBaseSeleniumTest {

	private HomePage homePage;
	private CreateRepositoryPage createRepositoryPage;
	private RepositoryPage repositoryPage;
	private OptionsPage optionsPage;
	private IssuesSectionPage issuesSectionPage;
	private ProjectsSectionPage projectsSectionPage;
	private IssuePage issuePage;
	private ProjectPage projectPage;
	private String repName;

	@BeforeEach
	public void beforeTest() {
		homePage = getPage(com.ui.pages.HomePage.class);
	}

	/**
	 * Tests correct repository creating.
	 * <p>
	 * 1. Log in, check that button to create repository presents and click it <br>
	 * 2. Check that form for creating repository is presented and options to
	 * choose owner, gitignore and license work correct <br>
	 * 3. Fill form with correct data and submit <br>
	 * 4. Check that name of the just created repository is the same as from
	 * creating and check that code, issues, pullRequests, wiki, pulse, graphs
	 * and settings sections are presented <br>
	 * 5. Delete this repository
	 */
	@Test
	public void testAddRepository() {

		createRepositoryPage = homePage.createNewRepository();

		createRepositoryPage.isNewRepFormExists();

		repName = randomString();
		repositoryPage = createRepositoryPage.createRepository(repName,
				repDescription, ADD_README, GIT_IGNORE);

		assertEquals(repositoryPage.getFullRepositoryName().replaceAll("\\s+",""),
				USER + "/" + repName);

		repositoryPage.areRepSectionsPresent();

		homePage = repositoryPage.deleteRepository(repName,
				repositoryPage);
	}

	/**
	 * Tests correct repository deleting
	 * <p>
	 * 1. Create new repository, go to settings <br>
	 * 2. Check that option to delete exists <br>
	 * 3. Delete repository, check that message about successful deleting
	 * appeared and check that name of deleted repository is not presented in
	 * the list of existing repositories
	 */
	@Test
	@Description("Verify delete repository")
	@Severity(SeverityLevel.CRITICAL)
	public void testDeleteRepository() {
		createRepositoryPage = homePage.createNewRepository();

		repName = randomString();
		repositoryPage = createRepositoryPage.createRepository(repName,
				repDescription, ADD_README, GIT_IGNORE);

		homePage = repositoryPage.deleteRepository(repName,
				repositoryPage);
		homePage.isRepositoryJustDeleted(repName);
	}

	/**
	 * Tests correct issue adding.
	 * <p>
	 * 1. Log in, add new repository <br>
	 * 2. Click on Issues link, check that all sections and welcome message
	 * present <br>
	 * 3. Click the link to create issue, check that Title, Comments fields and
	 * Labels, Milestone, Assignee links present <br>
	 * 4. Fill all fields and confirm creating, check that issue submitted <br>
	 * 5. Navigate to Issues Section page and check that new issue appeared in
	 * the list of issues <br>
	 * 6. Delete repository
	 */
	@Test
	@Description("Verify add issue")
	@Severity(SeverityLevel.CRITICAL)
	public void testAddIssue(){
		createRepositoryPage = homePage.createNewRepository();

		repName = randomString();
		repositoryPage = createRepositoryPage.createRepository(repName,
				repDescription, ADD_README, GIT_IGNORE);

		issuesSectionPage = repositoryPage.goToIssues();
		issuesSectionPage.areAllElementsPresent();

		issuePage = issuesSectionPage.addNewIssue();
		issuePage.areNewIssueElementsPresent();

		issuePage.addIssue(TITLE, COMMENT);
		issuePage.IsIssueJustAdded();

		issuesSectionPage = issuePage.goToIssues();
		issuesSectionPage.isIssuePresent(TITLE);

		optionsPage = issuesSectionPage.goToSettings();
		homePage = optionsPage.deleteRepository(repName);
	}

    @Test
	@Description("Verify add project")
	@Severity(SeverityLevel.CRITICAL)
    public void testAddProject() {
		createRepositoryPage = homePage.createNewRepository();

		repName = randomString();
		repositoryPage = createRepositoryPage.createRepository(repName,
				repDescription, ADD_README, GIT_IGNORE);

        projectsSectionPage = repositoryPage.goToProjects();
        projectsSectionPage.areAllElementsPresent();

        projectPage = projectsSectionPage.addNewProject();
        projectPage.areNewProjectElementsPresent();

        projectPage.addProject(PROJECT_NAME, PROJECT_BODY);
        projectPage.IsProjectJustAdded();

        projectsSectionPage = projectPage.goToProjects();
        projectsSectionPage.isProjectPresent(PROJECT_NAME);

        optionsPage = projectsSectionPage.goToSettings();
        homePage = optionsPage.deleteRepository(repName);
    }

}
