package org.polyfill.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.polyfill.components.Feature;
import org.polyfill.components.Query;
import org.polyfill.configurations.MockBrowserAliasesConfig;
import org.polyfill.configurations.MockPolyfillsConfig;
import org.polyfill.configurations.MockProjectInfoConfig;
import org.polyfill.interfaces.PolyfillService;
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
                MockBrowserAliasesConfig.class,
                MockProjectInfoConfig.class,
                SemVerUtilService.class,
                UADetectorBasedUserAgentParserService.class,
                PolyfillsOutputService.class,
                PreSortPolyfillService.class
        })
public class PreSortPolyfillServiceTest {

    @Autowired
    private PolyfillService polyfillService;

    @Test
    public void testSearchByUserAgentMeetVersionRequirements() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query(features);
        String actual = getPolyfillsRawSources("chrome/30", query);
        String expected = getMockRawSources("c", "b", "d", "a");
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchByUserAgentSomeNotMeetVersionRequirement() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query(features);
        String actual = getPolyfillsRawSources("firefox/5", query);
        String expected = getMockRawSources("c", "a");
        assertEquals(expected, actual);
    }

    @Test
    public void testUnknownUserAgentShouldReturnEmpty() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query(features);
        String actual = getPolyfillsRawSources("firefox/1", query);
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void testFeaturesAlwaysLoaded() {
        List<Feature> features = Arrays.asList(new Feature("default", false, true));
        Query query = new Query(features);
        String actual = getPolyfillsRawSources("firefox/5", query);
        String expected = getMockRawSources("c", "b", "d", "a");
        assertEquals(expected, actual);
    }

    @Test
    public void testUserAgentNotMeetBaselineNullifyAlwaysFlag() {
        List<Feature> features = Arrays.asList(new Feature("default", false, true));
        Query query = new Query(features);
        String actual = getPolyfillsRawSources("firefox/1", query);
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void testNoFeaturesShouldReturnEmpty() {
        List<Feature> features = Collections.emptyList();
        Query query = new Query(features);
        String actual = getPolyfillsRawSources(null, query);
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchBySingleFeature() {
        List<Feature> features = Arrays.asList(new Feature("c"));
        Query query = new Query(features);
        String actual = getPolyfillsRawSources(null, query);
        String expected = getMockRawSources("c");
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchByMultipleFeatures() {
        List<Feature> features = Arrays.asList(new Feature("c"), new Feature("e"));
        Query query = new Query(features);
        String actual = getPolyfillsRawSources(null, query);
        String expected = getMockRawSources("c", "e");
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchBySingleAlias() {
        List<Feature> features = Arrays.asList(new Feature("foo"));
        Query query = new Query(features);
        String actual = getPolyfillsRawSources(null, query);
        String expected = getMockRawSources("c", "e");
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchByMultipleAliases() {
        List<Feature> features = Arrays.asList(new Feature("default"), new Feature("foo"));
        Query query = new Query(features);
        String actual = getPolyfillsRawSources(null, query);
        String expected = getMockRawSources("c", "e", "b", "d", "a");
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchByMixingAliasAndFeature() {
        List<Feature> features = Arrays.asList(new Feature("default"), new Feature("e"));
        Query query = new Query(features);
        String actual = getPolyfillsRawSources(null, query);
        String expected = getMockRawSources("c", "e", "b", "d", "a");
        assertEquals(expected, actual);
    }

    @Test
    public void testExcludesFeatures() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query(features).excludeFeatures("c", "b");
        String actual = getPolyfillsRawSources(null, query);
        String expected = getMockRawSources("d", "a");
        assertEquals(expected, actual);
    }

    @Test
    public void testCannotExcludeAlias() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query(features).excludeFeatures("es6");
        String actual = getPolyfillsRawSources(null, query);
        String expected = getMockRawSources("c", "b", "d", "a");
        assertEquals(expected, actual);
    }

    @Test
    public void testDoMinify() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query(features);
        String actual = getPolyfillsMinSources(null, query);
        String expected = getMockMinSources("c", "b", "d", "a");
        assertEquals(expected, actual);
    }

    @Test
    public void testLoadOnUnknownUA() {
        List<Feature> features = Arrays.asList(new Feature("c"));
        Query query = new Query(features).setLoadOnUnknownUA(true);
        String actual = getPolyfillsRawSources("unknown/0.0.0", query);
        String expected = getMockRawSources("c");
        assertEquals(expected, actual);
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
                .map(featureName -> featureName + (doMinify ? ".min" : ".raw"))
                .collect(Collectors.joining());
    }

    private String getPolyfillsRawSources(String userAgentString, Query query) {
        return getPolyfillsSources(userAgentString, query, false);
    }

    private String getPolyfillsMinSources(String userAgentString, Query query) {
        return getPolyfillsSources(userAgentString, query, true);
    }

    private String getPolyfillsSources(String userAgentString, Query query, boolean minify) {
        return polyfillService.getFeatures(query, userAgentString).stream()
                .map(feature -> feature.getSource(minify))
                .collect(Collectors.joining());
    }
}
