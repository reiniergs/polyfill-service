package org.polyfill.api.components;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by smo on 3/5/17.
 */
public class FeatureTest {

    private final String minSource = "min";
    private final String rawSource = "raw";
    private final String detectSource = "detect";

    @Test
    public void testName() {
        Feature feature = new Feature("featureName");
        assertEquals("featureName", feature.getName());
    }

    @Test
    public void testGated() {
        Feature feature = new Feature("featureName", true, false);
        assertTrue("Expected feature to be gated", feature.isGated());
    }

    @Test
    public void testAlways() {
        Feature feature = new Feature("featureName", false, true);
        assertTrue("Expected feature to be always shown", feature.isAlways());
    }

    @Test
    public void testGatedAlways() {
        Feature feature = new Feature("featureName", true, true);
        assertTrue("Expected feature to be gated", feature.isGated());
        assertTrue("Expected feature to be always shown", feature.isAlways());
    }

    @Test
    public void testRequiredBy() {
        Feature featureA = new Feature("a");
        Feature featureB = new Feature("b", featureA);
        assertTrue(featureB.getRequiredBys().contains("a"));
    }

    @Test
    public void testInheritRequiredBys() {
        Feature featureA = new Feature("a");
        Feature featureB = new Feature("b", featureA);
        Feature featureC = new Feature("c", featureB);

        assertTrue(featureC.getRequiredBys().contains("a"));
        assertTrue(featureC.getRequiredBys().contains("b"));
        assertEquals(2, featureC.getRequiredBys().size());
    }

    @Test
    public void testInheritFlags() {
        Feature featureA = new Feature("a", true, true);
        Feature featureB = new Feature("b", featureA);

        assertTrue("Expected featureB to be gated", featureB.isGated());
        assertTrue("Expected featureB to be always shown", featureB.isAlways());
    }
}
