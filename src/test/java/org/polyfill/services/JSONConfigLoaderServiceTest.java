package org.polyfill.services;

import org.junit.Test;
import org.polyfill.utils.UnitTestingUtil;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by bvenkataraman on 10/13/16.
 */
public class JSONConfigLoaderServiceTest {

    JSONConfigLoaderService jsonConfigLoaderService = new JSONConfigLoaderService();

    @Test
    public void testInvalidFilePath() throws Exception {
        Map<String, Object> resultantConfigMap = null;
        String filePath = UnitTestingUtil.getResourcesPath().resolve("config.jsosn").toString();
        try {
            resultantConfigMap = jsonConfigLoaderService.getConfig(filePath);
            fail("Should throw IOException when file path is incorrect");
        } catch (IOException e) {
            assertNull("Should not return anything when there's IOException", resultantConfigMap);
        }
    }

    @Test
    public void testInvalidFileFormat() throws Exception {
        Map<String, Object> resultantConfigMap = null;
        String filePath = UnitTestingUtil.getPolyfillsPath().resolve(Paths.get("a", "min.js")).toString();
        try {
            resultantConfigMap = jsonConfigLoaderService.getConfig(filePath);
            fail("Should throw IOException when file type is incorrect");
        } catch (IOException e) {
            assertNull("Should not return anything when there's IOException", resultantConfigMap);
        }
    }

    @Test
    public void testGetConfigWithStringPath() throws Exception {
        String filePath = UnitTestingUtil.getResourcesPath().resolve("simpleConfig.json").toString();
        Map<String, Object> expectedConfigMap = getSimpleConfig();
        try {
            Map<String, Object> actualConfigMap = jsonConfigLoaderService.getConfig(filePath);
            assertTrue("The two config maps do not match", expectedConfigMap.equals(actualConfigMap));
        } catch (IOException e) {
            fail("Should not throw IOException when file exists");
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
