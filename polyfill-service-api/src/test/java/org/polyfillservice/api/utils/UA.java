package org.polyfillservice.api.utils;

/**
 * Created by smo on 6/20/17.
 */
public enum UA {
    // chrome
    // ------------------------------
    CHROME_18("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19"),
    CHROME_59("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3070.0 Safari/537.36"),
    CHROME_IOS("Mozilla/5.0 (iPhone; CPU iPhone OS 10_0_2 like Mac OS X) AppleWebKit/601.1 (KHTML, like Gecko) CriOS/53.0.2785.109 Mobile/14A456 Safari/601.1.46"),
    CHROME_18_ANDROID4("Mozilla/5.0 (Linux; Android 4.2.1; Nexus 4 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19"),

    // firefox
    // ------------------------------
    FIREFOX_16("Mozilla/6.0 (Windows NT 6.2; WOW64; rv:16.0.1) Gecko/20121011 Firefox/16.0.1"),
    FIREFOX_IOS("Mozilla/5.0 (iPhone; CPU iPhone OS 10_1_1 like Mac OS X) AppleWebKit/602.2.14 (KHTML, like Gecko) FxiOS/5.3 Mobile/14B100 Safari/602.2.14"),
    FIREFOX_NAMOROKA("Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2a2pre) Gecko/20090908 Ubuntu/9.04 (jaunty) Namoroka/3.6a2pre GTB5 (.NET CLR 3.5.30729)"),

    // opera
    // ------------------------------
    OPERA_IOS("Mozilla/5.0 (iPhone; CPU iPhone OS 10_1_1 like Mac OS X) AppleWebKit/602.2.14 (KHTML, like Gecko) OPiOS/14.0.0.104835 Mobile/14B100 Safari/9537.53"),
    OPERA12("Opera/12.0(Windows NT 5.2;U;en)Presto/22.9.168 Version/12.00"),
    OPERA12_MOBILE("Opera/12.02 (Android 4.1; Linux; Opera Mobi/ADR-1111101157; U; en-US) Presto/2.9.201 Version/12.02"),
    OPERA_MINI("Opera/9.80 (J2ME/MIDP; Opera Mini/9.80 (S60; SymbOS; Opera Mobi/23.348; U; en) Presto/2.5.25 Version/10.54"),

    // ie
    // ------------------------------
    IE6("Mozilla/4.0 (compatible; MSIE 6.1; Windows XP; .NET CLR 1.1.4322; .NET CLR 2.0.50727)"),
    IE7("Mozilla/5.0 (Windows; U; MSIE 7.0; Windows NT 6.0; en-US)"),
    IE8("Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; Media Center PC 4.0; SLCC1; .NET CLR 3.0.04320)"),
    IE9("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 7.1; Trident/5.0)"),
    IE10("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)"),
    IE11("Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko"),
    IE11_NET_FRAMEWORK("Mozilla/5.0 (Windows NT 6.3; Trident/7.0; .NET4.0E; .NET4.0C; rv:11.0) like Gecko"),

    IE_10_LUMIA_928("Mozilla/5.0 (compatible; MSIE 10.0; Windows Phone 8.0; Trident/6.0; IEMobile/10.0; ARM; Touch; NOKIA; Lumia 928)"),
    IE_11_LUMIA_928("Mozilla/5.0 (Windows Phone 8.1; ARM; Trident/7.0; Touch; rv:11; IEMobile/11.0; NOKIA; Lumia 928) like Gecko"),
    IE10_RT_8("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; ARM; Trident/6.0 Touch)"),
    IE11_PHONE_8_1("Mozilla/5.0 (Windows Phone 8.1; ARM; Trident/7.0;Touch; rv:11.0; IEMobile/11.0; Microsoft; Virtual) like Gecko"),
    IE11_PHONE_8_1_SDK("Mozilla/5.0 (Windows Phone 8.1; ARM; Trident/7.0; Touch; rv:11; IEMobile/11.0) like Android 4.1.2; compatible) like iPhone OS 7_0_3 Mac OS X WebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.99 Mobile Safari /537.36"),
    IE11_RT_8_1("Mozilla/5.0 (compatible; MSIE 11.0; Windows NT 6.3; ARM; Trident/7.0)"),
    IE11_WIN10("Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko"),

    // Microsoft Edge
    IE12("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.10240"),
    IE13("Mozilla/5.0 (Windows Phone 10.0; Android 4.2.1; Microsoft MDG; Lumia 630 Dual SIM) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Mobile Safari/537.36 Edge/13.10586"),
    IE14("Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.82 Safari/537.36 Edge/14.14332"),
    IE15("Mozilla/5.0 (Windows Phone 10.0; Android 6.0.1; NuAns; NEO) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Mobile Safari/537.36 Edge/15.15063"),

    // safari
    // ------------------------------
    SAFARI6("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.73.11 (KHTML, like Gecko) Version/6.1.1 Safari/537.73.11"),
    IPHONE4("Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3"),
    IPAD("Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5355d Safari/8536.25"),
    IPAD_7("Mozilla/5.0 (iPad; CPU OS 7_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53"),
    IPAD_WEBVIEW("Mozilla/5.0 (iPad; CPU OS 6_1 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Mobile/10B141"),

    // Blackberry Z10
    BLACKBERRY_10("Mozilla/5.0 (BB10; Touch) AppleWebKit/537.10+ (KHTML, like Gecko) Version/10.0.9.2372 Mobile Safari/537.10+"),
    GOOD_IPHONE("Mozilla/5.0 (iPhone; CPU iPhone OS 7_1 like Mac OS X) AppleWebKit/.537.51.2 (KHTML, like Gecko) Mobile/11D167 Safari/8536.25 GoodAccess/1.1.333.302"),
    GOOD_ANDROID("Mozilla/5.0 (Linux; Android 4.1.1; SAMSUNG-SGH-1747 Build/JRO03L) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.45 Mobile Safari/537.36 Good Access/1.0.21.304"),
    NEXUS_10("Mozilla/5.0 (Linux; Android 4.4.2; Nexus 10 Build/KOT49H) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.136 Safari/537.36"),
    NEXUS_9_SFDC_CONTAINER("SalesforceMobileSDK/2.2.3 android mobile/5.0 (Nexus 9) Salesforce1/7.0 Native XBranding/1.0 SalesforceTouchContainer/2.0 Mozilla/5.0 (Linux; Android 5.0; Nexus 9 Build/LRX21L) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/37.0.0.0 Safari/537.36"),
    NEXUS_9_CHROME("Mozilla/5.0 (Linux; Android 5.0; Nexus 9 Build/LRX21L) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.509 Safari/537.36"),
    NEXUS_7_SFDC_CONTAINER("SalesforceMobileSDK/2.2.3 android mobile/4.4.4 (Nexus 7) Salesforce1/7.0 Native XBranding/1.0 SalesforceTouchContainer/2.0 Mozilla/5.0 (Linux; Android 4.4.4; Nexus 7 Build/KTU84P) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 Safari/537.36"),
    IPOD("Mozilla/5.0 (iPod; U; CPU iPhone OS 4_3_3 like Mac OS X; ja-jp) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5"),
    SAFARI5_MAC("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/537.13+ (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2"),
    SAFARI5_WINDOWS("Mozilla/5.0 (Windows; U; Windows NT 6.1; tr-TR) AppleWebKit/533.20.25 (KHTML, like Gecko) Version/5.0.4 Safari/533.20.27"),

    ANDROID2_3("Mozilla/5.0 (Linux; U; Android 2.3.5; en-us; HTC Vision Build/GRI40) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1"),
    ANDROID1_6("Mozilla/5.0 (Linux; U; Android 1.6; ar-us; SonyEricssonX10i Build/R2BA026) AppleWebKit/528.5+ (KHTML, like Gecko) Version/3.1.2 Mobile Safari/525.20.1"),
    KINDLE_FIRE("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_3; en-us; Silk/1.1.0-84) AppleWebKit/533.16 (KHTML, like Gecko) Version/5.0 Safari/533.16 Silk-Accelerated=true"),
    PLAYBOOK("Mozilla/5.0 (PlayBook; U; RIM Tablet OS 2.0.0; en-US) AppleWebKit/535.8+ (KHTML, like Gecko) Version/7.2.0.0 Safari/535.8+"),
    NOKIA_N95("Mozilla/5.0 (SymbianOS/9.2; U; Series60/3.1 NokiaN95/10.0.018; Profile/MIDP-2.0 Configuration/CLDC-1.1 ) AppleWebKit/413 (KHTML, like Gecko) Safari/413"),
    BLACKBERRY_7("Mozilla/5.0 (BlackBerry; U; BlackBerry 9900; en) AppleWebKit/534.11+ (KHTML, like Gecko) Version/7.1.0.346 Mobile Safari/534.11+"),
    NETSCAPE("Mozilla/5.0 (Windows; U; Win 9x 4.90; SG; rv:1.9.2.4) Gecko/20101104 Netscape/9.1.0285"),

    // yandex
    // ------------------------------
    YANDEX_14("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.102 YaBrowser/14.2.1700.12508 Safari/537.36"),

    EMPTY("");

    private String userAgentString;

    private UA(String agentString) {
        this.userAgentString = agentString;
    }

    public String getValue() {
        return userAgentString;
    }
}
