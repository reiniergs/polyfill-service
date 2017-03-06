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
        List<String> excludes = new ArrayList<>();
        boolean doMinify = false;

        String actualPolyfillsString = polyfillQueryService.getPolyfillsSource(needAllUA, doMinify, features, excludes);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "b", "d", "a"), doMinify);
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchByUserAgentSomeNotMeetVersionRequirement() {
        UserAgent userAgent = new UserAgentImpl("firefox", "5");
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default"));
        List<String> excludes = new ArrayList<>();
        boolean doMinify = false;

        String actualPolyfillsString = polyfillQueryService.getPolyfillsSource(userAgent, doMinify, features, excludes);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "a"), doMinify);
    }

    @Test
    public void testUserAgentNotMeetBaseline() {
        UserAgent userAgent = new UserAgentImpl("firefox", "1");
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default"));
        List<String> excludes = new ArrayList<>();
        boolean doMinify = false;

        String actualPolyfillsString = polyfillQueryService.getPolyfillsSource(userAgent, doMinify, features, excludes);
        String expectedPolyfillsString = "";
        assertEquals("Should return empty String when user agent doesn't meet minimum requirements",
                expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testFeaturesAlwaysLoaded() {
        UserAgent userAgent = new UserAgentImpl("firefox", "5");
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default|always"));
        List<String> excludes = new ArrayList<>();
        boolean doMinify = false;

        String actualPolyfillsString = polyfillQueryService.getPolyfillsSource(userAgent, doMinify, features, excludes);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "b", "d", "a"), doMinify);
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testUserAgentNotMeetBaselineNullifyAlwaysFlag() {
        UserAgent userAgent = new UserAgentImpl("firefox", "1");
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default|always"));
        List<String> excludes = new ArrayList<>();
        boolean doMinify = false;

        String actualPolyfillsString = polyfillQueryService.getPolyfillsSource(userAgent, doMinify, features, excludes);
        String expectedPolyfillsString = "";
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testNoFeatures() {
        List<FeatureOptions> noFeatures = new ArrayList<>();
        List<String> noExcludes = new ArrayList<>();
        boolean doMinify = false;

        String actualPolyfillsString = polyfillQueryService.getPolyfillsSource(needAllUA, doMinify, noFeatures, noExcludes);
        assertEquals("Should return empty String when requesting no features", "", actualPolyfillsString);
    }

    @Test
    public void testSearchBySingleFeature() {
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("c"));
        List<String> excludes = new ArrayList<>();
        boolean doMinify = false;

        String actualPolyfillsString = polyfillQueryService.getPolyfillsSource(needAllUA, doMinify, features, excludes);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c"), doMinify);
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchByMultipleFeatures() {
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("c"), new FeatureOptions("e"));
        List<String> excludes = new ArrayList<>();
        boolean doMinify = false;

        String actualPolyfillsString = polyfillQueryService.getPolyfillsSource(needAllUA, doMinify, features, excludes);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "e"), doMinify);
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchBySingleAlias() {
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("foo"));
        List<String> excludes = new ArrayList<>();
        boolean doMinify = false;

        String actualPolyfillsString = polyfillQueryService.getPolyfillsSource(needAllUA, doMinify, features, excludes);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "e"), doMinify);
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchByMultipleAliases() {
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default"), new FeatureOptions("foo"));
        List<String> excludes = new ArrayList<>();
        boolean doMinify = false;

        String actualPolyfillsString = polyfillQueryService.getPolyfillsSource(needAllUA, doMinify, features, excludes);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "e", "b", "d", "a"), doMinify);
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testSearchByMixingAliasAndFeature() {
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default"), new FeatureOptions("e"));
        List<String> excludes = new ArrayList<>();
        boolean doMinify = false;

        String actualPolyfillsString = polyfillQueryService.getPolyfillsSource(needAllUA, doMinify, features, excludes);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "e", "b", "d", "a"), doMinify);
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testExcludesFeatures() {
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default"));
        List<String> excludes = Arrays.asList("c", "b");
        boolean doMinify = false;

        String actualPolyfillsString = polyfillQueryService.getPolyfillsSource(needAllUA, doMinify, features, excludes);
        String expectedPolyfillsString = getMockSources(Arrays.asList("d", "a"), doMinify);
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testCannotExcludeAlias() {
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default"));
        List<String> excludes = Arrays.asList("es6");
        boolean doMinify = false;

        String actualPolyfillsString = polyfillQueryService.getPolyfillsSource(needAllUA, doMinify, features, excludes);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "b", "d", "a"), doMinify);
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    @Test
    public void testDoMinify() {
        List<FeatureOptions> features = Arrays.asList(new FeatureOptions("default"));
        List<String> excludes = new ArrayList<>();
        boolean doMinify = true;

        String actualPolyfillsString = polyfillQueryService.getPolyfillsSource(needAllUA, doMinify, features, excludes);
        String expectedPolyfillsString = getMockSources(Arrays.asList("c", "b", "d", "a"), doMinify);
        assertEquals(expectedPolyfillsString, actualPolyfillsString);
    }

    /****************************************************************
     * Helper
     ****************************************************************/
    private String getMockSource(String featureName, boolean doMinify) {
        return featureName + (doMinify ? ".min" : ".raw_");
    }

    private String getMockSources(List<String> featureNameList, boolean doMinify) {
        return featureNameList.stream()
                .map(featureName -> getMockSource(featureName, doMinify))
                .collect(Collectors.joining());
    }
}
