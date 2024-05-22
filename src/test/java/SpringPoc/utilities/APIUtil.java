package SpringPoc.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static SpringPoc.utilities.YamlUtil.getYamlData;

@Component
public class APIUtil {

    public static RestTemplate restTemplate = new RestTemplate();
    public static ResponseEntity<String> responseEntity;
    public static HttpHeaders headers = new HttpHeaders();
    public static String strValue;
    public static String responseBody;
    public static long responseTime;
    public static HttpEntity<String> requestEntity;

    public static String strBaseUrl = getYamlData("api.base_url");

    /**
     * This method always perform get action
     * @param strApiEndPoint need to pass the api end point
     * @return response of the end point
     */
    public static ResponseEntity<String> get(String strApiEndPoint) {
        try {
            long startTime = System.currentTimeMillis();

            System.out.println("URL = "+strBaseUrl+strApiEndPoint);

            responseEntity = restTemplate.getForEntity(strBaseUrl+strApiEndPoint, String.class);

            long endTime = System.currentTimeMillis();

            responseTime = endTime - startTime;

            return responseEntity;

        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
        }
        return null;
    }

    /**
     *
     * This method always perform post action.
     * It gets request body from json file
     * @param strApiEndPoint need to pass the api end point
     * @param filePath path for .json file
     * @return response of the end point
     * @throws IOException
     */
    public static ResponseEntity<String> postBodyFromJsonFile(String strApiEndPoint, String filePath) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        File jsonFile = new File(filePath);

        String requestBody = objectMapper.readTree(jsonFile).toString();
        try {
            headers.setContentType(MediaType.APPLICATION_JSON);

            requestEntity = new HttpEntity<>(requestBody, headers);

            responseEntity = restTemplate.postForEntity(strBaseUrl+strApiEndPoint, requestEntity, String.class);

            return responseEntity;

        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
            System.err.println("Error: " + responseEntity.getStatusCode());
        }

        return null;
    }

    /**
     *
     * This method always perform post action.
     * It gets request body from data table
     * @param strApiEndPoint need to pass the api end point
     * @param dataTable pass the request body from datatable
     * @return response of the end point
     */
    public static ResponseEntity<String> postBodyFromDatatable(String strApiEndPoint, DataTable dataTable) {
        try {
            String requestBody = null;

            List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
            for (Map<String, String> row : rows) {
                ObjectMapper objectMapper = new ObjectMapper();
                requestBody = objectMapper.writeValueAsString(row);
            }

            headers.setContentType(MediaType.APPLICATION_JSON);

            requestEntity = new HttpEntity<>(requestBody, headers);

            responseEntity = restTemplate.postForEntity(strBaseUrl+strApiEndPoint, requestEntity, String.class);

            return responseEntity;

        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
            System.err.println("Error: " + responseEntity.getStatusCode());
        }

        return null;
    }

    /**
     *
     * This method always perform post action.
     * It gets header from data table
     * It gets request body from json file
     * @param strApiEndPoint need to pass the api end point
     * @param filePath path for .json file
     * @param dataTable pass header from datatable
     * @return response of the end point
     */
    public static ResponseEntity<String> postBodyWithHeader(String strApiEndPoint, String filePath,DataTable dataTable) {
        try {

            List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
            for (Map<String, String> row : rows) {
                String key = row.get("key");
                String value = row.get("value");
                headers.set(key, TestDataUtil.getValue(value));
            }

            ObjectMapper objectMapper = new ObjectMapper();

            File jsonFile = new File(filePath);

            String requestBody = objectMapper.readTree(jsonFile).toString();

            requestEntity = new HttpEntity<>(requestBody, headers);

            long startTime = System.currentTimeMillis();

            responseEntity = restTemplate.postForEntity(strBaseUrl+strApiEndPoint, requestEntity, String.class);

            long endTime = System.currentTimeMillis();

            responseTime = endTime - startTime;

            return responseEntity;

        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
            System.err.println("Error: " + responseEntity.getStatusCode());
        }

        return null;
    }

    /**
     *
     * This method verifies status code and status message.
     * @param strExpectedStatus need to pass expected status code and status message
     */
    public boolean verifyCurrentStatus(String strExpectedStatus) {
        boolean isVerified = false;
        try {
            System.out.println("responseEntity.getStatusCode() ::: "+responseEntity.getStatusCode());
            String strActualStatus = String.valueOf(responseEntity.getStatusCode());
            if (strActualStatus.equalsIgnoreCase(strExpectedStatus)) {
                isVerified = true;
            }
            return isVerified;

        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
        }
        return isVerified;
    }

    /**
     *
     * This method always return status code
     * @return response code
     */

    public static String getCurrentStatusCode() {
        try {
            return String.valueOf(responseEntity.getStatusCode());

        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
        }
        return null;
    }

    /**
     *
     * This method always get specific value from the response body.
     * @param strKey need to pass key
     * @return required value
     */
    public static String getValue(String strKey) {
        try {

            String key = "\"" + strKey + "\"" + ":\"";

            responseBody = responseEntity.getBody();

            int startIndex = responseBody.indexOf(key);
            if (startIndex != -1) {
                int endIndex = responseBody.indexOf("\"", startIndex + key.length());
                if (endIndex != -1) {
                    strValue = responseBody.substring(startIndex + key.length(), endIndex);
                }
            }
            return strValue;

        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
        }

        return null;
    }

    /**
     *
     * This method always pass auth token into the header
     * @param requestBody
     * @param strApiEndPoint
     */
    public static void passAuthToken(String requestBody, String strApiEndPoint) {
        try {
            String strToken = "\"" + "token" + "\"" + ":\"";

            String accessToken = getValue(strToken);

            headers.set("Authorization", "Bearer " + accessToken);

            requestEntity = new HttpEntity<>(requestBody, headers);

        } catch (Exception e) {
            System.err.println("Error: " + responseEntity.getStatusCode());
        }
    }

    /**
     *
     * This method always perform delete action
     * @param strApiUrl need to pass the api end point
     * @return response of the end point
     */
    public static ResponseEntity<String> delete(String strApiUrl) {
        responseEntity = restTemplate.exchange(
                strApiUrl,
                HttpMethod.DELETE,
                null,
                String.class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            System.out.println("DELETE request successful");
        } else {
            System.err.println("Error: " + responseEntity.getStatusCode());
        }
        return responseEntity;
    }

    /**
     *
     * This method always perform put action
     * @param strApiEndPoint need to pass the api end point
     * @param requestBody need to pass request body
     * @return response of the end point
     */

    public static ResponseEntity<String> put(String strApiEndPoint,String requestBody) {

        headers.setContentType(MediaType.APPLICATION_JSON);

        requestEntity = new HttpEntity<>(requestBody, headers);

        responseEntity = restTemplate.exchange(
                strBaseUrl+strApiEndPoint,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        String responseBody = responseEntity.getBody();

        System.out.println("Response: " + responseBody);

        return responseEntity;
    }

    /**
     *
     * This method always perform post function for upload form-data
     * @param strApiEndPoint need to pass the api end point
     * @param strAppPath need to pass app path
     * @param strFileType need to pass file type
     * @param strUsername need to pass username
     * @param strToken need to pass the token
     */

    public static boolean uploadFile(String strApiEndPoint, String strAppPath, String strFileType, String strUsername, String strToken) {
        boolean isUploaded = false;
        String filePath;
        restTemplate = new RestTemplate();

        // Create headers with authentication and content type
        headers = new HttpHeaders();
        headers.setBasicAuth(strUsername, strToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Create the request body with the file
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        filePath = FileUtil.getFileNameFromFolder(strAppPath, strFileType);
        body.add("file", new FileSystemResource(new File(strAppPath, filePath)));


        // Create the HTTP entity with headers and body
        org.springframework.http.HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new org.springframework.http.HttpEntity<>(body, headers);

        // Make the POST request using exchange method
        responseEntity = restTemplate.exchange(
                strBaseUrl+strApiEndPoint,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // Check if the request was successful (status code 200)
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            isUploaded = true;
        } else {
            System.err.println("Error: " + responseEntity.getStatusCode());
            System.err.println("Response Body: " + responseEntity.getBody());
        }
        return isUploaded;
    }

    /**
     *
     * This method always perform http post function
     * @param strApiEndPoint need to pass the api end point
     * @param strAppPath need to pass app path
     * @param strFileType need to pass file type
     * @param strUsername need to pass username
     * @param strAutomateKey need to pass automate key
     * @return app url
     */

    public static String uploadAppInBrowserStack(String strApiEndPoint, String strAppPath, String strFileType, String strUsername, String strAutomateKey) {
        String appUrl = "";
        String filePath;

        // Create headers with authentication and content type
        headers = new HttpHeaders();
        headers.setBasicAuth(strUsername, strAutomateKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Create the request body with the file
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        filePath = FileUtil.getFileNameFromFolder(strAppPath, strFileType);
        body.add("file", new FileSystemResource(new File(strAppPath, filePath)));


        // Create the HTTP entity with headers and body
        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new org.springframework.http.HttpEntity<>(body, headers);

        // Make the POST request using exchange method
        responseEntity = restTemplate.exchange(
                strBaseUrl+strApiEndPoint,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        try {
            // Check if the request was successful (status code 200)
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                // Use Jackson ObjectMapper to parse the JSON response
                ObjectMapper objectMapper = new ObjectMapper();
                // Parse the JSON response
                JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());

                // Get the value of the "app_url" field
                appUrl = rootNode.path("app_url").asText();

                // Print the app_url
                System.out.println("App URL: " + appUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + responseEntity.getStatusCode());
            System.err.println("Response Body: " + responseEntity.getBody());
        }
        return appUrl;
    }

    /**
     *
     * This method always verifies response time
     * @param intMs need to pass expected time ex.3000 ms
     */
    public boolean verifyResponseTime(int intMs){
        boolean isVerified;
        isVerified = intMs > responseTime;
        return isVerified;
    }
}