package org.polyfill.services;

import org.junit.Before;
import org.junit.Test;
import org.polyfill.services.JSONConfigLoaderService;
import org.polyfill.services.MapUtilService;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by bvenkataraman on 11/21/16.
 */
public class MapUtilServiceTest {


    private JSONConfigLoaderService jsonConfigLoaderService;
    private MapUtilService mapUtilService;
    private String testConfigFilePath, realConfigFilePath;

    @Before
    public void setup() {
        jsonConfigLoaderService = new JSONConfigLoaderService();
        mapUtilService = new MapUtilService();
        testConfigFilePath = "./src/test/testPolyfill/config.json";
    }

    @Test
    public void testGetInSingleValue() throws Exception {

        String expectedValue = "http://people.mozilla.org/~jorendorff/es6-draft.html#sec-math.acosh";
        Map<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(testConfigFilePath);

        assertNotNull("Map returned should not be empty", resultantConfigMap);
        assertEquals("Attribute value does not match", expectedValue, mapUtilService.getFromMap(resultantConfigMap, "spec"));
    }

    @Test
    public void testGetInFromArrays() throws Exception {

        Map<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(testConfigFilePath);
        assertNotNull("Map returned should not be empty",  resultantConfigMap);

        assertEquals("Alias config mismatch", "default-2.0", mapUtilService.getFromMap(resultantConfigMap, "aliases.1"));
        assertEquals("Dependencies config mismatch", "Symbol.species", mapUtilService.getFromMap(resultantConfigMap, "dependencies.3"));
    }

    @Test
    public void testGetInFromMaps() throws Exception {

        String expectedValue = "<12";
        Map<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(testConfigFilePath);

        assertNotNull("Map returned should not be empty",  resultantConfigMap);
        assertEquals("IE config mismatch", expectedValue, mapUtilService.getFromMap(resultantConfigMap, "my.ie"));
    }

    @Test
    public void testGetInFromMixedHierarchy() throws Exception {

        Map<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(testConfigFilePath);

        assertNotNull("Map returned should not be empty", resultantConfigMap);
        assertEquals("Attribute value does not match", "bonjour", mapUtilService.getFromMap(resultantConfigMap, "my.myBrowser.ios_saf.0.hi.french"));
    }

    @Test
    public void testInvalidAttributeFromMixedHierarchy() throws Exception {

        Map<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(testConfigFilePath);

        assertNotNull("Map returned should not be empty", resultantConfigMap);
        assertNull(mapUtilService.getFromMap(resultantConfigMap, "my.myBrowser.ios_saf.7"));
    }

    @Test
    public void testIntermediateValue() throws Exception {

        String expectedValue = "[{hi={english=hello, french=bonjour}}, First one, Second one]";
        Map<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(testConfigFilePath);

        assertNotNull("Map returned should not be empty", resultantConfigMap);
        assertEquals("Attribute value mismatch", expectedValue, mapUtilService.getFromMap(resultantConfigMap, "my.myBrowser.ios_saf").toString());
    }
}
