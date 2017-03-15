package org.polyfill.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.polyfill.components.FeatureOptions;
import org.polyfill.components.UserAgentImpl;
import org.polyfill.configurations.MockPolyfillsConfig;
import org.polyfill.interfaces.PolyfillQueryService;
import org.polyfill.interfaces.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.ArrayList;
import java.util.Arrays;
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

    private UserAgent needAllUA = new UserAgentImpl("chrome", "30");

    @Test
    public void testSearchByUserAgentMeetVersionRequirements() {
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default"));
        String actualPolyfillsString = getPolyfillsSource(needAllUA, features);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "b", "d", "a"));
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchByUserAgentSomeNotMeetVersionRequirement() {
        UserAgent userAgent = new UserAgentImpl("firefox", "5");
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default"));
        String actualPolyfillsString = getPolyfillsSource(userAgent, features);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "a"));
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testUnknownUserAgentShouldReturnEmpty() {
        UserAgent userAgent = new UserAgentImpl("firefox", "1");
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default"));
        String actualPolyfillsString = getPolyfillsSource(userAgent, features);
        String expectedPolyfillsString = "";
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testFeaturesAlwaysLoaded() {
        UserAgent userAgent = new UserAgentImpl("firefox", "5");
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default|always"));
        String actualPolyfillsString = getPolyfillsSource(userAgent, features);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "b", "d", "a"));
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testUserAgentNotMeetBaselineNullifyAlwaysFlag() {
        UserAgent userAgent = new UserAgentImpl("firefox", "1");
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default|always"));
        String actualPolyfillsString = getPolyfillsSource(userAgent, features);
        String expectedPolyfillsString = "";
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testNoFeaturesShouldReturnEmpty() {
        List<FeatureOptions> features = new ArrayList<>();
        String actualPolyfillsString = getPolyfillsSource(needAllUA, features);
        String expectedPolyfillsString = "";
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchBySingleFeature() {
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("c"));
        String actualPolyfillsString = getPolyfillsSource(needAllUA, features);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c"));
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchByMultipleFeatures() {
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("c"), new FeatureOptions("e"));
        String actualPolyfillsString = getPolyfillsSource(needAllUA, features);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "e"));
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchBySingleAlias() {
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("foo"));
        String actualPolyfillsString = getPolyfillsSource(needAllUA, features);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "e"));
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchByMultipleAliases() {
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default"), new FeatureOptions("foo"));
        String actualPolyfillsString = getPolyfillsSource(needAllUA, features);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "e", "b", "d", "a"));
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchByMixingAliasAndFeature() {
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default"), new FeatureOptions("e"));
        String actualPolyfillsString = getPolyfillsSource(needAllUA, features);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "e", "b", "d", "a"));
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testExcludesFeatures() {
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default"));
        List<String> excludes = Arrays.asList("c", "b");
        String actualPolyfillsString = getPolyfillsSource(needAllUA, features, excludes);
        String expectedPolyfillsString = getMockSources(Arrays.asList("d", "a"));
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testCannotExcludeAlias() {
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default"));
        List<String> excludes = Arrays.asList("es6");
        String actualPolyfillsString = getPolyfillsSource(needAllUA, features, excludes);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "b", "d", "a"));
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testDoMinify() {
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default"));
        List<String> excludes = new ArrayList<>();
        boolean doMinify = true;
        boolean loadOnUnknownUA = false;

        String actualPolyfillsString = getPolyfillsSource(needAllUA, features, excludes, doMinify, loadOnUnknownUA);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "b", "d", "a"), true);
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testloadOnUnknownUA() {
        UserAgent unknownUA = new UserAgentImpl("unknown", "0.0.0");
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("c"));
        List<String> excludes = new ArrayList<>();
        boolean doMinify = false;
        boolean loadOnUnknownUA = true;

        String actualPolyfillsString = getPolyfillsSource(unknownUA, features, excludes, doMinify, loadOnUnknownUA);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c"), doMinify);
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    /****************************************************************
     * Helper
     ****************************************************************/
    // helpers to generate mock sources
    private String getMockSource(String featureName, boolean doMinify) {
        return featureName + (doMinify ? ".min" : ".raw_");
    }

    private String getMockSources(List<String> featureNameList, boolean doMinify) {
        return featureNameList.stream()
                .map(featureName -> getMockSource(featureName, doMinify))
                .collect(Collectors.joining());
    }

    private String getMockSources(List<String> featureNameList) {
        return getMockSources(featureNameList, false);
    }

    // helper methods to wrap the function under the test
    private String getPolyfillsSource(UserAgent userAgent, List<FeatureOptions> features) {
        return getPolyfillsSource(userAgent, features, new ArrayList<>());
    }

    private String getPolyfillsSource(UserAgent userAgent, List<FeatureOptions> features, List<String> excludes) {
        return getPolyfillsSource(userAgent, features, excludes, false, false);
    }

    private String getPolyfillsSource(UserAgent userAgent,
            List<FeatureOptions> features, List<String> excludes, boolean doMinify, boolean loadOnUnknownUA) {
        return polyfillQueryService.getPolyfillsSource(userAgent, features, excludes, doMinify, loadOnUnknownUA);
    }
}
