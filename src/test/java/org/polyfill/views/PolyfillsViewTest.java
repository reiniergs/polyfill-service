package org.polyfill.views;

import org.junit.Test;
import org.polyfill.components.Feature;
import org.polyfill.components.Polyfill;
import org.polyfill.components.UserAgentImpl;
import org.polyfill.interfaces.UserAgent;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by smo on 3/20/17.
 */
public class PolyfillsViewTest {

    private static final UserAgent USER_AGENT = new UserAgentImpl("chrome", "4.5.6");
    private static final String PROJECT_VERSION = "1.2.3";
    private static final String PROJECT_URL = "https://polyfills.com";
    private static final String PROJECT_VERSION_LINE = "Polyfill service v" + PROJECT_VERSION;
    private static final String PROJECT_URL_LINE = "For detailed credits and licence information see " + PROJECT_URL;
    private static final String USER_AGENT_LINE = "UA detected: " + USER_AGENT.toString();
    private static final String NO_POLYFILLS_MESSAGE = "/* No polyfills found for current settings */";
    private static final String MIN_MESSAGE = "/* Disable minification (remove `.min` from URL path) for more info */";
    private static final List<Feature> EMPTY_LIST = Collections.emptyList();

    @Test
    public void testContentType() throws Exception {
        HttpServletResponse resp = getPolyfillsViewResp(EMPTY_LIST, EMPTY_LIST, false);
        assertEquals("text/javascript;charset=UTF-8", resp.getContentType());
    }

    @Test
    public void testAccessControlAllowOrigin() throws Exception {
        HttpServletResponse resp = getPolyfillsViewResp(EMPTY_LIST, EMPTY_LIST, false);
        assertEquals("*", resp.getHeader("Access-Control-Allow-Origin"));
    }

    @Test
    public void testRawNoPolyfillsLoaded() throws Exception {
        List<Feature> requestedList = Collections.singletonList(new Feature("default"));
        MockHttpServletResponse resp = getPolyfillsViewResp(requestedList, EMPTY_LIST, false);
        String expectedResponse =
                "/* " + PROJECT_VERSION_LINE +"\n" +
                " * " + PROJECT_URL_LINE + "\n" +
                " * \n" +
                " * " + USER_AGENT_LINE + "\n" +
                " * Features requested: default */\n\n" +
                NO_POLYFILLS_MESSAGE;
        assertEquals(expectedResponse, resp.getContentAsString());
    }

    @Test
    public void testMinNoPolyfillsLoaded() throws Exception {
        List<Feature> requestedList = Collections.singletonList(new Feature("default"));
        MockHttpServletResponse resp = getPolyfillsViewResp(requestedList, EMPTY_LIST, true);
        assertEquals(MIN_MESSAGE, resp.getContentAsString());
    }

    @Test
    public void testRawPolyfillsLoaded() throws Exception {
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
        testPolyfillsLoadedTemplate(false, expectedResponse);
    }

    @Test
    public void testMinPolyfillsLoaded() throws Exception {
        String expectedResponse = MIN_MESSAGE + "\n\n" + wrapInClosure("abcdef", true);
        testPolyfillsLoadedTemplate(true, expectedResponse);
    }

    private void testPolyfillsLoadedTemplate(boolean minify, String expectedResponse) throws Exception {
        String featureLoadedName1 = "123";
        Feature featureRequested = new Feature("default");
        Feature featureLoaded1 = new Feature(featureLoadedName1, featureRequested);
        featureLoaded1.setPolyfill(new Polyfill(featureLoaded1.getName(), new HashMap<String, Object>(){{
            put(Polyfill.RAW_SOURCE_KEY, "abc");
            put(Polyfill.MIN_SOURCE_KEY, "abc");
            put(Polyfill.LICENSE_KEY, "MIT");
        }}));

        String featureLoadedName2 = "456";
        Feature featureLoaded2 = new Feature(featureLoadedName2, featureLoaded1);
        featureLoaded2.setPolyfill(new Polyfill(featureLoaded2.getName(), new HashMap<String, Object>(){{
            put(Polyfill.RAW_SOURCE_KEY, "def");
            put(Polyfill.MIN_SOURCE_KEY, "def");
            put(Polyfill.LICENSE_KEY, "LALALA");
        }}));

        List<Feature> requestedList = Collections.singletonList(featureRequested);
        List<Feature> loadedList = Arrays.asList(featureLoaded1, featureLoaded2);
        MockHttpServletResponse resp = getPolyfillsViewResp(requestedList, loadedList, minify);

        assertEquals(expectedResponse, resp.getContentAsString());
    }

    private String wrapInClosure(String source, boolean minify) {
        String lf = minify ? "" : "\n";
        return "(function(undefined) {" + lf + source + "})" + lf
                + ".call('object' === typeof window && window" // bind `this` to window in a browser
                + " || 'object' === typeof self && self"       // bind `this` to self in a web worker
                + " || 'object' === typeof global && global"   // bind `this` to global in Node
                + " || {});";
    }

    private MockHttpServletResponse getPolyfillsViewResp(List<Feature> requestedList, List<Feature> loadedList,
            boolean minify) throws Exception {
        View polyfillsView = new PolyfillsView(PROJECT_VERSION, PROJECT_URL, USER_AGENT, requestedList, loadedList, minify);
        MockHttpServletResponse mockResp = new MockHttpServletResponse();
        polyfillsView.render(null, null, mockResp);
        return mockResp;
    }
}
