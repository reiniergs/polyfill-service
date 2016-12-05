package org.polyfill.components.services;

import org.junit.Before;
import org.junit.Test;
import org.polyfill.services.HashMapUtilService;
import org.polyfill.services.JSONConfigLoaderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by bvenkataraman on 11/21/16.
 */
public class HashMapUtilServiceTest {


    @Autowired
    JSONConfigLoaderService jsonConfigLoaderService;

    @Autowired
    HashMapUtilService hashMapUtilService;

    private String testConfigFilePath, realConfigFilePath;

    @Before
    public void setup() {

        jsonConfigLoaderService = new JSONConfigLoaderService();

        testConfigFilePath = "./src/test/testPolyfill/config.json";

    }

    @Test
    public void testGetInSingleValue() throws Exception {

        String expectedValue = "http://people.mozilla.org/~jorendorff/es6-draft.html#sec-math.acosh";
        HashMap<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(testConfigFilePath);

        assertNotNull("HashMap returned should not be empty", resultantConfigMap);
        assertEquals("Attribute value does not match", expectedValue, hashMapUtilService.getFromMap(resultantConfigMap, "spec"));

    }

    @Test
    public void testGetInFromArrays() throws Exception {

        HashMap<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(testConfigFilePath);
        assertNotNull("HashMap returned should not be empty",  resultantConfigMap);

        assertEquals("Alias config mismatch", "default-2.0", hashMapUtilService.getFromMap(resultantConfigMap, "aliases.1"));
        assertEquals("Dependencies config mismatch", "Symbol.species", hashMapUtilService.getFromMap(resultantConfigMap, "dependencies.3"));
    }

    @Test
    public void testGetInFromMaps() throws Exception {

        String expectedValue = "<12";
        HashMap<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(testConfigFilePath);

        assertNotNull("HashMap returned should not be empty",  resultantConfigMap);
        assertEquals("IE config mismatch", expectedValue, hashMapUtilService.getFromMap(resultantConfigMap, "my.ie"));
    }

    @Test
    public void testGetInFromMixedHierarchy() throws Exception {

        HashMap<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(testConfigFilePath);

        assertNotNull("HashMap returned should not be empty", resultantConfigMap);
        assertEquals("Attribute value does not match", "bonjour", hashMapUtilService.getFromMap(resultantConfigMap, "my.myBrowser.ios_saf.0.hi.french"));
    }

    @Test
    public void testInvalidAttributeFromMixedHierarchy() throws Exception {

        HashMap<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(testConfigFilePath);

        assertNotNull("HashMap returned should not be empty", resultantConfigMap);
        assertNull(hashMapUtilService.getFromMap(resultantConfigMap, "my.myBrowser.ios_saf.7"));

    }

    @Test
    public void testIntermediateValue() throws Exception {

        String expectedValue = "[{hi={english=hello, french=bonjour}}, First one, Second one]";
        HashMap<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(testConfigFilePath);

        assertNotNull("HashMap returned should not be empty", resultantConfigMap);
        assertEquals("Attribute value mismatch", expectedValue, hashMapUtilService.getFromMap(resultantConfigMap, "my.myBrowser.ios_saf").toString());

    }

}
