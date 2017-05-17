package org.polyfill.api.services;

import org.junit.Test;
import org.polyfill.api.components.Feature;
import org.polyfill.api.components.Query;
import org.polyfill.api.interfaces.QueryLoaderService;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.*;

/**
 * Created by smo on 4/23/17.
 */
public class XMLQueryLoaderServiceTest {

    QueryLoaderService queryLoaderService = new XMLQueryLoaderService();

    @Test
    public void testInvalidXMLPath() throws Exception {
        Query query = loadTestQuery("invalid query");
        assertNull("Expected to be null when xml file doesn't exist", query);
    }

    @Test
    public void testNumFeaturesLoaded() throws Exception {
        Query query = loadTestQuery("query-config.xml");
        assertEquals("There should be 5 features defined to be loaded", 5, query.getFeatures().size());
    }

    @Test
    public void testFeaturesLoaded() throws Exception {
        List<String> expectedFeatures = Arrays.asList("default",
                "GFeature", "AFeature", "GAFeature", "NGAFeature");
        Query query = loadTestQuery("query-config.xml");
        for (int i = 0; i < expectedFeatures.size(); i++) {
            assertEquals(expectedFeatures.get(i), query.getFeatures().get(i).getName());
        }
    }

    @Test
    public void testGatedFeature() throws Exception {
        Query query = loadTestQuery("query-config.xml");
        Feature featureReq = getFeatureByName(query, "GFeature");
        assertTrue("GFeature should be gated", featureReq.isGated());
    }

    @Test
    public void testAlwaysFeature() throws Exception {
        Query query = loadTestQuery("query-config.xml");
        Feature featureReq = getFeatureByName(query, "AFeature");
        assertTrue("AFeature should be always loaded", featureReq.isAlways());
    }

    @Test
    public void testGatedAlwaysFeature() throws Exception {
        Query query = loadTestQuery("query-config.xml");
        Feature featureReq = getFeatureByName(query, "GAFeature");
        assertTrue("GAFeature should be gated and always loaded", featureReq.isGated() && featureReq.isAlways());
    }

    @Test
    public void testNonGatedAlwaysFeature() throws Exception {
        Query query = loadTestQuery("query-config.xml");
        Feature featureReq = getFeatureByName(query, "NGAFeature");
        assertTrue("GAFeature should not be gated and always loaded", !featureReq.isGated() && !featureReq.isAlways());
    }

    private Query loadTestQuery(String fileName) throws IOException {
        Path resourcePath = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + ".").toPath();
        Path filePath = resourcePath.resolve("queries").resolve(fileName);
        return queryLoaderService.loadQuery(filePath.toString());
    }

    private Feature getFeatureByName(Query query, String name) {
        return query.getFeatures().stream()
                .filter(feature -> name.equals(feature.getName()))
                .findFirst().get();
    }
}
