package org.polyfill.services;

import org.junit.Before;
import org.junit.Test;
import org.polyfill.services.JSONConfigLoaderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by bvenkataraman on 10/13/16.
 */
public class JSONConfigLoaderServiceTest {

    private static final String TEST_RESOURCES = "./src/test/resources/";

    @Autowired
    JSONConfigLoaderService jsonConfigLoaderService;

    @Before
    public void setup() {
        jsonConfigLoaderService = new JSONConfigLoaderService();
    }

    @Test
    public void testInvalidFilePath() {
        Map<String, Object> resultantConfigMap = null;
        String filePath = TEST_RESOURCES + "config.jsosn";
        try {
            resultantConfigMap = jsonConfigLoaderService.getConfig(filePath);
            fail("JSONConfigLoaderService::getConfig should throw IOException when file path is incorrect");
        } catch (IOException e) {
            assertNull("JSONConfigLoaderService::getConfig should not return anything when there's IOException",
                    resultantConfigMap);
        }
    }

    @Test
    public void testInvalidFileFormat() {
        Map<String, Object> resultantConfigMap = null;
        String filePath = TEST_RESOURCES + "polyfills/a/min.js";
        try {
            resultantConfigMap = jsonConfigLoaderService.getConfig(filePath);
            fail("JSONConfigLoaderService::getConfig should throw IOException when file type is incorrect");
        } catch (IOException e) {
            assertNull("JSONConfigLoaderService::getConfig should not return anything when there's IOException",
                    resultantConfigMap);
        }
    }

    @Test
    public void testValidFile() {
        String filePath = TEST_RESOURCES + "simpleConfig.json";
        Map<String, Object> expectedConfigMap = getSimpleConfig();
        try {
            Map<String, Object> actualConfigMap = jsonConfigLoaderService.getConfig(filePath);
            assertTrue("The two config maps do not match", expectedConfigMap.equals(actualConfigMap));
        } catch (IOException e) {
            fail("JSONConfigLoaderService::getConfig should not throw IOException when file exists");
        }
    }

    private Map<String, Object> getSimpleConfig() {
        String firstItem = "First";

        List<String> secondItem = Arrays.asList("first", "second");

        Map<String, Object> thirdItem = new HashMap<>();
        thirdItem.put("one", "1");
        thirdItem.put("two", "2");

        Map<String, Object> simpleConfigMap = new HashMap<>();
        simpleConfigMap.put("one", firstItem);
        simpleConfigMap.put("two", secondItem);
        simpleConfigMap.put("three", thirdItem);

        return simpleConfigMap;
    }
}
