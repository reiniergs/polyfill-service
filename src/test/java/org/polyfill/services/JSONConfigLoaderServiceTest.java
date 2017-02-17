package org.polyfill.services;

import org.junit.Before;
import org.junit.Test;
import org.polyfill.services.JSONConfigLoaderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by bvenkataraman on 10/13/16.
 */
public class JSONConfigLoaderServiceTest {

    @Autowired
    JSONConfigLoaderService jsonConfigLoaderService;

    @Before
    public void setup() {
        jsonConfigLoaderService = new JSONConfigLoaderService();
    }

    @Test
    public void testInvalidFileFormat() throws Exception {

        Map<String, Object> resultantConfigMap = new HashMap<String, Object>();
        String filePath = "./polyfills/Element/config.jsosn";
        try {
            resultantConfigMap = jsonConfigLoaderService.getConfig(filePath);
        } catch (Exception e) {
            assertEquals("Retrieved config map should be null", 0, resultantConfigMap.size());
            assertEquals(filePath + " (No such file or directory)", e.getMessage());
        }
    }

    @Test
    public void testInvalidFile() throws Exception {

        Map<String, Object> resultantConfigMap = new HashMap<String, Object>();
        String filePath = "./polyfills/Element/detect.json";
        try {
            resultantConfigMap = jsonConfigLoaderService.getConfig(filePath);
        } catch (Exception e) {
            assertEquals("Retrieved config map should be null", 0, resultantConfigMap.size());
            assertEquals(filePath + " (No such file or directory)", e.getMessage());
        }
    }

    @Test
    public void testRetrieveConfigMap() throws Exception {

        String filePath = "./src/test/resources/simpleConfig.json";
        String firstItem = "First";

        List<String> secondItem = new ArrayList<String>();
        secondItem.add("first");
        secondItem.add("second");

        Map<String, Object> thirdItem = new HashMap<String, Object>();
        thirdItem.put("one", "1");
        thirdItem.put("two", "2");

        Map<String, Object> mockConfigMap = new HashMap<String, Object>();
        mockConfigMap.put("one", firstItem);
        mockConfigMap.put("two", secondItem);
        mockConfigMap.put("three", thirdItem);

        Map<String, Object> resultantConfigMap = jsonConfigLoaderService.getConfig(filePath);

        assertNotNull("Map returned should not be empty", resultantConfigMap);
        assertTrue("The two config maps do not match", mockConfigMap.equals(resultantConfigMap));
    }
}
