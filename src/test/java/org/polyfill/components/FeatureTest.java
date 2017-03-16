package org.polyfill.components;

import org.junit.Test;

import static junit.framework.TestCase.*;

/**
 * Created by smo on 3/5/17.
 */
public class FeatureTest {

    @Test
    public void testFeatureNameWithNoFlags() {
        Feature feature = new Feature("featureName");
        assertEquals("featureName", feature.getName());
    }

    @Test
    public void testFeatureNameWithFlags() {
        Feature feature = new Feature("featureName|always|gated");
        assertEquals("featureName", feature.getName());
    }

    @Test
    public void testFeatureWithGated() {
        Feature feature = new Feature("featureName|gated");
        assertTrue("Expected feature to be gated", feature.isGated());
    }

    @Test
    public void testFeatureWithAlways() {
        Feature feature = new Feature("featureName|always");
        assertTrue("Expected feature to be always shown", feature.isAlways());
    }

    @Test
    public void testFeatureWithGatedAlways() {
        Feature feature = new Feature("featureName|always|gated");
        assertTrue("Expected feature to be gated", feature.isGated());
        assertTrue("Expected feature to be always shown", feature.isAlways());
    }
}
