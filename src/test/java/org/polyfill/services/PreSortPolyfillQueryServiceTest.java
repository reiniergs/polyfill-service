package org.polyfill.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.polyfill.components.Polyfill;
import org.polyfill.components.UserAgentImpl;
import org.polyfill.configurations.TestConfig;
import org.polyfill.interfaces.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader=AnnotationConfigContextLoader.class,
        classes={TestConfig.class,
                PreSortPolyfillQueryService.class,
                JSONConfigLoaderService.class,
                SemVerUtilService.class})
@ActiveProfiles("test")
public class PreSortPolyfillQueryServiceTest {

    @Autowired
    private PreSortPolyfillQueryService polyfillQueryService;

    @Test
    public void testSearchByUserAgentMeetVersionRequirements() {
        UserAgent chromeUA = new UserAgentImpl("chrome", "53");
        List<Polyfill> polyfills = polyfillQueryService.getPolyfillsByUserAgent(chromeUA);
        String polyfillName;

        int expectedNumOfPolyfills = 4;
        assertEquals("Number of polyfills should be " + expectedNumOfPolyfills,
                expectedNumOfPolyfills, polyfills.size());

        polyfillName = polyfills.get(0).getName();
        assertTrue("b or c should be the 1st polyfill, but actual is " + polyfillName,
                polyfillName.equals("b") || polyfillName.equals("c"));

        polyfillName = polyfills.get(1).getName();
        assertTrue("b or c should be the 2nd polyfill, but actual is " + polyfillName,
                polyfillName.equals("b") || polyfillName.equals("c"));

        polyfillName = polyfills.get(2).getName();
        assertEquals("d.a should be the 3rd polyfill, but actual is " + polyfillName, "d.a", polyfillName);

        polyfillName = polyfills.get(3).getName();
        assertEquals("a should be the 4th polyfill, but actual is " + polyfillName, "a", polyfillName);
    }

    @Test
    public void testSearchByUserAgentSomeNotMeetVersionRequirement() {
        UserAgent userAgent = new UserAgentImpl("firefox", "5");
        List<Polyfill> polyfills = polyfillQueryService.getPolyfillsByUserAgent(userAgent);
        String polyfillName;

        int expectedNumOfPolyfills = 2;
        assertEquals("Number of polyfills should be " + expectedNumOfPolyfills,
                expectedNumOfPolyfills, polyfills.size());

        polyfillName = polyfills.get(0).getName();
        assertEquals("c should be the 1st polyfill, but actual is " + polyfillName, "c", polyfillName);

        polyfillName = polyfills.get(1).getName();
        assertEquals("a should be the 2nd polyfill, but actual is " + polyfillName, "a", polyfillName);
    }

    @Test
    public void testSearchByUserAgentNotMeetBaseline() {
        UserAgent userAgent = new UserAgentImpl("firefox", "1");
        List<Polyfill> polyfills = polyfillQueryService.getPolyfillsByUserAgent(userAgent);
        String polyfillName;

        int expectedNumOfPolyfills = 0;
        assertEquals("Number of polyfills should be " + expectedNumOfPolyfills,
                expectedNumOfPolyfills, polyfills.size());
    }
}
