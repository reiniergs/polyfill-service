package org.polyfill.api.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.polyfill.api.components.Polyfill;
import org.polyfill.api.components.PolyfillLocationString;
import org.polyfill.api.configurations.MockPolyfillsConfig;
import org.polyfill.api.configurations.MockServiceConfigPartial;
import org.polyfill.api.interfaces.PolyfillLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by smo on 2/26/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader=AnnotationConfigContextLoader.class,
        classes={MockPolyfillsConfig.class,
                MockServiceConfigPartial.class,
                JsonConfigLoaderService.class,
                FinancialTimesPolyfillLoaderService.class
        })
public class FinancialTimesPolyfillLoaderServicePartialLoadTest {

    @Autowired
    private FinancialTimesPolyfillLoaderService polyfillLoaderService;

    @Resource(name = "polyfills")
    private Map<String, Polyfill> allPolyfills;

    private List<PolyfillLocation> polyfillLocationList;

    private Map<String, Polyfill> expectedPolyfills;

    @Before
    public void setup() {
        polyfillLocationList = Collections.singletonList(new PolyfillLocationString("polyfills"));
        expectedPolyfills = new HashMap<String, Polyfill>(){{
            put("d", allPolyfills.get("d")); // explicitly requested
            put("b", allPolyfills.get("b")); // depended by d
            put("c", allPolyfills.get("c")); // depended by d
            put("e", allPolyfills.get("e")); // contained by alias eee
        }};;
    }

    @Test
    public void testNumberOfPolyfillsLoaded() {
        try {
            Map<String, Polyfill> actualPolyfills = polyfillLoaderService.loadPolyfills(polyfillLocationList);
            assertEquals("Number of expectedPolyfills loaded is incorrect",
                expectedPolyfills.size(), actualPolyfills.size());
        } catch(IOException e) {
            fail("Loading polyfills with correct path should not throw IOException");
        }
    }

    @Test
    public void testPolyfillsLoaded() {
        try {
            Map<String, Polyfill> actualPolyfills = polyfillLoaderService.loadPolyfills(polyfillLocationList);
            assertEquals("Loaded polyfills are incorrect",
                expectedPolyfills.toString(), actualPolyfills.toString());
        } catch(IOException e) {
            fail("Loading polyfills with correct path should not throw IOException");
        }
    }
}
