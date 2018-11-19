//Env vars to set: 
//CBT_USERNAME
//CBT_AUTHKEY
//APPLITOOLS_KEY

//Properties to set in command:
//APP_NAME
//TEST_NAME

package com.cbt;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.Parameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;

import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.BatchInfo;

public class SingleTest {
    public RemoteWebDriver driver;
    private static String APP_NAME = System.getProperty("APP_NAME");
    private static String TEST_NAME = System.getProperty("TEST_NAME");

    @BeforeMethod(alwaysRun=true)
    @org.testng.annotations.Parameters(value={"config", "environment"})
    public void setUp(String config_file, String environment) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader("src/test/resources/conf/" + config_file));
        JSONObject envs = (JSONObject) config.get("environments");

        DesiredCapabilities capabilities = new DesiredCapabilities();

        Map<String, String> envCapabilities = (Map<String, String>) envs.get(environment);
        Iterator it = envCapabilities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
        }
        
        Map<String, String> commonCapabilities = (Map<String, String>) config.get("capabilities");
        it = commonCapabilities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(capabilities.getCapability(pair.getKey().toString()) == null){
                capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
            }
        }

        //String username = ((String) config.get("user")).replace("@", "%40");
        //String accessKey = (String) config.get("key");

        String username = ((String) System.getenv("CBT_USERNAME")).replace("@", "%40");
        String accessKey = (String) System.getenv("CBT_AUTHKEY");

        URL test_a = new URL("http://"+username+":"+accessKey+"@"+config.get("server")+":80/wd/hub");

        driver = new RemoteWebDriver(test_a, capabilities);
        System.out.println("Session ID: " + driver.getSessionId().toString() + " started.");
    }

    @Test
    public void test() throws Exception {
        Eyes eyes = new Eyes();
        try {
            BatchInfo mybatch = new BatchInfo(System.getenv("APPLITOOLS_BATCH_NAME"));
            mybatch.setId (System.getenv("APPLITOOLS_BATCH_ID"));
            eyes.setBatch(mybatch);

            eyes.setApiKey(System.getenv("APPLITOOLS_KEY"));
            eyes.setForceFullPageScreenshot(true);

            eyes.open(driver, APP_NAME, TEST_NAME, new RectangleSize(800,600));
            

            driver.get("https://www.crossbrowsertesting.com");
            eyes.checkWindow("CBT-Homepage"); //void return

            driver.get("https://www.google.com/imghp?hl=en&tab=wi&authuser=0");
            eyes.checkWindow("Google-Homepage");


            eyes.close(false);
        } catch (Exception ex) {
            System.out.println("This is the Exception Error: ");
            System.out.println(ex.getMessage());

        } finally {
            System.out.println("Fully Stopping Test");
            eyes.abortIfNotClosed();
        }
    }

    @AfterMethod(alwaysRun=true)
    public void tearDown() throws Exception {
        String sessionId = driver.getSessionId().toString();
        driver.quit();
        System.out.println("Session ID: " + sessionId + " finished.");
    }
}
