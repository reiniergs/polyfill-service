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
        UserAgent chromeUA = new UserAgentImpl("chrome", "53");
        List<Polyfill> polyfills = polyfillQueryService.getPolyfillsByUserAgent(chromeUA);
        String polyfillName;

        int expectedNumOfPolyfills = 4;
        assertEquals("Number of polyfills should be " + expectedNumOfPolyfills,
                expectedNumOfPolyfills, polyfills.size());

        polyfillName = polyfills.get(0).getName();
        assertTrue("b or c should be the 1st polyfill, but actual is " + polyfillName,
                "b".equals(polyfillName) || "c".equals(polyfillName));

        polyfillName = polyfills.get(1).getName();
        assertTrue("b or c should be the 2nd polyfill, but actual is " + polyfillName,
                "b".equals(polyfillName) || "c".equals(polyfillName));

        polyfillName = polyfills.get(2).getName();
        assertEquals("a should be the 3rd polyfill, but actual is " + polyfillName, "a", polyfillName);

        polyfillName = polyfills.get(3).getName();
        assertEquals("d should be the 4th polyfill, but actual is " + polyfillName, "d", polyfillName);
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

        int expectedNumOfPolyfills = 0;
        assertEquals("Number of polyfills should be " + expectedNumOfPolyfills,
                expectedNumOfPolyfills, polyfills.size());
    }
}
