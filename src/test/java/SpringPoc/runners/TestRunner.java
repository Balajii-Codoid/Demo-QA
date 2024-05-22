package SpringPoc.runners;

import SpringPoc.utilities.Constants;
import SpringPoc.utilities.TestDataUtil;
import SpringPoc.utilities.TestrailUtil;
import SpringPoc.utilities.YamlUtil;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import static SpringPoc.utilities.YamlUtil.getYamlData;


@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        plugin = {"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "html:reports/cucumber/CucumberReport.html",
                "json:reports/cucumber/cucumber.json",
//                "com.codoid.products.listener.KageMetricsEventListener",
                "SpringPoc.utilities.ExecutionTracker"},
        glue = "SpringPoc")
public class TestRunner {

    @BeforeClass
    public static void beforeClass() {
        YamlUtil.initializeObject();
        TestDataUtil.loadData(Constants.TESTDATA_PATH, System.getProperty(Constants.ENV_VARIABLE_APPLICATION));

        if (getYamlData("testrail.enable", System.getProperty(Constants.TESTRAIL)).equalsIgnoreCase("TRUE")) {
            TestrailUtil.createTestRailInstance(getYamlData("testrail.engine_url"), getYamlData("testrail.username"), getYamlData("testrail.password"));
            if (getYamlData("testrail.new_run.enable", System.getProperty(Constants.TESTRAIL_NEW_RUN)).equalsIgnoreCase("TRUE")) {
                TestrailUtil.createTestRailRun(getYamlData("testrail.new_run.project_id"), getYamlData("testrail.new_run.suite_id"), getYamlData("testrail.new_run.run_name"));
            }
        }
    }
//    clean test -Dexecution_type=mobile -Dconfig=local_android_SamsungGalaxyA52 -Denv=QA -Dapp=SpringPoc -Drunner=TestRunner -Dcucumber.filter.tags=@BlinkTradePro
}
