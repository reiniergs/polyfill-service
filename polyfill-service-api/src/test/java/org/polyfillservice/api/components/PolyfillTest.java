package org.polyfillservice.api.components;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by smo on 2/16/17.
 */
public class PolyfillTest {

    @Test
    public void testName() {
        String value = "abc";
        Polyfill polyfill = new Polyfill.Builder(value).build();
        assertEquals(value, polyfill.getName());
    }

    @Test
    public void testMinSource() {
        String value = "min source";
        Polyfill polyfill = new Polyfill.Builder("abc")
                .minSource(value)
                .build();
        assertEquals(value, polyfill.getMinSource());
    }

    @Test
    public void testRawSource() {
        String value = "raw source";
        Polyfill polyfill = new Polyfill.Builder("abc")
                .rawSource(value)
                .build();
        assertEquals(value, polyfill.getRawSource());
    }

    @Test
    public void testDetectSource() {
        String value = "detect source";
        Polyfill polyfill = new Polyfill.Builder("abc")
                .detectSource(value)
                .build();
        assertEquals(value, polyfill.getDetectSource());
    }

    @Test
    public void testTestsSource() {
        String value = "test source";
        Polyfill polyfill = new Polyfill.Builder("abc")
                .testsSource(value)
                .build();
        assertEquals(value, polyfill.getTestsSource());
    }

    @Test
    public void testIsTestable() {
        boolean value = true;
        Polyfill polyfill = new Polyfill.Builder("abc")
                .isTestable(value)
                .build();
        assertEquals(value, polyfill.isTestable());
    }

    @Test
    public void testLicense() {
        String value = "license";
        Polyfill polyfill = new Polyfill.Builder("abc")
                .license(value)
                .build();
        assertEquals(value, polyfill.getLicense());
    }

    @Test
    public void testRepository() {
        String value = "repo";
        Polyfill polyfill = new Polyfill.Builder("abc")
                .repository(value)
                .build();
        assertEquals(value, polyfill.getRepository());
    }

    @Test
    public void testAliases() {
        List<String> values = Arrays.asList("a", "b", "c");
        Polyfill polyfill = new Polyfill.Builder("abc")
                .aliases(values)
                .build();
        assertEquals(values, polyfill.getAliases());
    }

    @Test
    public void testDependencies() {
        List<String> values = Arrays.asList("a", "b", "c");
        Polyfill polyfill = new Polyfill.Builder("abc")
                .dependencies(values)
                .build();
        assertEquals(values, polyfill.getDependencies());
    }

    @Test
    public void testBrowserRequirements() {
        Map<String, String> values = new HashMap<String, String>(){{
            put("chrome", "53");
            put("firefox", "*");
            put("ie", "11");
        }};
        Polyfill polyfill = new Polyfill.Builder("abc")
                .browserRequirements(values)
                .build();
        assertEquals(values, polyfill.getAllBrowserRequirements());
        assertEquals("53", polyfill.getBrowserRequirement("chrome"));
    }

    @Test
    public void testEmpty() {
        Polyfill polyfill = new Polyfill.Builder("abc").build();
        assertTrue(polyfill.getAliases().isEmpty());
        assertTrue(polyfill.getDependencies().isEmpty());
        assertTrue(polyfill.getAllBrowserRequirements().isEmpty());
    }
}
