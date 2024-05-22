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
public class ShoppersStopHomePage extends BasePage {


    @FindBy(how = How.CSS, using = "img.desk-logo[title='SHOPPERS STOP']")
    protected WebElement elmntShoppersStopHeaderLogo;

    @FindBy(how = How.XPATH, using = "(//div[@class='new-banners']/div[@class='carousel-div'])[1]")
    protected WebElement elmntCarouselInfo;

    @FindBy(how = How.XPATH, using = "//input[contains(@placeholder,'Search Products')]")
    protected WebElement txtSearchProducts;

    @FindBy(how = How.CSS, using = "input.btnsearch")
    protected WebElement btnSearch;

    @FindBy(how = How.XPATH, using = "//h2[contains(.,'smart watch')]/following::ul[contains(@class,'product-listing product-grid ')]")
    protected WebElement lstWatchProducts;


    protected String strProductLocator = new StringBuilder()
            .append("(//div[contains(@title,'")
            .append("<<REPLACECONTENT>>")
            .append("')])[1]").toString();

    protected String strProductLocatorInAddtoBagPage = new StringBuilder()
            .append("(//div[@class='brand-name']/div[contains(normalize-space(.),'")
            .append("<<REPLACECONTENT>>")
            .append("')])[1]").toString();

    protected String strAddtoBagLocator = new StringBuilder()
            .append("(//div[@class='custom-add-to-cart-buttons']/following::input[@value='")
            .append("<<REPLACECONTENT>>")
            .append("'])[4]").toString();

    @FindBy(how = How.XPATH, using = "//div[.='Product is added to the bag successfully'][2]")
    protected WebElement elmntAddproductSuccessMessage;

    @FindBy(xpath = "//a[contains(@href,'cart')]/i")
    protected WebElement elmntAddToCart;

    protected String strProductInAddToCartPage = new StringBuilder()
            .append("//h3[.='My cart']/following::a[contains(normalize-space(.),'")
            .append("<<REPLACECONTENT>>")
            .append("')][1]").toString();

    @FindBy(how = How.XPATH, using = " (//div[@class='price_div'])[2]/ul/li[1]")
    protected WebElement elmntPriceInfo;

    @FindBy(how = How.CSS, using = "a[title='KIDS']")
    protected WebElement elmntKids;

    @FindBy(how = How.XPATH, using = "//a[@title='Shirts'][contains(@href,'kids')]")
    protected WebElement elmntShirtsUnderKidsSection;

    @FindBy(how = How.XPATH, using = "//ul[contains(@class,'product-listing')]")
    protected WebElement elmntProductsList;



    public void visitShoppersStopApplicationURL() {
        visit(TestDataUtil.getValue("&URL&"));

    }

    public boolean verifyShoppersStopHomePage() {
        waitForElement(elmntShoppersStopHeaderLogo);
        takeScreenshot(driver);
        return verifyElement(elmntShoppersStopHeaderLogo);

    }

    public void enterProductNameInSearchBox(String strProduct) {
        waitForElement(txtSearchProducts);
        enterValue(txtSearchProducts, strProduct);
    }

    public void clickSearchButton() {
        waitForElement(btnSearch);
        click(btnSearch);
    }

    public boolean verifyWatchProductsList() {
        waitForElement(lstWatchProducts);
        return verifyElement(lstWatchProducts);
    }

    public boolean clickProductFromProductList(String strProduct) {
        WebElement elmntProductFromProductList = waitForElement(By.xpath(strProductLocator.replace("<<REPLACECONTENT>>", strProduct.toUpperCase())));
        return click(elmntProductFromProductList);
    }

    public boolean verifyProductInAddToBagPage(String strProduct) {
        WebElement elmntProductFromProductList = waitForElement(By.xpath(strProductLocatorInAddtoBagPage.replace("<<REPLACECONTENT>>", strProduct)));
        attachStepLog("Product Name", strProduct);
        return verifyElement(elmntProductFromProductList);
    }

    public void switchToFirstWindow() {
        try {
            waitForSeconds(4);
            waitForWindow(2);
            focusWindow(2);
            System.out.println("\n window focused");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean verifyAddtoBagPage(String strValue) {
        WebElement elmntAddtoBag = waitForElement(By.xpath(strAddtoBagLocator.replace("<<REPLACECONTENT>>", strValue)));
        return verifyElement(elmntAddtoBag);
    }

    public boolean verifyPriceInfo() {
        waitForElement(elmntPriceInfo);
        System.out.println("\n Product price -----> " + elmntPriceInfo.getText());
        attachStepLog("Product price", elmntPriceInfo.getText());
        takeScreenshot(driver);
        return verifyElement(elmntPriceInfo);

    }

    public void mouseHoverKids() {
        jsScrollUp();
        waitForElement(elmntKids);
        mouseHover(elmntKids);
    }

    public void clickShirtsSection() {
        waitForElement(elmntShirtsUnderKidsSection);
        click(elmntShirtsUnderKidsSection);
    }

    public boolean verifyProductsList() {
        waitForElement(elmntProductsList);
        return verifyElement(elmntProductsList);
    }

    public void switchToDefaultWindow() {
        try {
            waitForSeconds(2);
            waitForWindow(2);
            closeWindow(2);
            focusWindow(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
