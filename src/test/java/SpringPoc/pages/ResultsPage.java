package SpringPoc.pages;

import SpringPoc.cap.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.springframework.stereotype.Component;

@Component
public class ResultsPage extends BasePage {

    private static final String RESULTS_LOCATOR = "[data-testid='mainline'] [data-testid='result']";
    By elmntSearchBox = By.id("twotabsearchtextbox");

    @FindBy(how = How.XPATH, using = "//span[text()='Results']")
    protected WebElement elmntResult;


    public void assertResultsArePresent() {
        waitForElement(By.cssSelector(RESULTS_LOCATOR));
    }

    public boolean verifyResultPage() {
        waitForElement(elmntSearchBox);
        return verifyElement(elmntSearchBox);
    }
}