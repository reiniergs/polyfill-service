package org.polyfill.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.polyfill.components.Polyfill;
import org.polyfill.components.UserAgentImpl;
import org.polyfill.interfaces.PolyfillQueryService;
import org.polyfill.interfaces.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:testContext.xml"})
public class PreSortPolyfillQueryServiceTest {

    @Autowired
    @Qualifier("presort")
    private PolyfillQueryService polyfillQueryService;

    @Test
    public void testSearchByUserAgentMeetVersionRequirements() {
        UserAgent userAgent = new UserAgentImpl("chrome", "53");
        List<Polyfill> polyfills = polyfillQueryService.getPolyfillsByUserAgent(userAgent);
        String[] expectedPolyfills = {"b,c", "b,c", "d.a", "a"};

        assertNumberOfPolyfills(expectedPolyfills.length, polyfills.size());
        assertPolyfillsOrder(expectedPolyfills, polyfills);
    }

    @Test
    public void testSearchByUserAgentSomeNotMeetVersionRequirement() {
        UserAgent userAgent = new UserAgentImpl("firefox", "5");
        List<Polyfill> polyfills = polyfillQueryService.getPolyfillsByUserAgent(userAgent);
        String[] expectedPolyfills = {"c", "a"};

        assertNumberOfPolyfills(expectedPolyfills.length, polyfills.size());
        assertPolyfillsOrder(expectedPolyfills, polyfills);
    }

    @Test
    public void testUserAgentNotMeetBaseline() {
        UserAgent userAgent = new UserAgentImpl("firefox", "1");
        List<Polyfill> polyfills;

        polyfills = polyfillQueryService.getPolyfillsByUserAgent(userAgent);
        assertNumberOfPolyfills(0, polyfills.size());

        polyfills = polyfillQueryService.getPolyfillsByFeatures(userAgent, new String[]{"a"});
        assertNumberOfPolyfills(0, polyfills.size());
    }

    @Test
    public void testSearchBySingleFeature() {
        UserAgent userAgent = new UserAgentImpl("chrome", "53");
        String[] expectedPolyfills = {"a"};
        String[] features = {"a"};
        List<Polyfill> polyfills = polyfillQueryService.getPolyfillsByFeatures(userAgent, features);

        assertNumberOfPolyfills(expectedPolyfills.length, polyfills.size());
        assertPolyfillsOrder(expectedPolyfills, polyfills);
    }

    @Test
    public void testSearchByMultipleFeatures() {
        UserAgent userAgent = new UserAgentImpl("chrome", "53");
        String[] expectedPolyfills = {"b", "a"};
        String[] features = {"a", "b"};
        List<Polyfill> polyfills = polyfillQueryService.getPolyfillsByFeatures(userAgent, features);

        assertNumberOfPolyfills(expectedPolyfills.length, polyfills.size());
        assertPolyfillsOrder(expectedPolyfills, polyfills);
    }

    @Test
    public void testSearchBySingleAlias() {
        UserAgent userAgent = new UserAgentImpl("chrome", "53");
        String[] expectedPolyfills = {"b,c", "b,c", "d.a"};
        String[] features = {"es6"};
        List<Polyfill> polyfills = polyfillQueryService.getPolyfillsByFeatures(userAgent, features);

        assertNumberOfPolyfills(expectedPolyfills.length, polyfills.size());
        assertPolyfillsOrder(expectedPolyfills, polyfills);
    }

    @Test
    public void testSearchByMultipleAliases() {
        UserAgent userAgent = new UserAgentImpl("chrome", "53");
        String[] features = {"es6", "default"};
        String[] expectedPolyfills = {"b,c", "b,c", "d.a", "a"};
        List<Polyfill> polyfills = polyfillQueryService.getPolyfillsByFeatures(userAgent, features);

        assertNumberOfPolyfills(expectedPolyfills.length, polyfills.size());
        assertPolyfillsOrder(expectedPolyfills, polyfills);
    }

    @Test
    public void testSearchByMixingAliasAndFeature() {
        UserAgent userAgent = new UserAgentImpl("chrome", "53");
        String[] features = {"foo", "d.a"};
        String[] expectedPolyfills = {"c", "d.a", "a"};
        List<Polyfill> polyfills = polyfillQueryService.getPolyfillsByFeatures(userAgent, features);

        assertNumberOfPolyfills(expectedPolyfills.length, polyfills.size());
        assertPolyfillsOrder(expectedPolyfills, polyfills);
    }

    /****************************************************************
     * Helper
     ****************************************************************/

    private void assertNumberOfPolyfills(int expected, int actual) {
        assertEquals("Number of polyfills should be " + expected, expected, actual);
    }

    private void assertPolyfillsOrder(String[] expectedPolyfills, List<Polyfill> polyfillsRetrieved) {

        for (int i = 0; i < expectedPolyfills.length; i++) {
            String actualPolyfillName = polyfillsRetrieved.get(i).getName();
            boolean isPolyfillIndexCorrect = false;
            String[] expectedPolyfillsList = expectedPolyfills[i].split(",");

            for (String expectedPolyfillName : expectedPolyfillsList) {
                if (expectedPolyfillName.equals(actualPolyfillName)) {
                    isPolyfillIndexCorrect = true;
                    break;
                }
            }
            assertTrue(expectedPolyfills[i].replace(",", " or ") + " should be #" + (i + 1) +
                    " in the list, but actual is " + actualPolyfillName, isPolyfillIndexCorrect);
        }
    }
}
