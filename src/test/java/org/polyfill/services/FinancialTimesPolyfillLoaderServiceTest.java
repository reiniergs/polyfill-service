package org.polyfill.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.polyfill.components.Polyfill;
import org.polyfill.interfaces.ConfigLoaderService;
import org.polyfill.utils.UnitTestingUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by smo on 2/26/17.
 */
public class FinancialTimesPolyfillLoaderServiceTest {

    @InjectMocks
    private FinancialTimesPolyfillLoaderService polyfillLoader;
    @Spy
    private ConfigLoaderService configLoaderService = new JSONConfigLoaderService();

    private Path testPolyfillsPath;
    private Map<String, Polyfill> expectedPolyfills;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);

        testPolyfillsPath = UnitTestingUtil.getResourcesPath().resolve("loadPolyfillsTest");

        Polyfill polyfillA = new Polyfill.Builder("a")
                .aliases(Arrays.asList("default", "foo"))
                .browserRequirements(new HashMap<String, String>(){{
                    put("chrome", "*");
                    put("firefox", "4 - 31");
                }})
                .dependencies(Arrays.asList("b", "d"))
                .detectSource("a.detectSource")
                .minSource("a.min.js")
                .rawSource("a.raw.js")
                .build();

        Polyfill polyfillD = new Polyfill.Builder("d")
                .aliases(Arrays.asList("es6"))
                .browserRequirements(new HashMap<String, String>(){{
                    put("chrome", "*");
                    put("firefox", "31");
                }})
                .dependencies(Arrays.asList("b", "c"))
                .detectSource("d.detectSource")
                .build();

        expectedPolyfills = new HashMap<String, Polyfill>() {{
            put("a", polyfillA);
            put("d", polyfillD);
        }};
    }

    @Test
    public void testLoadSinglePolyfill() {
        try {
            String polyfillPath = testPolyfillsPath.resolve("a").toString();
            Polyfill actualPolyfill = polyfillLoader.loadPolyfill(polyfillPath);
            Polyfill expectedPolyfill = expectedPolyfills.get("a");
            assertEquals("Polyfill fields are different", expectedPolyfill.toString(), actualPolyfill.toString());
        } catch(IOException e) {
            fail("Loading a polyfill with correct directory path should not throw IOException");
        }
    }

    @Test
    public void testLoadSinglePolyfillWithNoMinRaw() {
        try {
            String polyfillPath = testPolyfillsPath.resolve("d").toString();
            Polyfill actualPolyfill = polyfillLoader.loadPolyfill(polyfillPath);
            Polyfill expectedPolyfill = expectedPolyfills.get("d");
            assertEquals("Polyfill fields are different", expectedPolyfill.toString(), actualPolyfill.toString());
        } catch(IOException e) {
            fail("Loading a polyfill with correct directory path should not throw IOException");
        }
    }

    @Test
    public void testSizeWhenLoadMultiplePolyfills() {
        try {
            Map<String, Polyfill> actualPolyfills = polyfillLoader.loadPolyfillsToMap(testPolyfillsPath.toString());
            assertEquals("Number of expectedPolyfills loaded is incorrect", 2, actualPolyfills.size());
        } catch(IOException e) {
            fail("Loading polyfills with correct directory path should not throw IOException");
        }
    }

    @Test
    public void testPolyfillsEqualWhenLoadMultiplePolyfills() {
        try {
            Map<String, Polyfill> actualPolyfills = polyfillLoader.loadPolyfillsToMap(testPolyfillsPath.toString());
            assertEquals("Loaded polyfills are incorrect", expectedPolyfills.toString(), actualPolyfills.toString());
        } catch(IOException e) {
            fail("Loading polyfills with correct path should not throw IOException");
        }
    }

    @Test
    public void testLoadMultiplePolyfillsFromWrongPath() {
        Map<String, Polyfill> actualPolyfills = null;
        try {
            actualPolyfills = polyfillLoader.loadPolyfillsToMap("wrong/path");
            fail("Loading polyfills from the wrong path should throw IOException");
        } catch(IOException e) {
            assertNull("Should return null when path doesn't exist", actualPolyfills);
        }
    }

    @Test
    public void testLoadSinglePolyfillFromWrongPath() {
        Polyfill actualPolyfill = null;
        try {
            actualPolyfill = polyfillLoader.loadPolyfill("wrong/path");
            fail("Loading polyfills from the wrong path should throw IOException");
        } catch(IOException e) {
            assertNull("Should return null when path doesn't exist", actualPolyfill);
        }
    }
}
