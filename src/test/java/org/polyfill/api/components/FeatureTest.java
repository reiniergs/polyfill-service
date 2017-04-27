package org.polyfill.api.components;

import org.junit.Test;

import static junit.framework.TestCase.*;

/**
 * Created by smo on 3/5/17.
 */
public class FeatureTest {

    private final String minSource = "min";
    private final String rawSource = "raw";
    private final String detectSource = "detect";

    @Test
    public void testFeatureNameWithNoFlags() {
        Feature feature = new Feature("featureName");
        assertEquals("featureName", feature.getName());
    }

    @Test
    public void testFeatureWithGated() {
        Feature feature = new Feature("featureName", true, false);
        assertTrue("Expected feature to be gated", feature.isGated());
    }

    @Test
    public void testFeatureWithAlways() {
        Feature feature = new Feature("featureName", false, true);
        assertTrue("Expected feature to be always shown", feature.isAlways());
    }

    @Test
    public void testFeatureWithGatedAlways() {
        Feature feature = new Feature("featureName", true, true);
        assertTrue("Expected feature to be gated", feature.isGated());
        assertTrue("Expected feature to be always shown", feature.isAlways());
    }

    @Test
    public void testGetMinSource() {
        Feature feature = getFeatureWithSources(false);
        assertEquals(minSource, feature.getSource(true));
    }

    @Test
    public void testGetMinSourceGated() {
        Feature feature = getFeatureWithSources(true);
        String detectMinSource = "if(!(" + detectSource + ")){" + minSource + "}";
        assertEquals(detectMinSource, feature.getSource(true));
    }

    @Test
    public void testGetRawSource() {
        Feature feature = getFeatureWithSources(false);
        assertEquals(rawSource, feature.getSource(false));
    }

    @Test
    public void testGetRawSourceGated() {
        Feature feature = getFeatureWithSources(true);
        String detectRawSource = "if(!(" + detectSource + ")){\n" + rawSource + "\n}\n\n";
        assertEquals(detectRawSource, feature.getSource(false));
    }

    @Test
    public void testNoDetectSource() {
        Feature feature = getFeatureWithSources(false);
        assertEquals(minSource, feature.getSource(true));
        assertEquals(rawSource, feature.getSource(false));
    }

    private Feature getFeatureWithSources(boolean hasDetectSource) {
        String name = "feature";

        Polyfill polyfill = new Polyfill.Builder(name)
                .minSource(this.minSource)
                .rawSource(this.rawSource)
                .detectSource(hasDetectSource ? this.detectSource : null)
                .build();

        Feature feature = new Feature(name, hasDetectSource, false);
        feature.setPolyfill(polyfill);
        return feature;
    }
}
