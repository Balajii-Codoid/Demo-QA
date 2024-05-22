package SpringPoc.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import jakarta.annotation.PostConstruct;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static SpringPoc.utilities.FileUtil.writeLog;
import static SpringPoc.utilities.YamlUtil.getYamlData;


@Component
public class DriverUtil {

    public static WebDriver driver;
    public static WebDriver mobileDriver;
    public static long randomID;
    public static DesiredCapabilities capability = null;
    public static WebDriverWait webDriverWait;
    public static WebDriverWait mobileDriverWait;

    public static String strWaitTime = System.getProperty(Constants.ENV_WAITTIME, "");

    public static String strTimeoutDuration = YamlUtil.getYamlData(Constants.ENV_WAITTIME, strWaitTime);

    public static String BS_APP_URL;
    public static String StrUserName = getYamlData("BrowserStack.Username", "");
    public static String StrAutomateKey = getYamlData("BrowserStack.AutomateKey", "");

    @Autowired
    private EmailUtil emailUtil;

    @PostConstruct
    public void initialize() {

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (isDriverLoaded()) {
                writeLog("CLOSING OPENED DRIVERS");
                closeDriver();
                writeLog("OPENED DRIVERS CLOSED");
            }
        }));
    }

    private boolean isDriverLoaded() {
        return driver != null || mobileDriver != null;
    }

    public WebDriver getDriver() {
        String strConfig = System.getProperty(Constants.ENV_VARIABLE_CONFIG, "");
        String strBrowser = System.getProperty(Constants.ENV_VARIABLE_BROWSER_NAME, "");
        String strExecutionType = System.getProperty(Constants.ENV_VARIABLE_EXECUTION_TYPE, "");
        writeLog("EXECUTION TYPE: " + strExecutionType.toUpperCase());
        writeLog("BROWSER NAME: " + strBrowser.toUpperCase());

        if (strConfig.isEmpty() || strExecutionType.equalsIgnoreCase("WEBMOBILE")) {
            driver = getLocalDriver(strBrowser);
            webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(Integer.parseInt(strTimeoutDuration)));
        } else if (strConfig.equalsIgnoreCase("remote-docker")) {
            String strRemoteIP = System.getProperty(Constants.ENV_VARIABLE_REMOTE_IP, "");
            String strRemotePort = System.getProperty(Constants.ENV_VARIABLE_REMOTE_PORT, "");

            if (strRemoteIP.isEmpty()) {
                strRemoteIP = Constants.DOCKER_IP;
            }

            if (strRemotePort.isEmpty()) {
                strRemotePort = Constants.DOCKER_PORT;
            }

            try {
                String strURL = new StringBuilder()
                        .append("http://")
                        .append(strRemoteIP)
                        .append(":")
                        .append(strRemotePort)
                        .append("/wd/hub")
                        .toString();

                System.out.println("\n DOCKER URL :: " + strURL);

                if (strBrowser.equalsIgnoreCase("chrome")) {
                    driver = new RemoteWebDriver(new URL(strURL), chromeCapabilities());
                } else if (strBrowser.equalsIgnoreCase("firefox")) {
                    driver = new RemoteWebDriver(new URL(strURL), firefoxOptions());
                } else {
                    driver = new RemoteWebDriver(new URL(strURL), edgeOptions());
                }
                webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(Long.parseLong(strTimeoutDuration)));
            } catch (Exception ex) {
                throw new Error(ex.getMessage());
            }
        }

        return driver;
    }

    public void closeDriver() {
        String strExecutionType = System.getProperty(Constants.ENV_VARIABLE_EXECUTION_TYPE);
        try {
            if (strExecutionType.equalsIgnoreCase("BROWSER")) {
                driver.quit();
            } else if (strExecutionType.equalsIgnoreCase("MOBILE")) {
                mobileDriver.quit();
            } else if (strExecutionType.equalsIgnoreCase("WEBMOBILE")) {
                driver.quit();
                mobileDriver.quit();
            }

            //Email Notification Configuration
            if (getYamlData("email.enable", System.getProperty(Constants.ENV_EMAIL)).equalsIgnoreCase("TRUE")) {
                emailUtil.emailNotification();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WebDriver getLocalDriver(String strBrowserName) {
        WebDriver driver = null;
        try {
            switch (strBrowserName) {
                case "firefox":
                    driver = new FirefoxDriver(firefoxOptions());
                    break;
                case "chrome":
                    driver = new ChromeDriver(chromeCapabilities());
                    break;
                case "edge":
                    driver = new EdgeDriver();
                    break;
                default:
                    driver = new ChromeDriver(chromeCapabilities());
                    break;
            }
            webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(Integer.parseInt(strTimeoutDuration)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return driver;
    }

    public ChromeOptions chromeCapabilities() {

        String strWorkingDirectory = System.getProperty("user.dir");
        String strDownloadLocation = new StringBuilder(strWorkingDirectory)
                .append(File.separator).append(Constants.DOWNLOAD_PATH).toString();
        ChromeOptions options = new ChromeOptions();
        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();

        // Hide save credentials prompt
        chromePrefs.put("credentials_enable_service", false);
        chromePrefs.put("profile.password_manager_enabled", false);

        // Default download directory
        chromePrefs.put("download.default_directory", strDownloadLocation);
        chromePrefs.put("profile.default_content_setting_values.automatic_downloads", 1);
        chromePrefs.put("safebrowsing.enabled", "true");
        chromePrefs.put("autofill.profile_enabled", false);
        //        To run in Headless mode
        String strHeadless = System.getProperty(Constants.ENV_VARIABLE_BROWSER_HEADLESS, "");
        System.out.println("\n headless -------- "  + strHeadless);
        if (strHeadless.equalsIgnoreCase("HEADLESS")) {
            options.addArguments("--window-size=1920,1080");
//            options.addArguments("start-maximized");
//            options.addArguments("--proxy-server='direct://'");
//            options.addArguments("--proxy-bypass-list=*");
            System.out.println("\n Headless mode");
            options.addArguments("--headless=new");
        }
        options.addArguments("--remote-allow-origins=*", "ignore-certificate-errors");
        options.addArguments("disable-infobars");
        options.addArguments("chrome.switches", "--disable-extensions");
        options.addArguments("--start-maximized");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches",
                Collections.singletonList("enable-automation"));
        options.setExperimentalOption("prefs", chromePrefs);
        return options;
    }

    public FirefoxOptions firefoxOptions() throws Exception {
        FirefoxOptions options = new FirefoxOptions();
        String strWorkingDirectory = System.getProperty("user.dir");
        String strDownloadLocation = new StringBuilder(strWorkingDirectory)
                .append(File.separator).append(Constants.DOWNLOAD_PATH).toString();

        options.addPreference("browser.download.folderList", 2);
        options.addPreference("browser.download.manager.showWhenStarting", false);
        //Set downloadPath
        options.addPreference("browser.download.dir", strDownloadLocation);
        options.addPreference("browser.helperApps.neverAsk.openFile",
                "text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml;text/html;message/rfc822;application/octet-stream;");
        options.addPreference("browser.helperApps.neverAsk.saveToDisk",
                "text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml;text/html;message/rfc822;application/octet-stream;");

        options.addPreference("browser.download.manager.showWhenStarting", false);
        options.addPreference("pdfjs.disabled", true);
        options.addPreference("browser.helperApps.alwaysAsk.force", false);
        options.addPreference("browser.download.manager.alertOnEXEOpen", false);
        options.addPreference("browser.download.manager.focusWhenStarting", false);
        options.addPreference("browser.download.manager.useWindow", false);

        options.addPreference("browser.download.manager.showAlertOnComplete", false);
        options.addPreference("browser.download.manager.closeWhenDone", false);
        return options;
    }

    public static EdgeOptions edgeOptions() {
        EdgeOptions options = new EdgeOptions();
        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

        return options;
    }

    public WebDriver getMobileDriver() {
        WebDriver driver = null;
        String strExecutionType = System.getProperty(Constants.ENV_VARIABLE_EXECUTION_TYPE, "");
        String strConfig = System.getProperty(Constants.ENV_VARIABLE_CONFIG, "");
        writeLog("EXECUTION TYPE: " + strExecutionType.toUpperCase());
        writeLog("MOBILE PROPERTY NAME: " + strConfig);
        if (!strConfig.isEmpty()) {
            capability = getCapability(strExecutionType, strConfig, "");
            driver = getAppiumDriver(capability);
        } else {
            System.exit(0);
        }
        return driver;
    }

    public DesiredCapabilities getCapability(String strExecutionType, String strConfig, String strBrowser) {
        DesiredCapabilities capability = new DesiredCapabilities();
        Properties config_prop = new Properties();
        InputStream config_inputStream = null;
        switch (strExecutionType) {
            case "mobile":
                try {
                    config_inputStream = new FileInputStream(new StringBuilder()
                            .append(Constants.CONFIG_FOLDER)
                            .append("/")
                            .append(Constants.ENV_VARIABLE_MOBILE)
                            .append("/")
                            .append(strConfig).append(".properties").toString());

                    config_prop.load(config_inputStream);

                    if (!strBrowser.isEmpty()) {
                        capability.setBrowserName(strBrowser);
                    }

                    // set capabilities
                    Enumeration<Object> enuKeys = config_prop.keys();
                    while (enuKeys.hasMoreElements()) {
                        String key = (String) enuKeys.nextElement();
                        String value = config_prop.getProperty(key);
                        capability.setCapability(key, value);
                        System.setProperty(key, value);
                        System.out.println(key + " : " + value);
                    }
                    capability.setCapability("newCommandTimeout", 15000);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("\nFile not present or Invalid config file name " + strConfig + ".properties");
                    System.exit(0);
                } finally {
                    try {
                        config_inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "webmobile":
                try {
                    config_inputStream = new FileInputStream(new StringBuilder()
                            .append(Constants.CONFIG_FOLDER)
                            .append("/")
                            .append(Constants.ENV_VARIABLE_MOBILE)
                            .append("/")
                            .append(strConfig).append(".properties").toString());

                    config_prop.load(config_inputStream);

                    if (!strBrowser.isEmpty()) {
                        capability.setBrowserName(strBrowser);
                    }

                    // set capabilities
                    Enumeration<Object> enuKeys = config_prop.keys();
                    while (enuKeys.hasMoreElements()) {
                        String key = (String) enuKeys.nextElement();
                        String value = config_prop.getProperty(key);
                        capability.setCapability(key, value);
                        System.setProperty(key, value);
                        System.out.println(key + " : " + value);
                    }
                    capability.setCapability("newCommandTimeout", 15000);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("\nCAP Fatal Error : File not present or Invalid config file name " + strConfig + ".properties");
                    System.exit(0);
                } finally {
                    try {
                        config_inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;


            case "WINDOWS":
                System.out.println(" - Caps - Windows");
                try {
                    config_inputStream = new FileInputStream(new StringBuilder()
                            .append(Constants.CONFIG_FOLDER)
                            .append("/")
                            .append(Constants.ENV_VARIABLE_WINDOW)
                            .append("/")
                            .append(strConfig).append(".properties").toString());

                    config_prop.load(config_inputStream);

                    if (!strBrowser.isEmpty()) {
                        capability.setBrowserName(strBrowser);
                    }

                    // set capabilities
                    Enumeration<Object> enuKeys = config_prop.keys();
                    while (enuKeys.hasMoreElements()) {
                        String key = (String) enuKeys.nextElement();
                        String value = config_prop.getProperty(key);
                        capability.setCapability(key, value);
                        System.setProperty(key, value);
                    }
                    capability.setCapability("newCommandTimeout", 15000);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("\nCAP Fatal Error : File not present or Invalid config file name " + strConfig + ".properties");
                    System.exit(0);
                } finally {
                    try {
                        config_inputStream.close();
                    } catch (Exception e) {
                    }
                }
                break;

            case "both":
                try {
                    config_inputStream = new FileInputStream(new StringBuilder()
                            .append(Constants.CONFIG_FOLDER)
                            .append("/")
                            .append(Constants.ENV_VARIABLE_WINDOW)
                            .append("/")
                            .append(strConfig).append(".properties").toString());

                    config_prop.load(config_inputStream);

                    if (!strBrowser.isEmpty()) {
                        capability.setBrowserName(strBrowser);
                    }

                    // set capabilities
                    Enumeration<Object> enuKeys = config_prop.keys();
                    while (enuKeys.hasMoreElements()) {
                        String key = (String) enuKeys.nextElement();
                        String value = config_prop.getProperty(key);
                        capability.setCapability(key, value);
                        System.setProperty(key, value);
                    }
                    capability.setCapability("newCommandTimeout", 15000);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("\nCAP Fatal Error : File not present or Invalid config file name " + strConfig + ".properties");
                    System.exit(0);
                } finally {
                    try {
                        config_inputStream.close();
                    } catch (Exception e) {
                    }
                }
                break;
        }
        return capability;
    }

    public WebDriver getAppiumDriver(DesiredCapabilities capability) {
        WebDriver driver = null;
        try {

            String strRemoteIP = System.getProperty(Constants.ENV_VARIABLE_REMOTE_IP, "");
            String strRemotePort = System.getProperty(Constants.ENV_VARIABLE_REMOTE_PORT, "");
            System.out.println("");
            if (strRemoteIP.isEmpty()) {
                strRemoteIP = Constants.APPIUM_LOCAL_IP;
            }

            if (strRemotePort.isEmpty()) {
                strRemotePort = Constants.APPIUM_LOCAL_PORT;
            }
            String strURL = "";
            if (System.getProperty(Constants.ENV_VARIABLE_CONFIG, "").contains("remote_")) {
                System.out.println("\n enter if... remote Execut");
                String StrDeleteAppUrl = getYamlData("BrowserStack.DeleteURL", "");
                String StrUploadAppUrl = getYamlData("BrowserStack.URL", "");
                String StrExistingAppAppUrl = getYamlData("BrowserStack.ExistingAppDetailsURL", "");

                strURL = "https://" + StrUserName + ":" + StrAutomateKey + "@hub-cloud.browserstack.com/wd/hub";
                int connectionTimeout = 20 * 60 * 1000;
                int socketTimeout = 90 * 1000;
                BrowserstackUtil.deleteExistingAPKInBrowserstack(StrExistingAppAppUrl, StrDeleteAppUrl, StrUserName, StrAutomateKey);
                if (capability.getPlatformName().toString().equalsIgnoreCase(Constants.ANDROID.toUpperCase())) {
                    BS_APP_URL = BrowserstackUtil.uploadAppInBrowserStack(StrUploadAppUrl, Constants.APP_PATH, "apk", StrUserName, StrAutomateKey);

                } else if (capability.getPlatformName().toString().equalsIgnoreCase(Constants.IOS.toUpperCase())) {
                    BS_APP_URL = BrowserstackUtil.uploadAppInBrowserStack(StrUploadAppUrl, Constants.APP_PATH, "ipa", StrUserName, StrAutomateKey);
                }

                capability.setCapability("app", BS_APP_URL);

            } else {
                strURL = new StringBuilder()
                        .append("http://")
                        .append(strRemoteIP)
                        .append(":")
                        .append(strRemotePort)
                        .append("/wd/hub")
                        .toString();
            }
            if (capability.getPlatformName().toString().equalsIgnoreCase(Constants.ANDROID.toUpperCase())) {
                driver = new AndroidDriver(new URL(strURL), capability);

            } else if (capability.getPlatformName().toString().equalsIgnoreCase(Constants.IOS.toUpperCase())) {
                driver = new IOSDriver(new URL(strURL), capability);
            }
            System.setProperty("PLATFORM", capability.getPlatformName().toString().toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(new StringBuilder().append("\nCAP Fatal Error : ").append(e.getMessage()));
            System.exit(0);
        }

        return driver;
    }

    public WebDriver getBrowserStackDriver() {
        try {
            DesiredCapabilities capability = new DesiredCapabilities();
            String strRemoteURL = "https://" + StrUserName + ":" + StrAutomateKey + "@hub-cloud.browserstack.com/wd/hub";
            System.out.println("strRemoteURL" + strRemoteURL);
            String strExecutionConfiguration = System.getProperty(Constants.ENV_VARIABLE_CONFIG, "");
            String strMapdata = strExecutionConfiguration + ".";

            // set the capability
            capability.setCapability("browserName", YamlUtil.getYamlData(strMapdata + "browserName"));
            capability.setCapability("browserVersion", YamlUtil.getYamlData(strMapdata + "browserVersion"));
            HashMap<String, Object> browserstackOptions = new HashMap<String, Object>();
            browserstackOptions.put("os", YamlUtil.getYamlData(strMapdata + "os"));
            browserstackOptions.put("osVersion", YamlUtil.getYamlData(strMapdata + "osVersion"));
            capability.setCapability("bstack:options", browserstackOptions);
            ChromeOptions options = new ChromeOptions();
            HashMap<String, Object> chromePrefs = new HashMap<String, Object>();

            // chrome options
            if(YamlUtil.getYamlData(strMapdata + "browserName").equalsIgnoreCase("Chrome")) {
                chromePrefs.put("credentials_enable_service", false);
                chromePrefs.put("profile.password_manager_enabled", false);
                options.setExperimentalOption("prefs", chromePrefs);
                options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                capability.setCapability(ChromeOptions.CAPABILITY, options);
            }
            driver = new RemoteWebDriver(new URL(strRemoteURL), capability);
            webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(Long.parseLong(strTimeoutDuration)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return driver;
    }
}