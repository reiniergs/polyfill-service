package org.polyfill.utils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.polyfill.Interfaces.UserAgent;
import org.polyfill.Services.UserAgentParserService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by smo on 10/11/16.
 */
public class UserAgentImplTest {

    private final static Map<String, String> UAStrings;

    static {
        UAStrings = new HashMap<String, String>();
        UAStrings.put("chrome0", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36");
        UAStrings.put("chrome1", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36");
        UAStrings.put("safari", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/7046A194A");
        UAStrings.put("unknown", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) NewBrowser/53.0.2785.116 Yoyo/537.36");
        UAStrings.put("edge", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246");
        UAStrings.put("lumia950", "Mozilla/5.0 (Windows Phone 10.0; Android 4.2.1; Microsoft; Lumia 950) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Mobile Safari/537.36 Edge/13.10586");

        UAStrings.put("chrome_ios", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0_2 like Mac OS X) AppleWebKit/601.1 (KHTML, like Gecko) CriOS/53.0.2785.109 Mobile/14A456 Safari/601.1.46");
        UAStrings.put("safari_ios", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0_2 like Mac OS X) AppleWebKit/602.1.50 (KHTML, like Gecko) Version/10.0 Mobile/14A456 Safari/602.1");

        UAStrings.put("ie7", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; chromeframe/12.0.742.100)");
        UAStrings.put("ie6", "Mozilla/4.0 (compatible; MSIE 6.1; Windows XP; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");

        UAStrings.put("firefox25", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0");
        UAStrings.put("firefox24", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:24.0) Gecko/20100101 Firefox/24.0");
        UAStrings.put("firefox3_5", "Mozilla/5.0 (X11;U; Linux i686; en-GB; rv:1.9.1) Gecko/20090624 Ubuntu/9.04 (jaunty) Firefox/3.5");
    }

    private UserAgentParserService userAgentParserService;

    @Before
    public void setup() {
        userAgentParserService = new UserAgentParserService();
    }

    @Test
    public void testGeneralUAInfo() {
        UserAgent ua = userAgentParserService.parse(UAStrings.get("chrome0"));
        assertEquals("Browser family name incorrect", "chrome", ua.getFamily());
        assertEquals("Major version incorrect", "41", ua.getMajorVersion());
        assertEquals("Minor version incorrect", "0", ua.getMinorVersion());
        assertEquals("Version number incorrect", "41.0.2227.0", ua.getVersion());
        assertEquals("User Agent should not be unknown", false, ua.isUnknown());
        assertEquals("toString should be in format family/version", "chrome/41.0.2227.0", ua.toString());
    }

    @Test
    public void testUnknownBrowser() {
        UserAgent ua = userAgentParserService.parse(UAStrings.get("unknown"));
        assertEquals("User Agent should be unknown", true, ua.isUnknown());
    }

    @Test
    public void testMeetsBaseline() {
        UserAgent ua = userAgentParserService.parse(UAStrings.get("ie7"));
        assertEquals("IE7 should meet the baseline", true, ua.meetsBaseline());
    }

    @Test
    public void testNotMeetsBaseline() {
        UserAgent ua = userAgentParserService.parse(UAStrings.get("ie6"));
        assertEquals("IE6 should not meet the baseline", false, ua.meetsBaseline());
    }

    @Ignore
    @Test
    public void testIOSChrome() {
        UserAgent ua = userAgentParserService.parse(UAStrings.get("chrome_ios"));
        assertEquals("Browser family name incorrect", "ios_saf", ua.getFamily());
        assertEquals("Version number incorrect", "10.0.2", ua.getVersion());
    }

    @Test
    public void testSatisfiesWithMinMaxRange() {
        UserAgent uaFF25 = userAgentParserService.parse(UAStrings.get("firefox25"));
        UserAgent uaFF24 = userAgentParserService.parse(UAStrings.get("firefox24"));
        UserAgent uaFF3_5 = userAgentParserService.parse(UAStrings.get("firefox3_5"));
        String range = "3.0 - 24";

        assertSatisfies(false, range, uaFF25);
        assertSatisfies(true, range, uaFF24);
        assertSatisfies(false, "since it doesn't meet baseline", range, uaFF3_5);
    }

    @Test
    public void testSatisfiesWithStar() {
        UserAgent uaFF25 = userAgentParserService.parse(UAStrings.get("firefox25"));
        UserAgent uaFF3_5 = userAgentParserService.parse(UAStrings.get("firefox3_5"));
        String range = "*";

        assertSatisfies(true, range, uaFF25);
        assertSatisfies(false, range, uaFF3_5);
    }

    @Test
    public void testSatisfiesWithMinStarRange() {
        UserAgent uaFF25 = userAgentParserService.parse(UAStrings.get("firefox25"));
        UserAgent uaFF24 = userAgentParserService.parse(UAStrings.get("firefox24"));
        String range = "25 - *";

        assertSatisfies(true, range, uaFF25);
        assertSatisfies(false, range, uaFF24);
    }

    @Test
    public void testSatisfiesWithStarMaxRange() {
        UserAgent uaFF25 = userAgentParserService.parse(UAStrings.get("firefox25"));
        UserAgent uaFF24 = userAgentParserService.parse(UAStrings.get("firefox24"));
        String range = "* - 24";

        assertSatisfies(false, range, uaFF25);
        assertSatisfies(true, range, uaFF24);
    }

    @Test
    public void testSatisfiesWithLessThan() {
        UserAgent uaFF25 = userAgentParserService.parse(UAStrings.get("firefox25"));
        UserAgent uaFF24 = userAgentParserService.parse(UAStrings.get("firefox24"));
        String range = "<25";

        assertSatisfies(false, range, uaFF25);
        assertSatisfies(true, range, uaFF24);
    }

    @Test
    public void testSatisfiesWithLessThanOrEqualTo() {
        UserAgent uaFF25 = userAgentParserService.parse(UAStrings.get("firefox25"));
        UserAgent uaFF24 = userAgentParserService.parse(UAStrings.get("firefox24"));
        String range = "<=24";

        assertSatisfies(false, range, uaFF25);
        assertSatisfies(true, range, uaFF24);
    }

    @Test
    public void testSatisfiesWithGreaterThan() {
        UserAgent uaFF25 = userAgentParserService.parse(UAStrings.get("firefox25"));
        UserAgent uaFF24 = userAgentParserService.parse(UAStrings.get("firefox24"));
        String range = ">24";

        assertSatisfies(true, range, uaFF25);
        assertSatisfies(false, range, uaFF24);
    }

    @Test
    public void testSatisfiesWithGreaterThanOrEqualTo() {
        UserAgent uaFF25 = userAgentParserService.parse(UAStrings.get("firefox25"));
        UserAgent uaFF24 = userAgentParserService.parse(UAStrings.get("firefox24"));
        String range = ">=25";

        assertSatisfies(true, range, uaFF25);
        assertSatisfies(false, range, uaFF24);
    }

    @Test
    public void testSatisfiesWithOnlyMajorVersion() {
        UserAgent uaFF25 = userAgentParserService.parse(UAStrings.get("firefox25"));
        UserAgent uaFF24 = userAgentParserService.parse(UAStrings.get("firefox24"));
        String range = "25";

        assertSatisfies(true, range, uaFF25);
        assertSatisfies(false, range, uaFF24);
    }

    @Test
    public void testSatisfiesWithExactVersion() {
        UserAgent uaChrome0 = userAgentParserService.parse(UAStrings.get("chrome0"));
        UserAgent uaChrome1 = userAgentParserService.parse(UAStrings.get("chrome1"));
        String range = "41.0.2227.1";

        assertSatisfies(false, range, uaChrome0);
        assertSatisfies(true, range, uaChrome1);
    }

    /****************************************************************
     * Helper Functions
     ****************************************************************/
    private void assertSatisfies(boolean isSatisfied, String range, UserAgent ua) {
        assertSatisfies(isSatisfied, "", range, ua);
    }

    private void assertSatisfies(boolean isSatisfied, String reason, String range, UserAgent ua) {
        String not = isSatisfied ? " " : " not ";
        assertEquals("When range is " + range + ", " + ua.getFamily() + "/" + ua.getVersion() + " should" + not + "be accepted " + reason,
                isSatisfied, ua.satisfies(range));
    }
}
