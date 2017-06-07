package org.polyfill.api.services;

import org.junit.Test;
import org.polyfill.api.interfaces.ConfigLoaderService;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by bvenkataraman on 10/13/16.
 */
public class JsonConfigLoaderServiceTest {

    private final ConfigLoaderService configLoaderService = new JsonConfigLoaderService();
    private final String testFolderName = "config_loader_tests";

    @Test
    public void testInvalidFilePath() throws Exception {
        Map<String, Object> resultantConfigMap = null;
        try {
            resultantConfigMap = configLoaderService.getConfig("config.jsosn");
            fail("Should throw IOException when file path is incorrect");
        } catch (IOException e) {
            assertNull("Should not return anything when there's IOException", resultantConfigMap);
        }
    }

    @Test
    public void testInvalidFileFormat() throws Exception {
        Map<String, Object> resultantConfigMap = null;
        try {
            resultantConfigMap = configLoaderService.getConfig(testFolderName, "json_loader.js");
            fail("Should throw IOException when file type is incorrect");
        } catch (IOException e) {
            assertNull("Should not return anything when there's IOException", resultantConfigMap);
        }
    }

    @Test
    public void testGetConfigWithStringPath() throws Exception {
        Map<String, Object> expectedConfigMap = getSimpleConfig();
        try {
            Map<String, Object> actualConfigMap = configLoaderService.getConfig(testFolderName, "json_loader.json");
            assertTrue("The two config maps do not match", expectedConfigMap.equals(actualConfigMap));
        } catch (IOException e) {
            fail("Should not throw IOException when file exists");
        }
    }

    private Map<String, Object> getSimpleConfig() {
        return new HashMap<String, Object>(){{
            put("one", "First");
            put("two", Arrays.asList("first", "second"));
            put("three", new HashMap<String, Object>(){{
                put("one", "1");
                put("two", "2");
            }});
        }};
    }
}
