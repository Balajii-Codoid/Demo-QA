package SpringPoc.utilities;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static SpringPoc.utilities.YamlUtil.getYamlData;

@Component
public class EmailUtil {

    public static String path = System.getProperty("user.dir");
    List<Map<String, Object>> scenarios = new ArrayList<>();

    private String emailSender = getYamlData("email.sender");

    private List<String> emailToRecipients = List.of(getYamlData("email.recipient").split(","));

    private List<String> emailCcRecipients = List.of(getYamlData("email.ccrecipient").split(","));
    private String emailPassword = getYamlData("email.password");
    private String emailSmtpHost = getYamlData("email.smtpHost");
    private String emailSmtpPort = getYamlData("email.smtpPort");
    private String emailSubject = getYamlData("email.subject");

    //For Furture Reference
//    @Value("${email.sender}")
//    private String emailSender;
//
//    @Value("#{'${email.recipient}'.split(',')}")
//    private List<String> emailToRecipients;
//
//    @Value("#{'${email.ccrecipient}'.split(',')}")
//    private List<String> emailCcRecipients;
//
//    @Value("${email.password}")
//    private String emailPassword;
//
//    @Value("${email.smtpHost}")
//    private String emailSmtpHost;
//
//    @Value("${email.smtpPort}")
//    private String emailSmtpPort;
//
//    @Value("${email.subject}")
//    private String emailSubject;


    public void emailNotification() {
        try {
            // Create FreeMarker configuration
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
            configuration.setClassForTemplateLoading(EmailUtil.class, "/");
            configuration.setDefaultEncoding("UTF-8");
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            getScenariosDataFromJson();

            // Load the template
            Template template = configuration.getTemplate("template.ftl");


            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("Path: " + path + "/reports/cucumber/email-report.json");
            File jsonFile = new File(path + "/reports/cucumber/email-report.json");
            objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class);
            List<Map<String, Object>> reportData = objectMapper.readValue(jsonFile, collectionType);


            // Process the template
            StringWriter writer = new StringWriter();
            Map<String, Object> data = new HashMap<>();
            data.put("Scenarios", scenarios);
            data.put("reportData", reportData);
            template.process(data, writer);

            // Output the rendered HTML
            String renderedHtml = writer.toString();
            sendEmail(renderedHtml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getScenariosDataFromJson() {
        String jsonFilePath = path + "/reports/cucumber/email-report.json";
        System.out.println("jsonFilePath: " + jsonFilePath);
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            // Parse the JSON content using JsonPath
            DocumentContext jsonContext = JsonPath.parse(jsonContent);

            List<Map<String, Object>> scenarios1 = jsonContext.read("$");

            // Iterate over each scenario
            for (Map<String, Object> scenario : scenarios1) {
                // Create a new data model for each scenario
                Map<String, Object> dataModel = new HashMap<>();
                // Iterate over each entry in the scenario map
                for (Map.Entry<String, Object> entry : scenario.entrySet()) {
                    String key = entry.getKey().toLowerCase();  // Convert key to lowercase for case-insensitive comparison
                    dataModel.put(key, entry.getValue());
                }
                // Add the data model to the scenarios list
                scenarios.add(dataModel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendEmail(String renderedHtml) {
        // Set properties for SMTP server
        Properties properties = new Properties();
        properties.put("mail.smtp.host", emailSmtpHost);
        properties.put("mail.smtp.port", emailSmtpPort);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Create a Session object with the SMTP server properties
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailSender, emailPassword);
            }
        });

        try {
            // Create a MimeMessage object
            Message message = new MimeMessage(session);

            // Set To: header field of the header
            message.setFrom(new InternetAddress(emailSender));
            InternetAddress[] recipientAddresses = new InternetAddress[emailToRecipients.size()];
            for (int i = 0; i < emailToRecipients.size(); i++) {
                recipientAddresses[i] = new InternetAddress(emailToRecipients.get(i));
            }
            message.addRecipients(Message.RecipientType.TO, recipientAddresses);

            // Set CC: header field of the header
            if (emailCcRecipients.get(0).contains("@")) {
                InternetAddress[] ccAddresses = new InternetAddress[emailCcRecipients.size()];
                for (int i = 0; i < emailCcRecipients.size(); i++) {
                    ccAddresses[i] = new InternetAddress(emailCcRecipients.get(i));
                }
                message.addRecipients(Message.RecipientType.CC, ccAddresses);
            }

            message.setSubject(emailSubject);

            Multipart multipart = new MimeMultipart();
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText("Hello, Please find the attached overall Automation execution status");

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(renderedHtml, "text/html");
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);
            Transport.send(message);
            System.out.println("Email sent successfully!!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
