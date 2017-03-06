package org.polyfill.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.polyfill.configurations.TestConfigs;
import org.polyfill.components.Polyfill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by smo on 2/26/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader=AnnotationConfigContextLoader.class,
        classes={TestConfigs.class,
                JSONConfigLoaderService.class,
                DirectoryBasedPolyfillLoaderService.class})
public class DirectoryBasedPolyfillLoaderServiceTest {

    @Resource(name = "testRootPath")
    private String testRootPath;

    private String testPolyfillsPath;

    @Autowired
    private DirectoryBasedPolyfillLoaderService polyfillLoaderService;

    private Map<String, Polyfill> expectedPolyfills;

    @Before
    public void setup() {
        testPolyfillsPath = testRootPath + "loadPolyfillsTest/";

        Polyfill polyfillA = new Polyfill("a", new HashMap<String, Object>(){{
            put("aliases", Arrays.asList("default", "foo"));
            put("browsers", new HashMap<String, Object>(){{
                put("chrome", "*");
                put("firefox", "4 - 31");
            }});
            put("dependencies", Arrays.asList("b", "d"));
            put("detectSource", "a.detectSource");
            put("minSource", "a.min.js");
            put("rawSource", "a.raw.js");
            put("baseDir", "a");
        }});

        Polyfill polyfillD = new Polyfill("d", new HashMap<String, Object>(){{
            put("aliases", Arrays.asList("es6"));
            put("browsers", new HashMap<String, Object>(){{
                put("chrome", "*");
                put("firefox", "31");
            }});
            put("dependencies", Arrays.asList("b", "c"));
            put("detectSource", "d.detectSource");
            put("minSource", "");
            put("rawSource", "");
            put("baseDir", "d");
        }});

        expectedPolyfills = new HashMap<>();
        expectedPolyfills.put("a", polyfillA);
        expectedPolyfills.put("d", polyfillD);
    }

    @Test
    public void testLoadSinglePolyfill() {
        try {
            Polyfill actualPolyfill = polyfillLoaderService.loadPolyfill(testPolyfillsPath + "a");
            assertEquals("Polyfill fields are different", expectedPolyfills.get("a"), actualPolyfill);
        } catch(IOException e) {
            fail("Loading a polyfill with correct directory path should not throw IOException");
        }
    }

    @Test
    public void testLoadSinglePolyfillWithNoMinRaw() {
        try {
            Polyfill actualPolyfill = polyfillLoaderService.loadPolyfill(testPolyfillsPath + "d");
            assertEquals("Polyfill fields are different", expectedPolyfills.get("d"), actualPolyfill);
        } catch(IOException e) {
            fail("Loading a polyfill with correct directory path should not throw IOException");
        }
    }

    @Test
    public void testSizeWhenLoadMultiplePolyfills() {
        try {
            Map<String, Polyfill> actualPolyfills = polyfillLoaderService.loadPolyfillsToMap(testPolyfillsPath);
            assertEquals("Number of expectedPolyfills loaded is incorrect", 2, actualPolyfills.size());
        } catch(IOException e) {
            fail("Loading polyfills with correct directory path should not throw IOException");
        }
    }

    @Test
    public void testPolyfillsEqualWhenLoadMultiplePolyfills() {
        try {
            Map<String, Polyfill> actualPolyfills = polyfillLoaderService.loadPolyfillsToMap(testPolyfillsPath);
            assertEquals("Loaded polyfills are incorrect", expectedPolyfills, actualPolyfills);
        } catch(IOException e) {
            fail("Loading polyfills with correct directory path should not throw IOException");
        }
    }

    @Test
    public void testLoadMultiplePolyfillsFromWrongPath() {
        Map<String, Polyfill> actualPolyfills = null;
        try {
            actualPolyfills = polyfillLoaderService.loadPolyfillsToMap("wrong/path");
            fail("Loading polyfills from the wrong path should throw IOException");
        } catch(IOException e) {
            assertNull("IOException should cause polyfillLoaderService unable to return anything",
                    actualPolyfills);
        }
    }

    @Test
    public void testLoadSinglePolyfillFromWrongPath() {
        Polyfill actualPolyfill = null;
        try {
            actualPolyfill = polyfillLoaderService.loadPolyfill("wrong/path");
            fail("Loading polyfills from the wrong path should throw IOException");
        } catch(IOException e) {
            assertNull("IOException should cause polyfillLoaderService unable to return anything",
                    actualPolyfill);
        }
    }
}
