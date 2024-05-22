package SpringPoc.steps;

import SpringPoc.pages.ShoppersStopHomePage;
import SpringPoc.utilities.TestDataUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class HomeSteps {


    @Autowired
    private ShoppersStopHomePage shoppersStopHomePage;


    @Given("I am on Shoppers Stop Application")
    public void iAmOnShoppersStopApplication() {
        shoppersStopHomePage.visitShoppersStopApplicationURL();
        Assert.assertTrue(shoppersStopHomePage.verifyShoppersStopHomePage());
    }

    @When("I search the product: {string}")
    public void iSearchTheProduct(String strProduct) {
        shoppersStopHomePage.enterProductNameInSearchBox(TestDataUtil.getValue(strProduct));
        shoppersStopHomePage.clickSearchButton();
    }

    @Then("I should see the product list")
    public void iShouldSeeTheProductList() {
        Assert.assertTrue(shoppersStopHomePage.verifyWatchProductsList());
    }

    @Given("I am on Product list page")
    public void iAmOnProductListPage() {
        Assert.assertTrue(shoppersStopHomePage.verifyWatchProductsList());
    }


    @When("I see {string} page")
    public void iSeeAddToBagPage(String strValue) {
        Assert.assertTrue(shoppersStopHomePage.verifyAddtoBagPage(TestDataUtil.getValue(strValue)));
//        Assert.assertTrue(shoppersStopHomePage.verifyAddProductSuccessMessage());
    }

    @And("I click on {string} from the list")
    public void iClickOnFromTheList(String strProduct) {
        Assert.assertTrue(shoppersStopHomePage.clickProductFromProductList(TestDataUtil.getValue(strProduct)));
        shoppersStopHomePage.switchToFirstWindow();
        Assert.assertTrue(shoppersStopHomePage.verifyProductInAddToBagPage(TestDataUtil.getValue(strProduct)));
    }

    @Given("I am on {string} page")
    public void iAmOnPage(String strPage) {
        Assert.assertTrue(shoppersStopHomePage.verifyAddtoBagPage(TestDataUtil.getValue(strPage)));
    }

    @Then("I should see Product Details page")
    public void iShouldSeeProductDetailsPage() {
        Assert.assertTrue(shoppersStopHomePage.verifyPriceInfo());
        Assert.assertTrue(shoppersStopHomePage.verifyAddtoBagPage("Add to bag"));
        shoppersStopHomePage.switchToDefaultWindow();
    }

    @Given("I am on Product List page")
    public void iAmOnProductDetailsPage() {
        Assert.assertTrue(shoppersStopHomePage.verifyWatchProductsList());
    }

    @When("I move to the {string} under Kids section")
    public void iMoveToTheUnderKidsSection(String strSection) {
        shoppersStopHomePage.mouseHoverKids();
        shoppersStopHomePage.clickShirtsSection();
    }

    @Then("I should see Kids Shirt list")
    public void iShouldSeeKidsShirtList() {
        Assert.assertTrue(shoppersStopHomePage.verifyProductsList());
    }

    @And("I switch to default window")
    public void iSwitchToDefaultWindow() {
        shoppersStopHomePage.switchToDefaultWindow();
    }

    @Given("I am on the Runloyal welcome page")
    public void iAmOnTheRunloyalWelcomePage() {
        System.out.println("\n RL Welcome page");
    }

    @When("I enter valid {string} and {string}")
    public void iEnterValidAnd(String strUN, String strPWD) {
        System.out.println("\n username ========= "  + strUN);
        System.out.println("\n password ========= "  + strPWD);
    }

    @Then("I see the Runloyal Home Page")
    public void iSeeTheRunloyalHomePage() {
        System.out.println("\n RL Home page");
    }
}
