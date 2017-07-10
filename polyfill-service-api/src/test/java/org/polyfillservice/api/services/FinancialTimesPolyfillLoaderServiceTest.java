package org.polyfillservice.api.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.polyfillservice.api.components.Polyfill;
import org.polyfillservice.api.components.PolyfillLocationString;
import org.polyfillservice.api.configurations.CustomPolyfillsConfig;
import org.polyfillservice.api.configurations.PolyfillsConfig;
import org.polyfillservice.api.interfaces.PolyfillLocation;
import org.polyfillservice.api.utils.TestingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by smo on 2/26/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader=AnnotationConfigContextLoader.class,
        classes={PolyfillsConfig.class,
                CustomPolyfillsConfig.class,
                JsonConfigLoaderService.class,
                FinancialTimesPolyfillLoaderService.class
        })
public class FinancialTimesPolyfillLoaderServiceTest {

    @Autowired
    private FinancialTimesPolyfillLoaderService service;

    @Resource(name = "polyfills")
    Map<String, Polyfill> defaultPolyfills;

    @Resource(name = "customPolyfills")
    Map<String, Polyfill> customPolyfills;

    PolyfillLocation defaultLocation = new PolyfillLocationString("polyfills");
    PolyfillLocation customLocation = new PolyfillLocationString("polyfills_custom");

    @Test
    public void testNumberOfPolyfillsLoadedFromSingleLocation() {
        List<PolyfillLocation> polyfillLocationList = Arrays.asList(defaultLocation);
        Map<String, Polyfill> expectedPolyfills = getDefaultExpectedPolyfills();
        Map<String, Polyfill> actualPolyfills = service.loadPolyfills(polyfillLocationList);
        assertEquals("Number of expectedPolyfills loaded is incorrect",
            expectedPolyfills.size(), actualPolyfills.size());
    }

    @Test
    public void testPolyfillsLoadedFromSingleLocation() {
        List<PolyfillLocation> polyfillLocationList = Arrays.asList(defaultLocation);
        Map<String, Polyfill> expectedPolyfills = getDefaultExpectedPolyfills();
        Map<String, Polyfill> actualPolyfills = service.loadPolyfills(polyfillLocationList);
        TestingUtil.assertPolyfillsEqual("Loaded polyfills are incorrect",
                expectedPolyfills, actualPolyfills);
    }

    @Test
    public void testNumberOfPolyfillsLoadedFromMultipleLocation() {
        List<PolyfillLocation> polyfillLocationList = Arrays.asList(customLocation, defaultLocation);
        Map<String, Polyfill> expectedPolyfills = getExpectedPolyfillsFromMultipleLocations();
        Map<String, Polyfill> actualPolyfills = service.loadPolyfills(polyfillLocationList);
        assertEquals("Number of expectedPolyfills loaded is incorrect",
                expectedPolyfills.size(), actualPolyfills.size());
    }

    @Test
    public void testPolyfillsLoadedFromMultipleLocation() {
        List<PolyfillLocation> polyfillLocationList = Arrays.asList(customLocation, defaultLocation);
        Map<String, Polyfill> expectedPolyfills = getExpectedPolyfillsFromMultipleLocations();
        Map<String, Polyfill> actualPolyfills = service.loadPolyfills(polyfillLocationList);
        TestingUtil.assertPolyfillsEqual("Loaded polyfills are incorrect",
                expectedPolyfills, actualPolyfills);
    }

    @Test
    public void testLoadingPolyfillsFromAWrongPath() {
        PolyfillLocation wrongLocation = new PolyfillLocationString("wrong/path");
        List<PolyfillLocation> polyfillLocations = Arrays.asList(wrongLocation);
        Map<String, Polyfill> actualPolyfills = service.loadPolyfills(polyfillLocations);
        assertTrue("Empty map is created when path is incorrect.", actualPolyfills.isEmpty());
    }

    Map<String, Polyfill> getDefaultExpectedPolyfills() {
        return defaultPolyfills;
    }

    Map<String, Polyfill> getExpectedPolyfillsFromMultipleLocations() {
        Map<String, Polyfill> polyfills = new HashMap<>();
        polyfills.putAll(defaultPolyfills);
        polyfills.putAll(customPolyfills);
        return polyfills;
    }
}
