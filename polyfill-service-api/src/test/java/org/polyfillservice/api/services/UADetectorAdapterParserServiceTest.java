package org.polyfillservice.api.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.polyfillservice.api.interfaces.UserAgentParserService;
import org.polyfillservice.api.utils.UA;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Created by smo on 6/21/17.
 */
@RunWith(Parameterized.class)
public class UADetectorAdapterParserServiceTest {

    // service under test
    private UserAgentParserService service = new UADetectorAdapterParserService();

    private String parsedUaString;
    private String uaString;

    public UADetectorAdapterParserServiceTest(Object uaString, String parsedUaString) {
        this.parsedUaString = parsedUaString;
        if (uaString instanceof UA) {
            this.uaString = ((UA) uaString).getValue();
        } else {
            this.uaString = (String)uaString;
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            { UA.CHROME_18, "chrome/18.0.0" },
            { UA.CHROME_59, "chrome/59.0.0" },
            { UA.CHROME_IOS, "ios_saf/10.0.0" },
            { UA.CHROME_18_ANDROID4, "chrome/18.0.0" },
            { UA.FIREFOX_16, "firefox/16.0.0" },
            { UA.FIREFOX_IOS, "ios_saf/10.1.0" },
            { UA.FIREFOX_NAMOROKA, "firefox/3.6.0" },
            { UA.OPERA_IOS, "ios_saf/10.1.0" },
            { UA.OPERA12, "opera/12.00.0" },
            { UA.OPERA12_MOBILE, "op_mob/12.02.0" },
            { UA.OPERA_MINI, "op_mini/9.80.0" },
            { UA.IE6, "ie/6.1.0" },
            { UA.IE7, "ie/7.0.0" },
            { UA.IE8, "ie/8.0.0" },
            { UA.IE9, "ie/9.0.0" },
            { UA.IE10, "ie/10.0.0" },
            { UA.IE11, "ie/11.0.0" },
            { UA.IE11_NET_FRAMEWORK, "ie/11.0.0" },
            { UA.IE_10_LUMIA_928, "ie_mob/10.0.0" },
            { UA.IE_11_LUMIA_928, "ie_mob/11.0.0" },
            { UA.IE10_RT_8, "ie/10.0.0" },
            { UA.IE11_PHONE_8_1, "ie_mob/11.0.0" },
            { UA.IE11_PHONE_8_1_SDK, "ie_mob/11.0.0" },
            { UA.IE11_RT_8_1, "ie/11.0.0" },
            { UA.IE11_WIN10, "ie/11.0.0" },
            { UA.IE12, "ie/12.10240.0" },
            { UA.IE13, "ie/13.10586.0" },
            { UA.IE14, "ie/14.14332.0" },
            { UA.IE15, "ie/15.15063.0" },
            { UA.SAFARI6, "safari/6.1.0" },
            { UA.IPHONE4, "ios_saf/5.0.0" },
            { UA.IPAD, "ios_saf/6.0.0" },
            { UA.IPAD_7, "ios_saf/7.0.0" },
            { UA.IPAD_WEBVIEW, "ios_saf/6.1.0" },
            { UA.GOOD_IPHONE, "ios_saf/7.1.0" },
            { UA.GOOD_ANDROID, "chrome/28.0.0" },
            { UA.NEXUS_10, "chrome/33.0.0" },
            { UA.NEXUS_9_CHROME, "chrome/38.0.0" },
            { UA.IPOD, "ios_saf/4.3.0" },
            { UA.SAFARI5_MAC, "safari/5.1.0" },
            { UA.SAFARI5_WINDOWS, "safari/5.0.0" },
            { UA.KINDLE_FIRE, "silk/1.1.0" },
            { UA.NETSCAPE, "netscape navigator/9.1.0" },
            { UA.YANDEX_14, "chrome/32.0.0" },
            { UA.PLAYBOOK, "bb/7.2.0" },
            { UA.BLACKBERRY_10, "bb/10.0.0" },
            { UA.BLACKBERRY_7, "bb/7.1.0" },

            // FIXME: need to support for SFDC container
            { UA.NEXUS_9_SFDC_CONTAINER, "android browser/4.0.0" },
            { UA.NEXUS_7_SFDC_CONTAINER, "android browser/4.0.0" },

            // parsed incorrectly, but we don't support the following browsers anyways
            // { UA.ANDROID2_3, "android browser/4.0.0" },
            // { UA.ANDROID1_6, "android browser/3.1.0" },
            // { UA.NOKIA_N95, "nokia web browser/0.0.0" },

            // normalized format
            { "firefox/23", "firefox/23.0.0" },
            // edge cases
            { "wrong pattern", "unknown/0.0.0" },
            { null, "unknown/0.0.0" },
            { UA.EMPTY, "unknown/0.0.0" }
        });
    }

    @Test
    public void testParsingUserAgent() {
        String actualParsedUaString = service.parse(this.uaString).toString();
        assertEquals(this.parsedUaString, actualParsedUaString);
    }
}
