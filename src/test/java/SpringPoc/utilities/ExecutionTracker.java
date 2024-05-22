package SpringPoc.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static SpringPoc.utilities.EmailUtil.path;

public class ExecutionTracker implements ConcurrentEventListener {
    private String featureName;
    private static Set<String> dataListForTestDatas = new LinkedHashSet<String>();
    private static Set<Object> dataListForOverAllExecutionStatus = new LinkedHashSet<>();

    private int passCount = 0;
    private int failCount = 0;
    private int skipCount = 0;


    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseFinished.class, this::afterScenario);
        publisher.registerHandlerFor(TestCaseStarted.class, this::beforeScenario);
        publisher.registerHandlerFor(TestStepFinished.class, this::afterStepStatus);
        publisher.registerHandlerFor(TestSourceRead.class, this::featureSourceRead);
        publisher.registerHandlerFor(TestRunFinished.class, this::testRunFinished);
        publisher.registerHandlerFor(TestRunStarted.class, this::testRunStarted);
    }

    private void afterStepStatus(TestStepFinished event) {
        try {
            PickleStepTestStep pickleStep = (PickleStepTestStep) event.getTestStep();
            List<Argument> elmnts = pickleStep.getDefinitionArgument();

            for (Argument elmnt : elmnts) {
                String[] strParameter = elmnt.getValue().split("\"");

                for (String example : strParameter) {
                    if (!example.isBlank())
                        dataListForTestDatas.add(example);

                }

            }
        } catch (Exception e) {
        }
    }

    ;

    private void beforeScenario(TestCaseStarted event) {
        dataListForTestDatas.clear(); //Example Datas...
    }

    ;


    private void afterScenario(TestCaseFinished event) {

        String strScenarioDescription = event.getTestCase().getName();
        String strTestcaseKeyword = event.getTestCase().getKeyword().toLowerCase();

        Result result = event.getResult();
        String status = getStatus(result);
        if ("Passed".equals(status)) {
            passCount++;
        } else if ("Failed".equals(status)) {
            failCount++;
        } else if ("Skipped".equals(status)) {
            skipCount++;
        }


        LinkedHashMap<String, String> scenarioStatus = new LinkedHashMap<>();

        if (strTestcaseKeyword.equals("scenario outline")) {
            scenarioStatus.put("Type", strTestcaseKeyword);
            scenarioStatus.put("Name", strScenarioDescription);
            scenarioStatus.put("Scenario_id", event.getTestCase().getId().toString());
            scenarioStatus.put("Status", getStatus(result));
            String strExample = new StringBuilder().append("|").append(" ")
                    .append(String.join(" | ", dataListForTestDatas)).append(" ").append("|").toString();
            scenarioStatus.put("Example", strExample);
        } else {
            scenarioStatus.put("Type", strTestcaseKeyword);
            scenarioStatus.put("Name", strScenarioDescription);
            scenarioStatus.put("Scenario_id", event.getTestCase().getId().toString());
            scenarioStatus.put("Status", getStatus(result));
        }
        dataListForOverAllExecutionStatus.add(scenarioStatus);
    }

    ;

    private void featureSourceRead(TestSourceRead event) {
        featureName = getFeatureName(event.getSource());
        System.out.println("Feature Name: " + featureName);
    }

    ;

    private void testRunStarted(TestRunStarted event) {
        deleteJSONFile();   //To check delete the JSON file
    }

    ;

    private final String STR_FEATURE_REGEX = "Feature:(.*)";

    private String getFeatureName(String source) {
        String pattern = STR_FEATURE_REGEX;
        Matcher matcher = Pattern.compile(pattern).matcher(source);
        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            return null;
        }
    }

    private void testRunFinished(TestRunFinished testrun) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        LinkedHashMap<String, String> scenarioCounts = new LinkedHashMap<>();
        scenarioCounts.put("TotalScenarios", String.valueOf(passCount + failCount + skipCount));
        scenarioCounts.put("Passed", String.valueOf(passCount));
        scenarioCounts.put("Failed", String.valueOf(failCount));
        scenarioCounts.put("Skipped", String.valueOf(skipCount));

        dataListForOverAllExecutionStatus.add(scenarioCounts);

        String json = null;
        try {
            json = mapper.writeValueAsString(dataListForOverAllExecutionStatus);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        saveToJSONFile(json);
    }

    private String getStatus(Result result) {
        return result.getStatus().isOk() ? "Passed" : "Failed";
    }


    String strEmailJsonPath = "/reports/cucumber/email-report.json";

    public void saveToJSONFile(String txtJSON) {
        try {
            File newTextFile = new File(path + strEmailJsonPath);
            FileWriter fw = new FileWriter(newTextFile);
            fw.write(txtJSON);

            fw.close();
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }


    private void deleteJSONFile() {
        try {
            File file = new File(path + strEmailJsonPath);
            file.delete();
        } catch (Exception e) {
            System.out.println("Delete File: " + e.getMessage());
        }
    }

}