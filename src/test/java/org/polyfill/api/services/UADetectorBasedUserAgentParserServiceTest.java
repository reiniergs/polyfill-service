package org.polyfill.api.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.polyfill.api.services.UADetectorBasedUserAgentParserService;
import org.polyfill.api.configurations.MockBrowserAliasesConfig;
import org.polyfill.api.interfaces.UserAgent;
import org.polyfill.api.interfaces.UserAgentParserService;
import org.polyfill.utils.UA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader=AnnotationConfigContextLoader.class,
        classes={MockBrowserAliasesConfig.class,
                UADetectorBasedUserAgentParserService.class
        })
public class UADetectorBasedUserAgentParserServiceTest {

    @Autowired
    private UserAgentParserService uaUtilService;

    @Test
    public void testGeneralUAInfo() {
        UserAgent ua = uaUtilService.parse(UA.CHROME_59);
        assertEquals("Browser family name incorrect", "chrome", ua.getFamily());
        assertEquals("Major version incorrect", "59", ua.getMajorVersion());
        assertEquals("Minor version incorrect", "0", ua.getMinorVersion());
        assertEquals("Version number incorrect", "59.0.3070.0", ua.getVersion());
        assertEquals("toString should be in format family/version", "chrome/59.0.3070.0", ua.toString());
    }

    @Test
    public void testChromeIOSTreatedAsMobileSafari() {
        UserAgent ua = uaUtilService.parse(UA.CHROME_IOS);
        assertEquals("Browser family name incorrect", "ios_saf", ua.getFamily());
        assertEquals("Version number incorrect", "10.0.2", ua.getVersion());
    }

    @Test
    public void testFirefoxIOSTreatedAsMobileSafari() {
        UserAgent ua = uaUtilService.parse(UA.FF_IOS);
        assertEquals("Browser family name incorrect", "ios_saf", ua.getFamily());
        assertEquals("Version number incorrect", "10.1.1", ua.getVersion());
    }

    @Test
    public void testOperaIOSTreatedAsMobileSafari() {
        UserAgent ua = uaUtilService.parse(UA.OPERA_IOS);
        assertEquals("Browser family name incorrect", "ios_saf", ua.getFamily());
        assertEquals("Version number incorrect", "10.1.1", ua.getVersion());
    }

    @Test
    public void testSimpleAlias() {
        UserAgent ua = uaUtilService.parse(UA.FF_NAMOROKA);
        assertEquals("Browser family name incorrect", "firefox", ua.getFamily());
        assertEquals("Version number incorrect", "3.6a2pre", ua.getVersion());
    }

    @Test
    public void testNonAliasedBrowser() {
        UserAgent ua = uaUtilService.parse(UA.AOL);
        assertEquals("Browser family name incorrect", "aol explorer", ua.getFamily());
        assertEquals("Version number incorrect", "9.7", ua.getVersion());
    }

    @Test
    public void testAliasWithMap() {
        UserAgent ua = uaUtilService.parse(UA.YANDEX);
        assertEquals("Browser family name incorrect", "chrome", ua.getFamily());
        assertEquals("Version number incorrect", "32", ua.getVersion());
    }

    @Test
    public void testNormalized() {
        String uaNormalized = "firefox/1.2.3";
        UserAgent ua = uaUtilService.parse(uaNormalized);
        assertEquals("Browser family name incorrect", "firefox", ua.getFamily());
        assertEquals("Version number incorrect", "1.2.3", ua.getVersion());
    }

    @Test
    public void testNormalizedWithAliasFail() {
        String uaNormalizedWithAlias = "mobile safari/1.2.3";
        UserAgent ua = uaUtilService.parse(uaNormalizedWithAlias);
        assertEquals("Browser family name incorrect", "unknown", ua.getFamily());
        assertEquals("Version number incorrect", "", ua.getVersion());
    }
}
