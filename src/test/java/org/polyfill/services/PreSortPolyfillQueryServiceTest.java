package org.polyfill.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.polyfill.components.Feature;
import org.polyfill.components.Filters;
import org.polyfill.components.UserAgentImpl;
import org.polyfill.configurations.MockPolyfillsConfig;
import org.polyfill.interfaces.PolyfillQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader=AnnotationConfigContextLoader.class,
        classes={MockPolyfillsConfig.class,
                SemVerUtilService.class,
                PreSortPolyfillQueryService.class
        })
public class PreSortPolyfillQueryServiceTest {

    @Autowired
    private PolyfillQueryService polyfillQueryService;

    @Test
    public void testSearchByUserAgentMeetVersionRequirements() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Filters filters = new Filters.Builder()
                .userAgent(new UserAgentImpl("chrome", "30"))
                .build();
        String actualPolyfillsString = getPolyfillsRawSources(features, filters);
        String expectedPolyfillsString = getMockRawSources("c", "b", "d", "a");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchByUserAgentSomeNotMeetVersionRequirement() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Filters filters = new Filters.Builder()
                .userAgent(new UserAgentImpl("firefox", "5"))
                .build();
        String actualPolyfillsString = getPolyfillsRawSources(features, filters);
        String expectedPolyfillsString = getMockRawSources("c", "a");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testUnknownUserAgentShouldReturnEmpty() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Filters filters = new Filters.Builder()
                .userAgent(new UserAgentImpl("firefox", "1"))
                .build();
        String actualPolyfillsString = getPolyfillsRawSources(features, filters);
        String expectedPolyfillsString = "";
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testFeaturesAlwaysLoaded() {
        List<Feature> features = Arrays.asList(new Feature("default|always"));
        Filters filters = new Filters.Builder()
                .userAgent(new UserAgentImpl("firefox", "5"))
                .build();
        String actualPolyfillsString = getPolyfillsRawSources(features, filters);
        String expectedPolyfillsString = getMockRawSources("c", "b", "d", "a");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testUserAgentNotMeetBaselineNullifyAlwaysFlag() {
        List<Feature> features = Arrays.asList(new Feature("default|always"));
        Filters filters = new Filters.Builder()
                .userAgent(new UserAgentImpl("firefox", "1"))
                .build();
        String actualPolyfillsString = getPolyfillsRawSources(features, filters);
        String expectedPolyfillsString = "";
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testNoFeaturesShouldReturnEmpty() {
        List<Feature> features = Collections.emptyList();
        Filters emptyFilters = new Filters.Builder().build();
        String actualPolyfillsString = getPolyfillsRawSources(features, emptyFilters);
        String expectedPolyfillsString = "";
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchBySingleFeature() {
        List<Feature> features = Arrays.asList(new Feature("c"));
        Filters emptyFilters = new Filters.Builder().build();
        String actualPolyfillsString = getPolyfillsRawSources(features, emptyFilters);
        String expectedPolyfillsString = getMockRawSources("c");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchByMultipleFeatures() {
        List<Feature> features = Arrays.asList(new Feature("c"), new Feature("e"));
        Filters emptyFilters = new Filters.Builder().build();
        String actualPolyfillsString = getPolyfillsRawSources(features, emptyFilters);
        String expectedPolyfillsString = getMockRawSources("c", "e");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchBySingleAlias() {
        List<Feature> features = Arrays.asList(new Feature("foo"));
        Filters emptyFilters = new Filters.Builder().build();
        String actualPolyfillsString = getPolyfillsRawSources(features, emptyFilters);
        String expectedPolyfillsString = getMockRawSources("c", "e");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchByMultipleAliases() {
        List<Feature> features = Arrays.asList(new Feature("default"), new Feature("foo"));
        Filters emptyFilters = new Filters.Builder().build();
        String actualPolyfillsString = getPolyfillsRawSources(features, emptyFilters);
        String expectedPolyfillsString = getMockRawSources("c", "e", "b", "d", "a");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchByMixingAliasAndFeature() {
        List<Feature> features = Arrays.asList(new Feature("default"), new Feature("e"));
        Filters emptyFilters = new Filters.Builder().build();
        String actualPolyfillsString = getPolyfillsRawSources(features, emptyFilters);
        String expectedPolyfillsString = getMockRawSources("c", "e", "b", "d", "a");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testExcludesFeatures() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Filters filters = new Filters.Builder()
                .excludeFeatures("c", "b")
                .build();
        String actualPolyfillsString = getPolyfillsRawSources(features, filters);
        String expectedPolyfillsString = getMockRawSources("d", "a");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testCannotExcludeAlias() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Filters filters = new Filters.Builder()
                .excludeFeatures("es6")
                .build();
        String actualPolyfillsString = getPolyfillsRawSources(features, filters);
        String expectedPolyfillsString = getMockRawSources("c", "b", "d", "a");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testDoMinify() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Filters filters = new Filters.Builder().build();
        String actualPolyfillsString = getPolyfillsMinSources(features, filters);
        String expectedPolyfillsString = getMockMinSources("c", "b", "d", "a");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testloadOnUnknownUA() {
        List<Feature> features = Arrays.asList(new Feature("c"));
        Filters filters = new Filters.Builder()
                .userAgent(new UserAgentImpl("unknown", "0.0.0"))
                .loadOnUnknown(true)
                .build();

        String actualPolyfillsString = getPolyfillsRawSources(features, filters);
        String expectedPolyfillsString = getMockRawSources("c");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    /****************************************************************
     * Helpers
     ****************************************************************/
    private String getMockRawSources(String ... featureNames) {
        return getMockSources(Arrays.asList(featureNames), false);
    }

    private String getMockMinSources(String ... featureNames) {
        return getMockSources(Arrays.asList(featureNames), true);
    }

    private String getMockSources(List<String> featureNameList, boolean doMinify) {
        return featureNameList.stream()
                .map(featureName -> featureName + (doMinify ? ".min" : ".raw_"))
                .collect(Collectors.joining());
    }

    private String getPolyfillsRawSources(List<Feature> features, Filters filters) {
        return getPolyfillsSources(features, filters, false);
    }

    private String getPolyfillsMinSources(List<Feature> features, Filters filters) {
        return getPolyfillsSources(features, filters, true);
    }

    private String getPolyfillsSources(List<Feature> features, Filters filters, boolean minify) {
        return polyfillQueryService.getFeatures(features, filters).stream()
                .map(feature -> feature.getSource(minify))
                .collect(Collectors.joining());
    }
}
