package org.polyfill.components;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by smo on 2/16/17.
 */
public class PolyfillTest {

    @Test
    public void testEmptyPolyfill() {
        Polyfill polyfill = new Polyfill.Builder("abc").build();
        assertTrue(polyfill.getAliases().isEmpty());
        assertTrue(polyfill.getDependencies().isEmpty());
        assertTrue(polyfill.getAllBrowserRequirements().isEmpty());
    }
}
