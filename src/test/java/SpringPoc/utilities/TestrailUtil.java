package SpringPoc.utilities;

import com.codepine.api.testrail.TestRail;
import com.codepine.api.testrail.model.*;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Objects;
import static SpringPoc.DemoPageContainer.scenarioStatus;
import static SpringPoc.DemoPageContainer.strTestcaseID;
import static SpringPoc.utilities.Constants.*;
import static SpringPoc.utilities.FileUtil.writeLog;
import static SpringPoc.utilities.FileUtil.writeLogError;

@Component
public class TestrailUtil {

    public static TestRail testRail;
    public static Project project;
    public static Suite suite;

    public static Section section;

    public static Case testCase;
    public static Run run;

    public static void createTestRailInstance(String url, String username, String password) {
        try {
            testRail = TestRail.builder(url, username, password).applicationName("testrail").build();
            writeLog("Testrail Instance Created");
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createTestRailProject(String pName) {
        try {
            project = testRail.projects().add(new Project().setName(pName)).execute();
            writeLog("TestRail Project Added Successfully");
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
        }
    }


    public static void createTestrailSuiteTest(String pId, String sName) {
        try {
            if (pId == null) {
                suite = testRail.suites().add(project.getId(), new Suite().setName(sName)).execute();
                writeLog("TestRail Suite added successfully");
            } else {
                suite = testRail.suites().add(Integer.parseInt(pId), new Suite().setName(sName)).execute();
                writeLog("TestRail Suite added successfully");
            }
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createTestRailSection(String pId, String sId, String secName) {
        try {
            if (pId == null & sId == null) {
                section = testRail.sections().add(project.getId(), new Section().setSuiteId(suite.getId()).setName(secName)).execute();
                writeLog("TestRail Section Added Successfully");
            } else if (sId == null) {
                section = testRail.sections().add(Integer.parseInt(pId), new Section().setSuiteId(suite.getId()).setName(secName)).execute();
                writeLog("TestRail Section Added Successfully");
            } else {
                assert pId != null;
                section = testRail.sections().add(Integer.parseInt(pId), new Section().setSuiteId(Integer.valueOf(sId)).setName(secName)).execute();
                writeLog("TestRail Section Added Successfully");
            }
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
        }
    }


    public static void createTestRailTestcases(String secId, String testCaseTitle) {
        try {
            if (secId == null) {
                List<CaseField> customCaseFields = testRail.caseFields().list().execute();
                testCase = testRail.cases().add(section.getId(), new Case().setTitle(testCaseTitle), customCaseFields).execute();
                writeLog("Testrail Testcase Added Successfully");
            } else {
                List<CaseField> customCaseFields = testRail.caseFields().list().execute();
                testCase = testRail.cases().add(Integer.parseInt(secId), new Case().setTitle(testCaseTitle), customCaseFields).execute();
                writeLog("Testrail Testcase Added Successfully");
            }
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
        }
    }


    public static void createTestRailRun(String pId, String sId, String testRunName) {
        try {
            if (pId == null & sId == null) {
                run = testRail.runs().add(project.getId(), new Run().setSuiteId(suite.getId()).setName(testRunName)).execute();
               writeLog("Testrail test Run Added Successfully");
            } else if (sId == null) {
                run = testRail.runs().add(Integer.parseInt(pId), new Run().setSuiteId(suite.getId()).setName(testRunName)).execute();
                writeLog("Testrail test Run Added Successfully");
            } else {
                assert pId != null;
                run = testRail.runs().add(Integer.parseInt(pId), new Run().setSuiteId(Integer.parseInt(sId)).setName(testRunName)).execute();
                writeLog("Testrail test Run Added Successfully");
            }
        } catch (Exception e) {
            writeLogError(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void addTestResult(String testRunId, String testCaseId) {
        try {
            if (Objects.equals(testCaseId, strTestcaseID)) {
                if (Objects.equals(scenarioStatus, "PASSED")) {
                    List<ResultField> customResultFields = testRail.resultFields().list().execute();
                   
                    if (testRunId == null) {
                        testRail.results().addForCase(run.getId(), Integer.parseInt(testCaseId), new Result().setStatusId(TEST_CASE_PASS_STATUS), customResultFields).execute();
                        writeLog("Testrail Run id : " + run.getId());
                        writeLog("Test Result Added to Testrail");
                    } else {                    
                        testRail.results().addForCase(Integer.parseInt(testRunId), Integer.parseInt(testCaseId), new Result().setStatusId(TEST_CASE_PASS_STATUS), customResultFields).execute();
                        writeLog("Testrail Run id : " + testRunId);
                        writeLog("Test Result Added to Testrail");
                    }
                } else if (Objects.equals(scenarioStatus, "FAILED")) {
                    List<ResultField> customResultFields = testRail.resultFields().list().execute();
                    if (testRunId == null) {
                        testRail.results().addForCase(run.getId(), Integer.parseInt(testCaseId), new Result().setStatusId(TEST_CASE_FAIL_STATUS), customResultFields).execute();
                        writeLog("Testrail Runid : " + run.getId());
                        writeLog("Test Result Added to Testrail");
                    } else {
                        testRail.results().addForCase(Integer.parseInt(testRunId), Integer.parseInt(testCaseId), new Result().setStatusId(TEST_CASE_FAIL_STATUS), customResultFields).execute();
                        writeLog("Testrail Run id : " + testRunId);
                        writeLog("Result added to Testrail");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            writeLogError(e.getMessage());
        }
    }
}
