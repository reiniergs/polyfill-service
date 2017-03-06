package org.polyfill.components;

import org.junit.Test;

import static junit.framework.TestCase.*;

/**
 * Created by smo on 3/5/17.
 */
public class FeatureOptionsTest {

    @Test
    public void testFeatureNameWithNoFlags() {
        FeatureOptions featureOptions = new FeatureOptions("featureName");
        assertEquals("featureName", featureOptions.getName());
    }

    @Test
    public void testFeatureNameWithFlags() {
        FeatureOptions featureOptions = new FeatureOptions("featureName|always|gated");
        assertEquals("featureName", featureOptions.getName());
    }

    @Test
    public void testFeatureWithGated() {
        FeatureOptions featureOptions = new FeatureOptions("featureName|gated");
        assertTrue("Expected feature to be gated", featureOptions.isGated());
    }

    @Test
    public void testFeatureWithAlways() {
        FeatureOptions featureOptions = new FeatureOptions("featureName|always");
        assertTrue("Expected feature to be always shown", featureOptions.isAlways());
    }

    @Test
    public void testFeatureWithGatedAlways() {
        FeatureOptions featureOptions = new FeatureOptions("featureName|always|gated");
        assertTrue("Expected feature to be gated", featureOptions.isGated());
        assertTrue("Expected feature to be always shown", featureOptions.isAlways());
    }
}
