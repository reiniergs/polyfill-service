package org.polyfill.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.polyfill.components.Feature;
import org.polyfill.components.Query;
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
        Query query = new Query(features)
                .setUserAgent(new UserAgentImpl("chrome", "30"));
        String actualPolyfillsString = getPolyfillsRawSources(query);
        String expectedPolyfillsString = getMockRawSources("c", "b", "d", "a");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchByUserAgentSomeNotMeetVersionRequirement() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query(features)
                .setUserAgent(new UserAgentImpl("firefox", "5"));
        String actualPolyfillsString = getPolyfillsRawSources(query);
        String expectedPolyfillsString = getMockRawSources("c", "a");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testUnknownUserAgentShouldReturnEmpty() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query(features)
                .setUserAgent(new UserAgentImpl("firefox", "1"));
        String actualPolyfillsString = getPolyfillsRawSources(query);
        String expectedPolyfillsString = "";
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testFeaturesAlwaysLoaded() {
        List<Feature> features = Arrays.asList(new Feature("default", false, true));
        Query query = new Query(features)
                .setUserAgent(new UserAgentImpl("firefox", "5"));
        String actualPolyfillsString = getPolyfillsRawSources(query);
        String expectedPolyfillsString = getMockRawSources("c", "b", "d", "a");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testUserAgentNotMeetBaselineNullifyAlwaysFlag() {
        List<Feature> features = Arrays.asList(new Feature("default", false, true));
        Query query = new Query(features)
                .setUserAgent(new UserAgentImpl("firefox", "1"));
        String actualPolyfillsString = getPolyfillsRawSources(query);
        String expectedPolyfillsString = "";
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testNoFeaturesShouldReturnEmpty() {
        List<Feature> features = Collections.emptyList();
        Query query = new Query(features);
        String actualPolyfillsString = getPolyfillsRawSources(query);
        String expectedPolyfillsString = "";
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchBySingleFeature() {
        List<Feature> features = Arrays.asList(new Feature("c"));
        Query query = new Query(features);
        String actualPolyfillsString = getPolyfillsRawSources(query);
        String expectedPolyfillsString = getMockRawSources("c");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchByMultipleFeatures() {
        List<Feature> features = Arrays.asList(new Feature("c"), new Feature("e"));
        Query query = new Query(features);
        String actualPolyfillsString = getPolyfillsRawSources(query);
        String expectedPolyfillsString = getMockRawSources("c", "e");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchBySingleAlias() {
        List<Feature> features = Arrays.asList(new Feature("foo"));
        Query query = new Query(features);
        String actualPolyfillsString = getPolyfillsRawSources(query);
        String expectedPolyfillsString = getMockRawSources("c", "e");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchByMultipleAliases() {
        List<Feature> features = Arrays.asList(new Feature("default"), new Feature("foo"));
        Query query = new Query(features);
        String actualPolyfillsString = getPolyfillsRawSources(query);
        String expectedPolyfillsString = getMockRawSources("c", "e", "b", "d", "a");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchByMixingAliasAndFeature() {
        List<Feature> features = Arrays.asList(new Feature("default"), new Feature("e"));
        Query query = new Query(features);
        String actualPolyfillsString = getPolyfillsRawSources(query);
        String expectedPolyfillsString = getMockRawSources("c", "e", "b", "d", "a");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testExcludesFeatures() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query(features)
                .excludeFeatures("c", "b");
        String actualPolyfillsString = getPolyfillsRawSources(query);
        String expectedPolyfillsString = getMockRawSources("d", "a");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testCannotExcludeAlias() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query(features)
                .excludeFeatures("es6");
        String actualPolyfillsString = getPolyfillsRawSources(query);
        String expectedPolyfillsString = getMockRawSources("c", "b", "d", "a");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testDoMinify() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query(features);
        String actualPolyfillsString = getPolyfillsMinSources(query);
        String expectedPolyfillsString = getMockMinSources("c", "b", "d", "a");
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testLoadOnUnknownUA() {
        List<Feature> features = Arrays.asList(new Feature("c"));
        Query query = new Query(features)
                .setUserAgent(new UserAgentImpl("unknown", "0.0.0"))
                .setLoadOnUnknownUA(true);

        String actualPolyfillsString = getPolyfillsRawSources(query);
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
                .map(featureName -> featureName + (doMinify ? ".min" : ".raw"))
                .collect(Collectors.joining());
    }

    private String getPolyfillsRawSources(Query query) {
        return getPolyfillsSources(query, false);
    }

    private String getPolyfillsMinSources(Query query) {
        return getPolyfillsSources(query, true);
    }

    private String getPolyfillsSources(Query query, boolean minify) {
        return polyfillQueryService.getFeatures(query).stream()
                .map(feature -> feature.getSource(minify))
                .collect(Collectors.joining());
    }
}
