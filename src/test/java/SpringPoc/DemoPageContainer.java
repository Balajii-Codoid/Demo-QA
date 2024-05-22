package SpringPoc;

import static SpringPoc.utilities.Constants.*;
import static SpringPoc.utilities.FileUtil.clearLogFile;
import static SpringPoc.utilities.FileUtil.writeLog;
import static SpringPoc.utilities.TestrailUtil.*;
import static SpringPoc.utilities.YamlUtil.getYamlData;
import static org.openqa.selenium.support.PageFactory.initElements;

import SpringPoc.utilities.DriverUtil;
import SpringPoc.utilities.*;
import SpringPoc.cap.common.BasePage;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.json.JSONException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Consumer;

public class DemoPageContainer {

    static int i;

    @Autowired
    private DriverUtil driverUtil;

    @Autowired
    private List<BasePage> webPages;
    public static Scenario myScenario;
    public static String strExecutionID;

    public static String scenarioStatus = null;
    public static String scenarioTags;
    public static String strTestcaseID;
    public static LinkedHashMap<String, String> printTestDataMap = new LinkedHashMap<String, String>();
    public static String strAccountID = getYamlData("testlodge.Existing_run.AccountID");
    public static String strProjectID = getYamlData("testlodge.Existing_run.ProjectID");
    public static String strTestRunID = getYamlData("testlodge.Existing_run.Run_ID");
    public static String strUsername = getYamlData("testlodge.username");
    public static String strPassword = getYamlData("testlodge.password");
    String strExecutionType = System.getProperty(Constants.ENV_VARIABLE_EXECUTION_TYPE, "");

    // Define the Consumer for launching WebDriver
    private final Consumer<String> browserHandler = executionType -> {
        DriverUtil.driver = driverUtil.getDriver();
        writeLog("<----------------------WEB DRIVER LAUNCHED---------------------->");
    };

    private final Consumer<String> mobileHandler = executionType -> {
        DriverUtil.mobileDriver = driverUtil.getMobileDriver();
        writeLog("<----------------------MOBILE DRIVER LAUNCHED---------------------->");
    };

    private final Consumer<String> APIHandler = executionType -> {
        DriverUtil.driver = null;
        writeLog("<----------------------API EXECUTION---------------------->");
    };
    private final Consumer<String> browserstackHandler = executionType -> {

        DriverUtil.driver = driverUtil.getBrowserStackDriver();
        writeLog("<----------------------BrowserStack WEB DRIVER LAUNCHED---------------------->");
    };


    private final Consumer<String> combinedHandler = executionType -> {
        // Logic from mobileHandler
        DriverUtil.mobileDriver = driverUtil.getMobileDriver();
        writeLog("<----------------------MOBILE DRIVER LAUNCHED---------------------->");

        // Logic from browserHandler
        DriverUtil.driver = driverUtil.getDriver();
        writeLog("<----------------------WEB DRIVER LAUNCHED---------------------->");
    };

    // Create a map to associate execution types with their respective handlers
    private final Map<String, Consumer<String>> executionTypeHandlers = new HashMap<>();

    @Before
    public void initializeDriver(Scenario beforeScenario) {

        try {

            // Check if the driver is null before initializing
            if (DriverUtil.driver == null && DriverUtil.mobileDriver == null) {
                clearLogFile(strLogFileLoc);
                executionTypeHandlers.put("BROWSER", browserHandler);
                executionTypeHandlers.put("MOBILE", mobileHandler);
                executionTypeHandlers.put("WEBMOBILE", combinedHandler);
                executionTypeHandlers.put("API", APIHandler);
                executionTypeHandlers.put("BSWEB", browserstackHandler);

                if (!executionTypeHandlers.containsKey(strExecutionType.toUpperCase())) {
                    throw new IllegalArgumentException("Invalid execution type: " + strExecutionType);
                }
                // Get the appropriate handler based on the execution type and execute it
                Consumer<String> handler = executionTypeHandlers.get(strExecutionType.toUpperCase());
                handler.accept(strExecutionType);

                webPages.forEach(p -> initElements(DriverUtil.driver, p));
//                mobileScreens.forEach(p -> initElements(new AppiumFieldDecorator(DriverUtil.mobileDriver), p));
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        strExecutionID = TestDataUtil.getRandomString();
        printTestDataMap.clear();
        System.out.println("\n SCENARIO: " + (++i) + " STARTED");
        System.out.println("\n STARTED AT: " + DateUtil.getCurrentDate("dd/MM/yy hh:mm:ss aaa"));
        System.out.println("\n SCENARIO NAME: " + beforeScenario.getName());
        myScenario = beforeScenario;
    }


    @After
    public void afterScenario(Scenario afterScenario) throws JSONException {
        if (printTestDataMap.size() > 0) {
            for (Map.Entry<String, String> entry : printTestDataMap.entrySet()) {
                afterScenario.log(entry.getKey().concat(": ").concat(entry.getValue()));
            }
        }

        System.out.println("\n SCENARIO: " + i + " COMPLETED");
        System.out.println("\n COMPLETED AT: " + DateUtil.getCurrentDate("dd/MM/yy hh:mm:ss aaa"));
        System.out.println("\n SCENARIO " + i + " STATUS: " + afterScenario.getStatus());

        try {
            if (strExecutionType.equalsIgnoreCase("BROWSER")) {
                afterScenario.attach(((TakesScreenshot) DriverUtil.driver).getScreenshotAs(OutputType.BYTES),
                        "image/png", "");
            } else if (strExecutionType.equalsIgnoreCase("MOBILE")) {
                afterScenario.attach(((TakesScreenshot) DriverUtil.mobileDriver).getScreenshotAs(OutputType.BYTES),
                        "image/png", "");
            } else if (strExecutionType.equalsIgnoreCase("WEBMOBILE")) {
                afterScenario.attach(((TakesScreenshot) DriverUtil.driver).getScreenshotAs(OutputType.BYTES),
                        "image/png", "");
                afterScenario.attach(((TakesScreenshot) DriverUtil.mobileDriver).getScreenshotAs(OutputType.BYTES),
                        "image/png", "");
            } else if (strExecutionType.equalsIgnoreCase("BSWEB")) {
                afterScenario.attach(((TakesScreenshot) DriverUtil.driver).getScreenshotAs(OutputType.BYTES),
                        "image/png", "");
            }
            if (getYamlData("testrail.enable", System.getProperty(Constants.TESTRAIL)).equalsIgnoreCase("TRUE")) {
                String strRunID = "";
                scenarioStatus = String.valueOf(afterScenario.getStatus());
                scenarioTags = String.valueOf(afterScenario.getSourceTagNames());
                strTestcaseID = PatternHandlerUtil.getMatchContent("@C(\\d+)", scenarioTags);
                if (getYamlData("testrail.new_run.enable", System.getProperty(Constants.TESTRAIL_NEW_RUN)).equalsIgnoreCase("TRUE")) {
                    strRunID = getYamlData("testrail.new_run.run_id");
                } else {
                    strRunID = getYamlData("testrail.existing_run.run_id");
                }
                String returnRunID = "null".equals(strRunID.trim()) ? null : strRunID;
                addTestResult(returnRunID, strTestcaseID);
            }
        } catch (Exception e) {

        }
    }
}
