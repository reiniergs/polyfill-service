package org.polyfillservice.api.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.polyfillservice.api.components.Feature;
import org.polyfillservice.api.components.Polyfill;
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
    private PolyfillService service;

    @Test
    public void testGetPolyfill() {
        Polyfill polyfill = service.getPolyfill("a");
        assertEquals("a", polyfill.getName());
    }

    @Test
    public void testGetPolyfillsWithNoServiceConfigPolyfillsAndNoQuery() {
        List<Polyfill> polyfills = service.getPolyfills("chrome/30");
        String polyfillNames = polyfillListToString(polyfills);
        assertEquals("", polyfillNames);
    }

    @Test
    public void testGetPolyfillsUserAgentMeetVersionRequirements() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query.Builder().includeFeatures(features).build();
        List<Polyfill> polyfills = service.getPolyfills("chrome/30", query);
        assertEquals("cbda", polyfillListToString(polyfills));
    }

    @Test
    public void testGetPolyfillsUserAgentSomeNotMeetVersionRequirement() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query.Builder().includeFeatures(features).build();
        List<Polyfill> polyfills = service.getPolyfills("firefox/5", query);
        assertEquals("ca", polyfillListToString(polyfills));
    }

    @Test
    public void testGetPolyfillsUnknownUserAgent() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query.Builder()
            .includeFeatures(features)
            .setLoadOnUnknownUA(false)
            .build();
        List<Polyfill> polyfills = service.getPolyfills("firefox/1", query);
        assertEquals("", polyfillListToString(polyfills));
    }

    @Test
    public void testGetPolyfillsFeaturesAlwaysLoaded() {
        List<Feature> features = Arrays.asList(new Feature("default", false, true));
        Query query = new Query.Builder().includeFeatures(features).build();
        List<Polyfill> polyfills = service.getPolyfills("firefox/5", query);
        assertEquals("cbda", polyfillListToString(polyfills));
    }

    @Test
    public void testGetPolyfillsUserAgentNotMeetBaselineNullifyAlwaysFlag() {
        List<Feature> features = Arrays.asList(new Feature("default", false, true));
        Query query = new Query.Builder()
            .includeFeatures(features)
            .setLoadOnUnknownUA(false)
            .build();
        List<Polyfill> polyfills = service.getPolyfills("firefox/1", query);
        assertEquals("", polyfillListToString(polyfills));
    }

    @Test
    public void testGetPolyfillsNoFeatures() {
        List<Feature> features = Collections.emptyList();
        Query query = new Query.Builder().includeFeatures(features).build();
        List<Polyfill> polyfills = service.getPolyfills("chrome/30", query);
        assertEquals("", polyfillListToString(polyfills));
    }

    @Test
    public void testGetPolyfillsSingleFeature() {
        List<Feature> features = Arrays.asList(new Feature("c"));
        Query query = new Query.Builder().includeFeatures(features).build();
        List<Polyfill> polyfills = service.getPolyfills("chrome/30", query);
        assertEquals("c", polyfillListToString(polyfills));
    }

    @Test
    public void testGetPolyfillsMultipleFeatures() {
        List<Feature> features = Arrays.asList(new Feature("c"), new Feature("e"));
        Query query = new Query.Builder().includeFeatures(features).build();
        List<Polyfill> polyfills = service.getPolyfills("chrome/30", query);
        assertEquals("ce", polyfillListToString(polyfills));
    }

    @Test
    public void testGetPolyfillsSingleAlias() {
        List<Feature> features = Arrays.asList(new Feature("foo"));
        Query query = new Query.Builder().includeFeatures(features).build();
        List<Polyfill> polyfills = service.getPolyfills("chrome/30", query);
        assertEquals("ce", polyfillListToString(polyfills));
    }

    @Test
    public void testGetPolyfillsMultipleAliases() {
        List<Feature> features = Arrays.asList(new Feature("default"), new Feature("foo"));
        Query query = new Query.Builder().includeFeatures(features).build();
        List<Polyfill> polyfills = service.getPolyfills("chrome/30", query);
        assertEquals("cebda", polyfillListToString(polyfills));
    }

    @Test
    public void testGetPolyfillsMixingAliasAndFeature() {
        List<Feature> features = Arrays.asList(new Feature("default"), new Feature("e"));
        Query query = new Query.Builder().includeFeatures(features).build();
        List<Polyfill> polyfills = service.getPolyfills("chrome/30", query);
        assertEquals("cebda", polyfillListToString(polyfills));
    }

    @Test
    public void testGetPolyfillsExcludesFeatures() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query.Builder()
            .includeFeatures(features)
            .excludeFeatures("c", "b")
            .build();
        List<Polyfill> polyfills = service.getPolyfills("chrome/30", query);
        assertEquals("da", polyfillListToString(polyfills));
    }

    @Test
    public void testGetPolyfillsCannotExcludeAlias() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query.Builder()
            .includeFeatures(features)
            .excludeFeatures("es6")
            .build();
        List<Polyfill> polyfills = service.getPolyfills("chrome/30", query);
        assertEquals("cbda", polyfillListToString(polyfills));
    }

    @Test
    public void testGetPolyfillsLoadOnUnknownUA() {
        List<Feature> features = Arrays.asList(new Feature("c"));
        Query query = new Query.Builder()
            .includeFeatures(features)
            .setLoadOnUnknownUA(true)
            .build();
        List<Polyfill> polyfills = service.getPolyfills("unknown/0.0.0", query);
        assertEquals("c", polyfillListToString(polyfills));
    }

    @Test
    public void testGetPolyfillsSourceDefaultServiceConfigWithoutQuery() {
        String actual = service.getPolyfillsSource("chrome/30");
        assertEquals("", actual);
    }

    @Test
    public void testGetPolyfillsSourceDefaultServiceConfig() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query.Builder().includeFeatures(features).build();
        String actual = service.getPolyfillsSource("chrome/30", query);
        String expected = "(function(undefined) {if(!(c.detectSource)){c.min}if(!(b.detectSource)){b.min}if(!(d.detectSource)){d.min}if(!(a.detectSource)){a.min}}).call('object' === typeof window && window || 'object' === typeof self && self || 'object' === typeof global && global || {});";
        assertEquals(expected, actual);
    }

    @Test
    public void testGetPolyfillsSourceQueryOverrideDefault() {
        List<Feature> features = Arrays.asList(new Feature("default"));
        Query query = new Query.Builder()
            .setGatedForAll(false)
            .includeFeatures(features)
            .build();
        String actual = service.getPolyfillsSource("chrome/30", query);
        String expected = "(function(undefined) {c.minb.mind.mina.min}).call('object' === typeof window && window || 'object' === typeof self && self || 'object' === typeof global && global || {});";
        assertEquals(expected, actual);
    }

    /****************************************************************
     * Helpers
     ****************************************************************/
    private String polyfillListToString(List<Polyfill> polyfills) {
        return polyfills.stream()
            .map(polyfill -> polyfill.getName())
            .collect(Collectors.joining());
    }
}
