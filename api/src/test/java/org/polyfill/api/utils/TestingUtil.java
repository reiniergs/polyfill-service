package org.polyfill.api.utils;

import org.polyfill.api.components.Polyfill;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by smo on 6/18/17.
 */
public class TestingUtil {

    public static void assertPolyfillsEqual(String message,
            Map<String, Polyfill> expected, Map<String, Polyfill> actual) {
        for (Map.Entry<String, Polyfill> entry : expected.entrySet()) {
            Polyfill expectedPolyfill = entry.getValue();
            Polyfill actualPolyfill = actual.get(entry.getKey());
            assertEquals(message, expectedPolyfill.toString(), actualPolyfill.toString());
        }
    }
}
