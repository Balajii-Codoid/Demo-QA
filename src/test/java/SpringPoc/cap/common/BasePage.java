package SpringPoc.cap.common;

import SpringPoc.DemoPageContainer;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static SpringPoc.utilities.DriverUtil.*;
import static SpringPoc.utilities.FileUtil.writeLogError;

public class BasePage {
    protected void visit(String URL) {
        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        System.out.println("URL:" + URL);
        driver.get(URL);
        System.out.println("Launched URL");
    }

    protected WebElement waitForElementClickable(By by, int secs) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(secs)).until(ExpectedConditions.elementToBeClickable(by));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    protected boolean verifyURLContains(String strURLValue) {
        try {
            return webDriverWait.until(ExpectedConditions.urlContains(strURLValue));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected WebElement waitForElement(By by) {
        try {
            return webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    protected boolean waitForElement(By by, int secs) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(secs)).until(ExpectedConditions.visibilityOfElementLocated(by)) != null;
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    protected WebElement waitForElement(WebElement element) {
        try {
            return webDriverWait.until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    protected WebElement waitForElementClickable(By by) {
        try {
            return webDriverWait.until(ExpectedConditions.elementToBeClickable(by));
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    protected WebElement waitForElementClickable(WebElement element) {
        try {
            return webDriverWait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    protected boolean verifyText(String strExpectedText) {
        try {
            return driver.getPageSource().contains(strExpectedText);
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void waitForSeconds(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (Exception e) {
        }
    }

    public void acceptAlert() {
        driver.switchTo().alert().accept();
    }

    public boolean focusFrame() {
        boolean isFrameFocused = false;
        waitForSeconds(1);
        driver.switchTo().frame(0);
        isFrameFocused = true;
        return isFrameFocused;
    }

    public boolean focusFrame(WebElement element) {
        boolean isFrameFocused;
        waitForSeconds(1);
        driver.switchTo().frame(element);
        isFrameFocused = true;
        return isFrameFocused;
    }

    public boolean focusBackToDefaultFrame() {
        boolean isFrameFocused = false;
        waitForSeconds(1);
        driver.switchTo().defaultContent();
        isFrameFocused = true;
        return isFrameFocused;

    }

    public boolean compareList(List<WebElement> lstElements, List<String> lstDataValues) {
        List<String> lstActualValue = new ArrayList<String>();
        List<String> lstExpectedValues = new ArrayList<String>();
        lstElements.stream().forEach(eleItem -> lstActualValue.add(eleItem.getText().trim()));
        lstDataValues.stream().forEach(strData -> lstExpectedValues.add(strData));
        return (lstActualValue.equals(lstExpectedValues));
    }

    public boolean compareByAttributeValue(WebElement element, String strExpectedValue) {
        return element.getAttribute("value").equalsIgnoreCase(strExpectedValue);
    }

    public boolean compareByText(WebElement element, String strExpectedValue) {
        return element.getText().equalsIgnoreCase(strExpectedValue);
    }

    public boolean compareByContainsAttributeValue(WebElement element, String strExpectedValue) {
        return element.getAttribute("value").contains(strExpectedValue);
    }

    public boolean compareByContainsText(WebElement element, String strExpectedValue) {
        return element.getText().contains(strExpectedValue);
    }

    public WebDriver waitForframeToBeAvailableAndSwitchToIt(WebDriver driver, WebElement element) {
        WebDriver frameDriver = null;
        frameDriver = new WebDriverWait(driver, Duration.ofSeconds(60)).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(element));
        return frameDriver;
    }

    public void mouseHover(WebElement element) {
        try {
            Actions actions = new Actions(driver);
            actions.moveToElement(element).build().perform();
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void mouseOver(By by) {
        try {
            Actions actions = new Actions(driver);
            actions.moveToElement(driver.findElement(by)).build().perform();
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void mouseClick(WebElement element) {
        try {
            Actions actions = new Actions(driver);
            waitForElement(element);
            actions.moveToElement(element).click(element).build().perform();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void waitAndMouseClick(By by) {
        try {
            WebElement elmnt = waitForElement(by);
            Actions actions = new Actions(driver);
            actions.moveToElement(elmnt).click(elmnt).build().perform();
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String alertGetText() {
        webDriverWait.until(ExpectedConditions.alertIsPresent());
        return driver.switchTo().alert().getText();

    }

    public void refreshBrowser() {
        driver.navigate().refresh();
    }

    public void navigatesTo(String strURL) {
        try {
            driver.navigate().to(strURL);
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void waitAndAcceptAlert() {
        waitForSeconds(2);
        webDriverWait.until(ExpectedConditions.alertIsPresent()).accept();
    }

    public void jsClick(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("var elem=arguments[0]; setTimeout(function() {elem.click();}, 100)", element);
        waitForSeconds(1);
    }

    public void jsClick(By by) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("var elem=arguments[0]; setTimeout(function() {elem.click();}, 100)",
                driver.findElement(by));
        waitForSeconds(1);
    }

    public void jsScroll() {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("window.scrollBy(0,300)");
        waitForSeconds(1);
    }

    public void jsScrollBy(int scrollValue) {
        String strScrollBy = new StringBuilder().append("\"window.scrollBy(0,").append(scrollValue).append(")\"").toString();
        System.out.println("Scroll by " + strScrollBy);
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("window.scrollBy(0,500)");
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,500)");
        waitForSeconds(1);
    }

    public void jsMouseHover(WebElement element) {
        //String strJavaScript = "var element = arguments[0];"
        //+ "var mouseEventObj = document.createEvent('MouseEvents');"
        //+ "mouseEventObj.initEvent( 'mouseover', true, true );" + "element.dispatchEvent(mouseEventObj);";

        ((JavascriptExecutor) driver).executeScript("arguments[0].onmouseover()", element);
    }

    public void jsEnterValue(WebElement elmnt, String strValue) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='';", elmnt);
        elmnt.sendKeys(strValue);
    }

    public void selectElementByVisibleText(WebElement element, String strValue) {
        Select select = new Select(element);
        select.selectByVisibleText(strValue);
    }

    public void getOptionsFromDropDown(WebElement element, String strValue) {
        Select select = new Select(element);
        select.getOptions().forEach(i -> System.out.println("Options: " + i.getText()));
    }

    public void selectElementByVisibleTextUsingBy(By by, String strVisibleTxt) {
        Select select = new Select(driver.findElement(by));
        select.selectByVisibleText(strVisibleTxt);
    }

    public void jsSelectDropdownOption(WebElement element, String strVisibleText) {
        System.out.println("\n >>> Enter JS Select with Parameter: " + strVisibleText);
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("var select = arguments[0]; for(var i = 0; i < select.options.length; i++){ if(select.options[i].text == arguments[1]){ select.options[i].selected = true; } }", element, strVisibleText);

        waitForSeconds(1);
    }

    public void selectElementByIndex(WebElement element, int i) {
        Select select = new Select(element);
        select.selectByIndex(i);
    }

    public void enterValue(WebElement element, String strValue) {
        try {
            element.click();
            element.clear();
            waitForSeconds(1);
            element.sendKeys(strValue);
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void waitForWindow(int inWindowIndex) {
        webDriverWait.until(ExpectedConditions.numberOfWindowsToBe(inWindowIndex));
    }

    public void closeWindow(int inWindowIndex) {
        Object[] handles = driver.getWindowHandles().toArray();
        driver.switchTo().window((String) handles[inWindowIndex - 1]).close();

    }

    public void refreshPage() {
        driver.navigate().refresh();
    }

    public void focusWindow(int inWindowIndex) {
        Object[] handles = driver.getWindowHandles().toArray();

        driver.switchTo().window((String) handles[inWindowIndex - 1]);
    }

    public void waitForText(By by, String strExpectedText) {
        webDriverWait.until(ExpectedConditions.textToBePresentInElementLocated(by, strExpectedText));
    }


    public WebElement waitForPresenceOfElement(By by) {
        return webDriverWait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public boolean verifyElement(WebElement element) {
        boolean isVerify = false;
        try {
            isVerify = element.isDisplayed();
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
            isVerify = false;
        }
        return isVerify;
    }

    public List<WebElement> waitForElements(List<WebElement> element) {
        return webDriverWait.until(ExpectedConditions.visibilityOfAllElements(element));
    }


    public List<WebElement> numberOfElementsToBeMoreThan(By by, int count) {
        List<WebElement> ele = webDriverWait.until(ExpectedConditions.numberOfElementsToBeMoreThan(by, count));
        return ele;
    }

    public boolean waitAndSelectByVisibleText(WebElement element, String strValue, By by, int count) {
        Boolean isSelected = false;
        numberOfElementsToBeMoreThan(by, count);
        selectElementByVisibleText(element, strValue);
        isSelected = true;
        return isSelected;
    }

    public List<String> getOptionsInDropdown(WebElement element) {
        List<String> optionText = new ArrayList<>();
        Select select = new Select(element);
        select.getOptions().forEach(e -> {
            optionText.add(e.getText());
        });
        return optionText;
    }

    public boolean verifyElement(By element) {
        boolean isVerify = false;
        try {
            isVerify = driver.findElement(element).isDisplayed();
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
            isVerify = false;
        }
        return isVerify;
    }

    public static boolean verifyStringMatchedWithListOfElement(List<WebElement> elements, String strValue) {
        boolean blResult = false;
        try {
            blResult = elements.stream().allMatch(element -> element.getText().trim().contains(strValue));
            System.out.println(blResult);
        } catch (Exception e) {
            blResult = false;
            e.printStackTrace();
        }
        return blResult;
    }

    public boolean click(WebElement element) {
        boolean blResult = false;
        try {
            element.click();
            blResult = true;
        } catch (Exception e) {
            e.printStackTrace();
            writeLogError(e.getMessage());
            throw e;
        }
        return blResult;
    }

    public boolean waitAndClick(WebElement element) {
        boolean blResult = false;
        try {
            waitForElement(element);
            click(element);
            blResult = true;
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return blResult;
    }

    public boolean doubleClick(WebDriver driver, WebElement element) {
        boolean blResult = false;
        try {
            Actions actions = new Actions(driver);
            actions.doubleClick(element).perform();
            blResult = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return blResult;
    }

    public void keysInput(WebElement element, Keys strKey) {
        element.click();
        element.clear();
        waitForSeconds(1);
        element.sendKeys(strKey);
    }

    public boolean click(WebDriver driver, By by) {
        boolean blResult = false;
        try {
            driver.findElement(by).click();
            blResult = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return blResult;
    }

    public void robotKey(WebElement element, int Keycode) {
        Robot robot = null;
        try {
            robot = new Robot();
            verifyElement(element);
            click(element);
            robot.keyPress(Keycode);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void robotKeyRelease(int Keycode) {
        Robot robot = null;
        try {
            robot = new Robot();
            robot.keyRelease(Keycode);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void robotKey(int Keycode) {
        Robot robot = null;
        try {
            robot = new Robot();
            robot.keyPress(Keycode);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public boolean keys(WebElement element, String strKey) {
        boolean blResult = false;
        try {
            element.sendKeys(strKey);
            blResult = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blResult;
    }

    public void jsScrollDown() {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("window.scrollBy(0,200)");
        waitForSeconds(1);
    }

    public String getBrowserName() {
        Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
        String browserName = cap.getBrowserName().toLowerCase();
        System.out.println(browserName);
        return browserName;
    }


    public void dragElement(WebElement source, WebElement destination) {
        Actions act = new Actions(driver);
        act.clickAndHold(source);
        act.moveToElement(destination);
        act.release(source);
        act.build().perform();
    }

    public void dragAndDrop(WebElement source, WebElement destination) {
        Actions act = new Actions(driver);
        act.dragAndDrop(source, destination).build().perform();
    }

    public boolean click(int X, int Y) {
        boolean blResult = false;
        try {
            Actions act = new Actions(driver);
            act.moveByOffset(X, Y).build().perform();
            act.click();
            act.build().perform();
//            act.moveByOffset(X, Y).click().build().perform();
            blResult = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blResult;
    }


    public void takeScreenshot(WebDriver driver) {
        try {
            DemoPageContainer.myScenario.attach(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES), "image/png", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void attachStepLog(String strKey, String strvalue) {
        try {
            DemoPageContainer.printTestDataMap.put(strKey, strvalue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jsScrollUp() {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("window.scrollBy(0,-245)");
        waitForSeconds(1);
    }

    public List<String> getOptions(WebElement element) {
        List<String> lstDropdownValues = new ArrayList<String>();
        Select allOptions = new Select(element);
        allOptions.getOptions().forEach(dropdownValue -> lstDropdownValues.add(dropdownValue.getText()));
        return lstDropdownValues;
    }

    public void uploadFile(WebElement element, String strFilepath) {
        waitForSeconds(1);
        element.sendKeys(strFilepath);
        waitForSeconds(5);
    }

    public void openNewTab(String strURL) {
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get(strURL);
    }
}
