package SpringPoc.pages;

import SpringPoc.cap.common.BasePage;
import SpringPoc.utilities.TestDataUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.springframework.stereotype.Component;

import static SpringPoc.utilities.DriverUtil.driver;

@Component
public class SearchPage extends BasePage {

    @FindBy(how = How.CSS, using = "input#searchbox_input")
    private WebElement searchInput;

    @FindBy(how = How.CSS, using = "button[type='submit']")
    private WebElement searchButton;

    By elmntSearchBox = By.id("twotabsearchtextbox");

    @FindBy(how = How.ID, using = "nav-search-submit-button")
    public WebElement iconSearch;

    public void inputSearch(String search) {
        driver.get(TestDataUtil.getValue("&URL&"));
        waitForElement(searchInput);
        enterValue(searchInput, search);
    }

    public void pressSearchButton() {
        waitForElement(searchButton);
        searchButton.click();
    }

    public void visit() {
        driver.navigate().to("https://www.amazon.in/");
    }

    public void SearchProduct() {
        waitForElement(elmntSearchBox);
        enterValue((WebElement) elmntSearchBox, "Mobile");
        click(iconSearch);
    }
}