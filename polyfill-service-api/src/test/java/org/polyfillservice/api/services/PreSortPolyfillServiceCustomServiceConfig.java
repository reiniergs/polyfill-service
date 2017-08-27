package org.polyfillservice.api.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.polyfillservice.api.components.Feature;
import org.polyfillservice.api.components.Query;
import org.polyfillservice.api.configurations.CustomServiceConfig;
import org.polyfillservice.api.configurations.PolyfillsConfig;
import org.polyfillservice.api.configurations.ProjectInfoConfig;
import org.polyfillservice.api.interfaces.PolyfillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by smo on 8/26/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    loader=AnnotationConfigContextLoader.class,
    classes={PolyfillsConfig.class,
        CustomServiceConfig.class,
        ProjectInfoConfig.class,
        SemVerUtilService.class,
        UADetectorAdapterParserService.class,
        PolyfillsOutputService.class,
        PreSortPolyfillService.class
    })
public class PreSortPolyfillServiceCustomServiceConfig {
    @Autowired
    private PolyfillService service;

    /**
     * Without query, polyfill service should use what's defined in
     * service config as the default query, in this case it should
     * load all polyfills
     */
    @Test
    public void testGetPolyfillsSourceCustomServiceConfigNoQuery() {
        String actual = service.getPolyfillsSource("chrome/30");
        String expected = "(function(undefined) {c.minb.mind.mina.min}).call('object' === typeof window && window || 'object' === typeof self && self || 'object' === typeof global && global || {});";
        assertEquals(expected, actual);
    }

    /**
     * Even though service config turns off gated flag, we still should be able to
     * turn it on dynamically with Query object
     */
    @Test
    public void testGetPolyfillsSourceCustomServiceConfigQueryOverrideDefault() {
        List<Feature> features = Arrays.asList(new Feature("c"));
        Query query = new Query.Builder()
            .includeFeatures(features)
            .setGatedForAll(true)
            .build();
        String actual = service.getPolyfillsSource("chrome/30", query);
        String expected = "(function(undefined) {if(!(c.detectSource)){c.min}}).call('object' === typeof window && window || 'object' === typeof self && self || 'object' === typeof global && global || {});";
        assertEquals(expected, actual);
    }
}
