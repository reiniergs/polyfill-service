package org.polyfillservice.api.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.polyfillservice.api.components.Feature;
import org.polyfillservice.api.components.Query;
import org.polyfillservice.api.configurations.PolyfillsConfig;
import org.polyfillservice.api.configurations.ProjectInfoConfig;
import org.polyfillservice.api.interfaces.PolyfillService;
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
        classes={PolyfillsConfig.class,
                ProjectInfoConfig.class,
                SemVerUtilService.class,
                UADetectorAdapterParserService.class,
                PolyfillsOutputService.class,
                PreSortPolyfillService.class
        })
public class PreSortPolyfillServiceTest {

    @Autowired
    private PolyfillService polyfillService;

    @Test
    public void testSearchByUserAgentMeetVersionRequirements() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query.Builder().includeFeatures(features).build();
        String actual = getPolyfillsRawSources("chrome/30", query);
        String expected = getMockRawSources("c", "b", "d", "a");
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchByUserAgentSomeNotMeetVersionRequirement() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query.Builder().includeFeatures(features).build();
        String actual = getPolyfillsRawSources("firefox/5", query);
        String expected = getMockRawSources("c", "a");
        assertEquals(expected, actual);
    }

    @Test
    public void testUnknownUserAgentShouldReturnEmpty() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query.Builder()
            .includeFeatures(features)
            .setLoadOnUnknownUA(false)
            .build();
        String actual = getPolyfillsRawSources("firefox/1", query);
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void testFeaturesAlwaysLoaded() {
        List<Feature> features = Arrays.asList(new Feature("default", false, true));
        Query query = new Query.Builder().includeFeatures(features).build();
        String actual = getPolyfillsRawSources("firefox/5", query);
        String expected = getMockRawSources("c", "b", "d", "a");
        assertEquals(expected, actual);
    }

    @Test
    public void testUserAgentNotMeetBaselineNullifyAlwaysFlag() {
        List<Feature> features = Arrays.asList(new Feature("default", false, true));
        Query query = new Query.Builder()
            .includeFeatures(features)
            .setLoadOnUnknownUA(false)
            .build();
        String actual = getPolyfillsRawSources("firefox/1", query);
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void testNoFeaturesShouldReturnEmpty() {
        List<Feature> features = Collections.emptyList();
        Query query = new Query.Builder().includeFeatures(features).build();
        String actual = getPolyfillsRawSources(null, query);
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchBySingleFeature() {
        List<Feature> features = Arrays.asList(new Feature("c"));
        Query query = new Query.Builder().includeFeatures(features).build();
        String actual = getPolyfillsRawSources(null, query);
        String expected = getMockRawSources("c");
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchByMultipleFeatures() {
        List<Feature> features = Arrays.asList(new Feature("c"), new Feature("e"));
        Query query = new Query.Builder().includeFeatures(features).build();
        String actual = getPolyfillsRawSources(null, query);
        String expected = getMockRawSources("c", "e");
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchBySingleAlias() {
        List<Feature> features = Arrays.asList(new Feature("foo"));
        Query query = new Query.Builder().includeFeatures(features).build();
        String actual = getPolyfillsRawSources(null, query);
        String expected = getMockRawSources("c", "e");
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchByMultipleAliases() {
        List<Feature> features = Arrays.asList(new Feature("default"), new Feature("foo"));
        Query query = new Query.Builder().includeFeatures(features).build();
        String actual = getPolyfillsRawSources(null, query);
        String expected = getMockRawSources("c", "e", "b", "d", "a");
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchByMixingAliasAndFeature() {
        List<Feature> features = Arrays.asList(new Feature("default"), new Feature("e"));
        Query query = new Query.Builder().includeFeatures(features).build();
        String actual = getPolyfillsRawSources(null, query);
        String expected = getMockRawSources("c", "e", "b", "d", "a");
        assertEquals(expected, actual);
    }

    @Test
    public void testExcludesFeatures() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query.Builder()
            .includeFeatures(features)
            .excludeFeatures("c", "b")
            .build();
        String actual = getPolyfillsRawSources(null, query);
        String expected = getMockRawSources("d", "a");
        assertEquals(expected, actual);
    }

    @Test
    public void testCannotExcludeAlias() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query.Builder()
            .includeFeatures(features)
            .excludeFeatures("es6")
            .build();
        String actual = getPolyfillsRawSources(null, query);
        String expected = getMockRawSources("c", "b", "d", "a");
        assertEquals(expected, actual);
    }

    @Test
    public void testDoMinify() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query.Builder().includeFeatures(features).build();
        String actual = getPolyfillsMinSources(null, query);
        String expected = getMockMinSources("c", "b", "d", "a");
        assertEquals(expected, actual);
    }

    @Test
    public void testLoadOnUnknownUA() {
        List<Feature> features = Arrays.asList(new Feature("c"));
        Query query = new Query.Builder()
            .includeFeatures(features)
            .setLoadOnUnknownUA(true)
            .build();
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

    private String getPolyfillsRawSources(String uaString, Query query) {
        return getPolyfillsSources(uaString, query, false);
    }

    private String getPolyfillsMinSources(String uaString, Query query) {
        return getPolyfillsSources(uaString, query, true);
    }

    private String getPolyfillsSources(String uaString, Query query, boolean minify) {
        return polyfillService.getPolyfills(uaString, query).stream()
                .map(polyfill -> minify ? polyfill.getMinSource() : polyfill.getRawSource())
                .collect(Collectors.joining());
    }
}
