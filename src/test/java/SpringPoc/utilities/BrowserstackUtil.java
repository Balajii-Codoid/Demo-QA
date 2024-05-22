package SpringPoc.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Base64;
import java.util.List;

public class BrowserstackUtil {

    public static String uploadAppInBrowserStack(String strUploadAppApiUrl,String strAppPath, String strFileType, String strUserName, String strAutomateKey) {
        String appUrl = "";
        String filePath;
        RestTemplate restTemplate = new RestTemplate();

        // Create headers with authentication and content type
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(strUserName, strAutomateKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Create the request body with the file
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        filePath = FileUtil.getFileNameFromFolder(strAppPath, strFileType);
        body.add("file", new FileSystemResource(new File(strAppPath, filePath)));


        // Create the HTTP entity with headers and body
        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

        // Make the POST request using exchange method
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                strUploadAppApiUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // Extract and print the response
        System.out.println("Response Code: " + responseEntity.getStatusCode());
        System.out.println("Response Body: " + responseEntity.getBody());


        // Check if the request was successful (status code 200)
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            // Use Jackson ObjectMapper to parse the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // Parse the JSON response
                JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());

                // Get the value of the "app_url" field
                appUrl = rootNode.path("app_url").asText();

                // Print the app_url
                System.out.println("App URL: " + appUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Error: " + responseEntity.getStatusCode());
            System.err.println("Response Body: " + responseEntity.getBody());
        }
        return appUrl;
    }


    public static void deleteExistingAPKInBrowserstack(String strExistingAppApiUrl, String strDeleteApiUrl, String strUserName, String strAutomateKey) {
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((strUserName + ":" + strAutomateKey).getBytes());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, authHeader);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    strExistingAppApiUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            Assert.assertEquals(responseEntity.getStatusCodeValue(), 200);

            JsonNode jsonNode = new ObjectMapper().readTree(responseEntity.getBody());
            List<String> lstAppID = jsonNode.findValuesAsText("app_id");

            for (String strMultipleAppID : lstAppID) {

                ResponseEntity<String> deleteResponseEntity = restTemplate.exchange(
                        strDeleteApiUrl + strMultipleAppID,
                        HttpMethod.DELETE,
                        entity,
                        String.class
                );

                System.out.println("\n GET Delete response:::: " + deleteResponseEntity.getBody());

            }
            System.out.println("<----------------------APK Deleted---------------------->");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n BS APP Delete error : Unable to delete APK");
        }


    }

}
