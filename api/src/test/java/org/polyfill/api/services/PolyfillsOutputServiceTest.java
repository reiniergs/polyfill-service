package org.polyfill.api.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.polyfill.api.components.Feature;
import org.polyfill.api.components.Polyfill;
import org.polyfill.api.components.Query;
import org.polyfill.api.configurations.MockProjectInfoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by smo on 3/20/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader=AnnotationConfigContextLoader.class,
        classes={MockProjectInfoConfig.class,
                PolyfillsOutputService.class
        })
public class PolyfillsOutputServiceTest {

    private static final String UA_STRING = "chrome/4.5.6";
    private static final String PROJECT_VERSION = "1.2.3";
    private static final String PROJECT_URL = "https://polyfills.com";
    private static final String PROJECT_VERSION_LINE = "Polyfill service v" + PROJECT_VERSION;
    private static final String PROJECT_URL_LINE = "For detailed credits and licence information see " + PROJECT_URL;
    private static final String USER_AGENT_LINE = "UA detected: " + UA_STRING;
    private static final String NO_POLYFILLS_MESSAGE = "No polyfills found for current settings";
    private static final String MIN_MESSAGE = "/* Disable minification (remove `.min` from URL path) for more info */";
    private static final List<Feature> EMPTY_LIST = Collections.emptyList();

    @Autowired
    private PolyfillsOutputService polyfillsOutputService;

    @Test
    public void testRawNoPolyfillsLoaded() throws Exception {
        List<Feature> requestedList = Collections.singletonList(new Feature("default"));
        Query query = new Query.Builder(requestedList).setMinify(false).build();
        String actual = polyfillsOutputService.getPolyfillsSource(UA_STRING, query, EMPTY_LIST, true);
        String expected =
            "/* " + PROJECT_VERSION_LINE +
            "\n * " + PROJECT_URL_LINE +
            "\n * " +
            "\n * " + USER_AGENT_LINE +
            "\n * Features requested: default" +
            "\n * " +
            "\n * " + NO_POLYFILLS_MESSAGE + " */";
        assertEquals(expected, actual);
    }

    @Test
    public void testMinNoPolyfillsLoaded() throws Exception {
        List<Feature> requestedList = Collections.singletonList(new Feature("default"));
        Query query = new Query.Builder(requestedList).setMinify(true).build();
        String actual = polyfillsOutputService.getPolyfillsSource(null, query, EMPTY_LIST, true);
        String expected = MIN_MESSAGE;
        assertEquals(expected, actual);
    }

    @Test
    public void testRawPolyfillsLoaded() throws Exception {
        String expectedResponse = wrapInClosure("abcdef", false);
        testPolyfillsLoadedTemplate(expectedResponse, false, false);
    }

    @Test
    public void testRawPolyfillsLoadedInDebugMode() throws Exception {
        String expectedResponse =
                "/* " + PROJECT_VERSION_LINE +"\n" +
                " * " + PROJECT_URL_LINE + "\n" +
                " * \n" +
                " * " + USER_AGENT_LINE + "\n" +
                " * Features requested: default\n" +
                " * \n" +
                " * - 123, License: MIT (required by \"default\")\n" +
                " * - 456, License: LALALA (required by \"default\", \"123\") */\n\n" +
                wrapInClosure("abcdef", false);
        testPolyfillsLoadedTemplate(expectedResponse, false, true);
    }

    @Test
    public void testMinPolyfillsLoaded() throws Exception {
        String expectedResponse = wrapInClosure("abcdef", true);
        testPolyfillsLoadedTemplate(expectedResponse, true, false);
    }

    @Test
    public void testMinPolyfillsLoadedInDebugMode() throws Exception {
        String expectedResponse = MIN_MESSAGE + "\n\n" + wrapInClosure("abcdef", true);
        testPolyfillsLoadedTemplate(expectedResponse, true, true);
    }

    private void testPolyfillsLoadedTemplate(String expected, boolean minify, boolean isDebugMode)
            throws Exception {

        Feature featureRequested = new Feature("default");

        Feature featureLoaded1 = new Feature("123", featureRequested);
        Polyfill polyfill1 = new Polyfill.Builder(featureLoaded1.getName())
                .rawSource("abc")
                .minSource("abc")
                .license("MIT")
                .build();
        featureLoaded1.setPolyfill(polyfill1);

        Feature featureLoaded2 = new Feature("456", featureLoaded1);
        Polyfill polyfill2 = new Polyfill.Builder(featureLoaded1.getName())
                .rawSource("def")
                .minSource("def")
                .license("LALALA")
                .build();
        featureLoaded2.setPolyfill(polyfill2);

        List<Feature> requestedList = Arrays.asList(featureRequested);
        List<Feature> loadedList = Arrays.asList(featureLoaded1, featureLoaded2);

        Query query = new Query.Builder(requestedList).setMinify(minify).build();
        String actual = polyfillsOutputService.getPolyfillsSource(UA_STRING, query, loadedList, isDebugMode);

        assertEquals(expected, actual);
    }

    private String wrapInClosure(String source, boolean minify) {
        String lf = minify ? "" : "\n";
        return "(function(undefined) {" + lf + source + "})" + lf
                + ".call('object' === typeof window && window" // bind `this` to window in a browser
                + " || 'object' === typeof self && self"       // bind `this` to self in a web worker
                + " || 'object' === typeof global && global"   // bind `this` to global in Node
                + " || {});";
    }
}
