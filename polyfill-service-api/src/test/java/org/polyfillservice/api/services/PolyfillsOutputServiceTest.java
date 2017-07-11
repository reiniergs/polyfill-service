package org.polyfillservice.api.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.polyfillservice.api.components.Feature;
import org.polyfillservice.api.components.Polyfill;
import org.polyfillservice.api.components.Query;
import org.polyfillservice.api.configurations.ProjectInfoConfig;
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
        classes={ProjectInfoConfig.class,
                PolyfillsOutputService.class
        })
public class PolyfillsOutputServiceTest {

    private static final String TEST_UA = "chrome/4.5.6";
    private static final String PROJECT_VERSION_LINE = "Polyfill service v1.2.3";
    private static final String PROJECT_URL_LINE = "For detailed credits and licence information see https://polyfills.com";
    private static final String USER_AGENT_LINE = "UA detected: " + TEST_UA;
    private static final String NO_POLYFILLS_MESSAGE = "No polyfills found for current settings";
    private static final String MIN_MESSAGE = "/* Disable minification (remove `.min` from URL path) for more info */";

    @Autowired
    private PolyfillsOutputService service;

    @Test
    public void testRawNoPolyfillsLoaded() {
        List<Feature> requestedList = Collections.singletonList(new Feature("default"));
        Query query = new Query.Builder(requestedList).setMinify(false).build();
        String actual = service.getPolyfillsSource(TEST_UA, query, Collections.emptyList(), true);
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
    public void testMinNoPolyfillsLoaded() {
        List<Feature> requestedList = Collections.singletonList(new Feature("default"));
        Query query = new Query.Builder(requestedList).setMinify(true).build();
        String actual = service.getPolyfillsSource(null, query, Collections.emptyList(), true);
        String expected = MIN_MESSAGE;
        assertEquals(expected, actual);
    }

    @Test
    public void testRawPolyfillsLoaded() {
        String expectedResponse = wrapInClosure("abcdef", false);
        testPolyfillsLoadedTemplate(expectedResponse, false, false);
    }

    @Test
    public void testMinPolyfillsLoaded() {
        String expectedResponse = wrapInClosure("abcdef", true);
        testPolyfillsLoadedTemplate(expectedResponse, true, false);
    }

    @Test
    public void testRawPolyfillsLoadedInDebugMode() {
        String expectedResponse =
                "/* " + PROJECT_VERSION_LINE +
                "\n * " + PROJECT_URL_LINE +
                "\n * " +
                "\n * " + USER_AGENT_LINE +
                "\n * Features requested: default" +
                "\n * " +
                "\n * - 123, License: MIT (required by \"default\")" +
                "\n * - 456, License: LALALA (required by \"default\", \"123\") */\n\n" +
                wrapInClosure("abcdef", false);
        testPolyfillsLoadedTemplate(expectedResponse, false, true);
    }

    @Test
    public void testMinPolyfillsLoadedInDebugMode() {
        String expectedResponse = MIN_MESSAGE + "\n\n" + wrapInClosure("abcdef", true);
        testPolyfillsLoadedTemplate(expectedResponse, true, true);
    }

    private void testPolyfillsLoadedTemplate(String expected, boolean minify, boolean isDebugMode) {

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
        String actual = service.getPolyfillsSource(TEST_UA, query, loadedList, isDebugMode);

        assertEquals(expected, actual);
    }

    @Test
    public void testRawWithDetect() {
        boolean minify = false;
        String expectedResponse = wrapInDetect("raw", "detect", minify);
        expectedResponse = wrapInClosure(expectedResponse, minify);
        testWrapInDetectTemplate(expectedResponse, minify);
    }

    @Test
    public void testMinWithDetect() {
        boolean minify = true;
        String expectedResponse = wrapInDetect("min", "detect", minify);
        expectedResponse = wrapInClosure(expectedResponse, minify);
        testWrapInDetectTemplate(expectedResponse, minify);
    }

    private void testWrapInDetectTemplate(String expected, boolean minify) {
        Feature feature = new Feature("feature");
        Polyfill polyfill = new Polyfill.Builder(feature.getName())
            .rawSource("raw")
            .minSource("min")
            .detectSource("detect")
            .build();
        feature.setPolyfill(polyfill);
        feature.setGated(true);

        List<Feature> featureList = Arrays.asList(feature);

        Query query = new Query.Builder(featureList).setMinify(minify).build();
        String actual = service.getPolyfillsSource(TEST_UA, query, featureList, false);

        assertEquals(expected, actual);
    }

    private String wrapInDetect(String source, String detectSource, boolean minify) {
        String lf = minify ? "" : "\n";
        return "if(!(" + detectSource + ")){" + lf + source + lf + "}" + lf + lf;
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
