package org.polyfill.components.services;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.polyfill.services.*;

import java.util.HashMap;

import static org.junit.Assert.*;


/**
 * Created by bvenkataraman on 10/13/16.
 */
public class JSONConfigObjectLoaderServiceTest {


    @Autowired
    JSONConfigLoaderService jsonConfigLoaderService;

    @Before
    public void setup() {

        jsonConfigLoaderService = new JSONConfigLoaderService();
    }

    @Test
    public void testInvalidFileFormat() throws Exception {

        String filePath = "./polyfills/Element/config.jsosn";
        try {
            HashMap<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(filePath);
        }
        catch (Exception e) {
            assertEquals(filePath + " (No such file or directory)", e.getMessage());
        }

    }

    @Test
    public void testInvalidFile() throws Exception {

        String filePath = "./polyfills/Element/detect.json";
        try {
            HashMap<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(filePath);
        }
        catch (Exception e) {
            assertEquals("Not a config file", e.getMessage());
        }

    }

    @Test
    public void testRetrieveConfigMap() throws Exception {

            String filePath = "./polyfills/Element/config.json";
            HashMap<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(filePath);
            assertNotNull("HashMap returned should not be empty", resultantConfigMap);
    }

    @Test
    public void testGetInConfigArrays() throws Exception {

        String filePath = "./polyfills/Map/config.json";
        HashMap<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(filePath);
        assertNotNull("HashMap returned should not be empty",  resultantConfigMap);

        assertEquals("Alias config mismatch", "default", jsonConfigLoaderService.getInConfig(resultantConfigMap, "aliases.1"));
        assertEquals("Dependencies config mismatch", "Symbol.species", jsonConfigLoaderService.getInConfig(resultantConfigMap, "dependencies.3"));
    }

    @Test
    public void testGetInConfigMaps() throws Exception {

        String filePath = "./polyfills/Map/config.json";
        String expectedValue = "<12";
        HashMap<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(filePath);
        assertNotNull("HashMap returned should not be empty",  resultantConfigMap);

        assertEquals("IE config mismatch", expectedValue, jsonConfigLoaderService.getInConfig(resultantConfigMap, "browsers.ie"));
    }

    @Test
    public void testGetInConfigSingleValue() throws Exception {

        String filePath = "./polyfills/Math/acosh/config.json";
        String expectedValue = "http://people.mozilla.org/~jorendorff/es6-draft.html#sec-math.acosh";
        HashMap<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(filePath);
        assertNotNull("HashMap returned should not be empty", resultantConfigMap);

        assertEquals("Attribute value does not match", expectedValue, jsonConfigLoaderService.getInConfig(resultantConfigMap, "spec"));

    }


    @Test
    public void testGetInConfigMultipleLayers() throws Exception {

        String filePath = "./src/test/testPolyfill/config.json";
        HashMap<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(filePath);
        assertNotNull("HashMap returned should not be empty", resultantConfigMap);

        assertEquals("Attribute value does not match", "bonjour", jsonConfigLoaderService.getInConfig(resultantConfigMap, "my.myBrowser.ios_saf.0.hi.french"));
    }

    @Test
    public void testInvalidAttributeMultipleLayer() throws Exception {

        String filePath = "./src/test/testPolyfill/config.json";
        HashMap<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(filePath);
        assertNotNull("HashMap returned should not be empty", resultantConfigMap);

        assertNull(jsonConfigLoaderService.getInConfig(resultantConfigMap, "my.myBrowser.ios_saf.7"));

    }

    @Test
    public void testIntermediateValue() throws Exception {

        String filePath = "./src/test/testPolyfill/config.json";
        String expectedValue = "[{hi={english=hello, french=bonjour}}, First one, Second one]";
        HashMap<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(filePath);
        assertNotNull("HashMap returned should not be empty", resultantConfigMap);

        assertEquals("Attribute value mismatch", expectedValue, jsonConfigLoaderService.getInConfig(resultantConfigMap, "my.myBrowser.ios_saf").toString());

    }


}
