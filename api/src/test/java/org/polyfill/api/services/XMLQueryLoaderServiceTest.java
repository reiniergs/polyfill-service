package org.polyfill.api.services;

import org.junit.Test;
import org.polyfill.api.components.Feature;
import org.polyfill.api.components.Query;
import org.springframework.core.io.Resource;
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

    private XMLQueryLoaderService queryLoaderService = new XMLQueryLoaderService();
    private final String testXML = "query-config.xml";
    private final String testXMLWithFlags = "query-config-flags.xml";
    private final String testResourceFolderName = "queries";

    @Test
    public void testInvalidXMLPath() throws Exception {
        try {
            loadTestQuery("invalid query");
            fail("Should throw IOException when query config file doesn't exist");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    public void testNumFeaturesLoaded() throws Exception {
        Query query = loadTestQuery(testXML);
        assertEquals("There should be 5 features defined to be loaded", 5, query.getFeatures().size());
    }

    @Test
    public void testFeaturesLoaded() throws Exception {
        List<String> expectedFeatures = Arrays.asList("default",
                "GFeature", "AFeature", "GAFeature", "NGAFeature");
        Query query = loadTestQuery(testXML);
        for (int i = 0; i < expectedFeatures.size(); i++) {
            assertEquals(expectedFeatures.get(i), query.getFeatures().get(i).getName());
        }
    }

    @Test
    public void testGatedFeature() throws Exception {
        Query query = loadTestQuery(testXML);
        Feature featureReq = getFeatureByName(query, "GFeature");
        assertTrue("GFeature should be gated", featureReq.isGated());
    }

    @Test
    public void testAlwaysFeature() throws Exception {
        Query query = loadTestQuery(testXML);
        Feature featureReq = getFeatureByName(query, "AFeature");
        assertTrue("AFeature should be always loaded", featureReq.isAlways());
    }

    @Test
    public void testGatedAlwaysFeature() throws Exception {
        Query query = loadTestQuery(testXML);
        Feature featureReq = getFeatureByName(query, "GAFeature");
        assertTrue("GAFeature should be gated and always loaded", featureReq.isGated() && featureReq.isAlways());
    }

    @Test
    public void testNonGatedAlwaysFeature() throws Exception {
        Query query = loadTestQuery(testXML);
        Feature featureReq = getFeatureByName(query, "NGAFeature");
        assertTrue("GAFeature should not be gated and always loaded", !featureReq.isGated() && !featureReq.isAlways());
    }

    @Test
    public void testGetQueryFromInputStream() throws Exception {
        List<String> expectedFeatures = Arrays.asList("default",
                "GFeature", "AFeature", "GAFeature", "NGAFeature");
        Resource resource = queryLoaderService.getResource(testResourceFolderName, testXML);
        Query query = queryLoaderService.loadQuery(resource.getInputStream());
        for (int i = 0; i < expectedFeatures.size(); i++) {
            assertEquals(expectedFeatures.get(i), query.getFeatures().get(i).getName());
        }
    }

    @Test
    public void testGlobalGated() throws Exception {
        Query query = loadTestQuery(testXMLWithFlags);
        assertTrue("All should be gated", query.isGatedForAll());
    }

    @Test
    public void testGlobalAlways() throws Exception {
        Query query = loadTestQuery(testXMLWithFlags);
        assertTrue("All should be always loaded", query.isAlwaysForAll());
    }

    @Test
    public void testMinified() throws Exception {
        Query query = loadTestQuery(testXMLWithFlags);
        assertTrue("Polyfills should be minified", query.shouldMinify());
    }

    @Test
    public void testLoadOnUnknownUA() throws Exception {
        Query query = loadTestQuery(testXMLWithFlags);
        assertTrue("Polyfills should be loaded when UA is unknown", query.shouldLoadOnUnknownUA());
    }

    @Test
    public void testExcludedFeatures() throws Exception {
        List<String> expectedExcludes = Arrays.asList("ExcludedFeature1", "ExcludedFeature2");
        Query query = loadTestQuery(testXML);
        for (String exclude : expectedExcludes) {
            assertTrue("Incorrect excludes loaded: " + query.getExcludes(),
                    query.getExcludes().contains(exclude));
        }
    }

    private Query loadTestQuery(String fileName) throws IOException {
        Path resourcePath = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + ".").toPath();
        Path filePath = resourcePath.resolve(testResourceFolderName).resolve(fileName);
        return queryLoaderService.loadQuery(filePath.toString());
    }

    private Feature getFeatureByName(Query query, String name) {
        return query.getFeatures().stream()
                .filter(feature -> name.equals(feature.getName()))
                .findFirst().get();
    }
}
