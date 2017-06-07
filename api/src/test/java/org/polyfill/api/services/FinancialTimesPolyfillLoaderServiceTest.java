package org.polyfill.api.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.polyfill.api.components.Polyfill;
import org.polyfill.api.components.ServiceConfig;
import org.polyfill.api.configurations.MockPolyfillsConfig;
import org.polyfill.api.interfaces.ConfigLoaderService;

import java.io.IOException;
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
    @Spy
    private ServiceConfig serviceConfig = new ServiceConfig();

    private MockPolyfillsConfig polyfillsConfig = new MockPolyfillsConfig();

    private String testPolyfillsPath = "polyfills";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSizeWhenLoadMultiplePolyfills() {
        try {
            Map<String, Polyfill> actualPolyfills = polyfillLoader.loadPolyfills(testPolyfillsPath);
            assertEquals("Number of expectedPolyfills loaded is incorrect", 5, actualPolyfills.size());
        } catch(IOException e) {
            fail("Loading polyfills with correct directory path should not throw IOException");
        }
    }

    @Test
    public void testPolyfillsEqualWhenLoadMultiplePolyfills() {
        try {
            Map<String, Polyfill> expectedPolyfills = polyfillsConfig.polyfills();
            Map<String, Polyfill> actualPolyfills = polyfillLoader.loadPolyfills(testPolyfillsPath);
            assertEquals("Loaded polyfills are incorrect", expectedPolyfills.toString(), actualPolyfills.toString());
        } catch(IOException e) {
            fail("Loading polyfills with correct path should not throw IOException");
        }
    }

    @Test
    public void testLoadMultiplePolyfillsFromWrongPath() {
        Map<String, Polyfill> actualPolyfills = null;
        try {
            actualPolyfills = polyfillLoader.loadPolyfills("wrong/path");
            fail("Loading polyfills from the wrong path should throw IOException");
        } catch(IOException e) {
            assertNull("Should return null when path doesn't exist", actualPolyfills);
        }
    }
}
