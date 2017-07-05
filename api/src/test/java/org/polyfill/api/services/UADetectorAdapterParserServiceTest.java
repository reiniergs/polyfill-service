package org.polyfill.api.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.polyfill.api.interfaces.UserAgentParserService;
import org.polyfill.api.utils.UA;

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

    public UADetectorAdapterParserServiceTest(String parsedUaString, Object uaString) {
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
            { "chrome/18.0.0", UA.CHROME_18 },
            { "chrome/59.0.0", UA.CHROME_59 },
            { "ios_saf/10.0.0", UA.CHROME_IOS },
            { "chrome/18.0.0", UA.CHROME_18_ANDROID4 },
            { "firefox/16.0.0", UA.FIREFOX_16 },
            { "ios_saf/10.1.0", UA.FIREFOX_IOS },
            { "firefox/3.6.0", UA.FIREFOX_NAMOROKA },
            { "ios_saf/10.1.0", UA.OPERA_IOS },
            { "opera/12.00.0", UA.OPERA12 },
            { "op_mob/12.02.0", UA.OPERA12_MOBILE },
            { "op_mini/9.80.0", UA.OPERA_MINI },
            { "ie/6.1.0", UA.IE6 },
            { "ie/7.0.0", UA.IE7 },
            { "ie/8.0.0", UA.IE8 },
            { "ie/9.0.0", UA.IE9 },
            { "ie/10.0.0", UA.IE10 },
            { "ie/11.0.0", UA.IE11 },
            { "ie/11.0.0", UA.IE11_NET_FRAMEWORK },
            { "ie_mob/10.0.0", UA.IE_10_LUMIA_928 },
            { "ie_mob/11.0.0", UA.IE_11_LUMIA_928 },
            { "ie/10.0.0", UA.IE10_RT_8 },
            { "ie_mob/11.0.0", UA.IE11_PHONE_8_1 },
            { "ie_mob/11.0.0", UA.IE11_PHONE_8_1_SDK },
            { "ie/11.0.0", UA.IE11_RT_8_1 },
            { "ie/12.10240.0", UA.IE12 },
            { "ie/13.10586.0", UA.IE13 },
            { "ie/14.14332.0", UA.IE14 },
            { "ie/15.15063.0", UA.IE15 },
            { "safari/6.1.0", UA.SAFARI6 },
            { "ios_saf/5.0.0", UA.IPHONE4 },
            { "ios_saf/6.0.0", UA.IPAD },
            { "ios_saf/7.0.0", UA.IPAD_7 },
            { "ios_saf/6.1.0", UA.IPAD_WEBVIEW },
            { "ios_saf/7.1.0", UA.GOOD_IPHONE },
            { "chrome/28.0.0", UA.GOOD_ANDROID },
            { "chrome/33.0.0", UA.NEXUS_10 },
            { "chrome/38.0.0", UA.NEXUS_9_CHROME },
            { "ios_saf/4.3.0", UA.IPOD },
            { "safari/5.1.0", UA.SAFARI5_MAC },
            { "safari/5.0.0", UA.SAFARI5_WINDOWS },
            { "silk/1.1.0", UA.KINDLE_FIRE },
            { "netscape navigator/9.1.0", UA.NETSCAPE },
            { "chrome/32.0.0", UA.YANDEX_14 },
            { "bb/7.2.0", UA.PLAYBOOK },
            { "bb/10.0.0", UA.BLACKBERRY_10 },
            { "bb/7.1.0", UA.BLACKBERRY_7 },

            // FIXME: need to support for SFDC container
            { "android browser/4.0.0", UA.NEXUS_9_SFDC_CONTAINER },
            { "android browser/4.0.0", UA.NEXUS_7_SFDC_CONTAINER },

            // parsed incorrectly, but we don't support the following browsers anyways
            // { "android browser/4.0.0", UA.ANDROID2_3 },
            // { "android browser/3.1.0", UA.ANDROID1_6 },
            // { "nokia web browser/0.0.0", UA.NOKIA_N95 },

            // normalized format
            { "firefox/23.0.0", "firefox/23" },
            // edge cases
            { "unknown/0.0.0", "wrong pattern" },
            { "unknown/0.0.0", null },
            { "unknown/0.0.0", UA.EMPTY }
        });
    }

    @Test
    public void testParsingUserAgent() {
        String actualParsedUaString = service.parse(uaString).toString();
        assertEquals(parsedUaString, actualParsedUaString);
    }
}
