package org.polyfill.services;

import org.junit.Test;
import org.polyfill.interfaces.ConfigLoaderService;

import java.io.IOException;
import java.util.*;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests for default config loader interface methods
 */
public class ConfigLoaderServiceTest {

    private class ConfigLoaderServiceStub implements ConfigLoaderService {
        public List<Map<String, Object>> testConfigs = new ArrayList<>();
        public ConfigLoaderServiceStub() {
            Map<String, Object> nestedconfig = new HashMap<>();
            nestedconfig.put("filename", "config.json");
            nestedconfig.put("testfield1", "1");
            nestedconfig.put("testfield2", "2");
            this.testConfigs.add(nestedconfig);

            Map<String, Object> nested2config = new HashMap<>();
            nested2config.put("filename", "nested/config.json");
            nested2config.put("testfield1", "3");
            nested2config.put("testfield2", "4");
            this.testConfigs.add(nested2config);
        }
        public List<Map<String, Object>> getTestConfigs() {
            return this.testConfigs;
        }
        @Override
        public Map<String, Object> getConfig(String path) throws IOException {
            if (path.equals(TEST_DIRECTORY_PATH + "config.json")) {
                return this.testConfigs.get(0);
            } else if (path.equals(TEST_DIRECTORY_PATH + "nested/config.json")) {
                return this.testConfigs.get(1);
            }
            return null;
        }
        @Override
        public boolean isConfig(String fileName) {
            return fileName.endsWith(".json");
        }
    }

    private ConfigLoaderService configLoaderService = new ConfigLoaderServiceStub();
    private static final String TEST_DIRECTORY_PATH = "./src/test/testPolyfill/configLoader/";

    @Test
    public void testGetAllConfigPathsNonRecursiveNoExtraTest() throws Exception {
        List<String> expectedPaths = new ArrayList<>();
        expectedPaths.add(TEST_DIRECTORY_PATH + "config.json");
        expectedPaths.add(TEST_DIRECTORY_PATH + "randomConfig.json");

        List<String> actualPaths = configLoaderService.getAllConfigPaths(
                TEST_DIRECTORY_PATH, false, null);

        assertListEquals(expectedPaths, actualPaths);
    }

    @Test
    public void testGetAllConfigPathsRecursiveNoExtraTest() throws Exception {
        List<String> expectedPaths = new ArrayList<>();
        expectedPaths.add(TEST_DIRECTORY_PATH + "config.json");
        expectedPaths.add(TEST_DIRECTORY_PATH + "randomConfig.json");
        expectedPaths.add(TEST_DIRECTORY_PATH + "nested/config.json");

        List<String> actualPaths = configLoaderService.getAllConfigPaths(
                TEST_DIRECTORY_PATH, true, null);

        assertListEquals(expectedPaths, actualPaths);
    }

    @Test
    public void testGetAllConfigPathsRecursiveWithTest() throws Exception {
        List<String> expectedPaths = new ArrayList<>();
        expectedPaths.add(TEST_DIRECTORY_PATH + "config.json");
        expectedPaths.add(TEST_DIRECTORY_PATH + "nested/config.json");

        List<String> actualPaths = configLoaderService.getAllConfigPaths(
                TEST_DIRECTORY_PATH, true,
                path -> path.getFileName().toString().equals("config.json"));

        assertListEquals(expectedPaths, actualPaths);
    }

    @Test
    public void testGetConfigsFromDirectoryCallGetConfig() throws Exception {
        Map<String, Object> config = new HashMap<>();
        config.put("filename", "config.json");
        config.put("testfield1", "1");
        config.put("testfield2", "2");

        Map<String, Object> nestedConfig = new HashMap<>();
        nestedConfig.put("filename", "nested/config.json");
        nestedConfig.put("testfield1", "3");
        nestedConfig.put("testfield2", "4");

        List<Map<String, Object>> expectedConfigs = new ArrayList<>();
        expectedConfigs.add(config);
        expectedConfigs.add(nestedConfig);

        List<Map<String, Object>> actualConfigs = configLoaderService.getConfigsFromDirectory(
                TEST_DIRECTORY_PATH, true,
                path -> path.getFileName().toString().equals("config.json"));

        assertListEquals(expectedConfigs, actualConfigs);
    }

    private void assertListEquals(List expectedList, List actualList) throws Exception {
        Set expectedListSet = new HashSet<>(expectedList);
        Set actualListSet = new HashSet<>(actualList);

        assertEquals("Number of elements should be " + expectedList.size(),
                expectedList.size(), actualList.size());

        assertEquals("Lists do not match. Expected: " + expectedList.toString(),
                expectedListSet, actualListSet);
    }
}
