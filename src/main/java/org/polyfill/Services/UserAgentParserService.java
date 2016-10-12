package org.polyfill.Services;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.VersionNumber;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.polyfill.Interfaces.UserAgent;
import org.polyfill.UserAgentImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Created by reinier.guerra on 10/12/16.
 */

@Service
public class UserAgentParserService {

    private final UserAgentStringParser uaParser = UADetectorServiceFactory.getResourceModuleParser();

    public UserAgent parse(String userAgentString) {
        HashMap<String, Object> uaMap = new HashMap<String, Object>();
        ReadableUserAgent ua = uaParser.parse(stripiOSWebViewBrowsers(userAgentString));
        VersionNumber versionNumber = ua.getVersionNumber();

        uaMap.put("family", ua.getName().toLowerCase());
        uaMap.put("version", versionNumber.toVersionString());
        uaMap.put("majorVersion", versionNumber.getMajor());
        uaMap.put("minorVersion", versionNumber.getMinor());
        uaMap.put("operatingSystem", ua.getOperatingSystem());

        return new UserAgentImpl(uaMap);
    }

    /**
     * Chrome, Opera, and Firefox on iOS use webview, so stripping them away so that uaDetector
     * falls back to mobile safari and we can map it to ios_saf for more accurate results
     */
    private String stripiOSWebViewBrowsers(String uaString) {
        return uaString.replaceAll("((CriOS|OPiOS)\\/(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)|(FxiOS\\/(\\d+)\\.(\\d+)))", "");
    }

}
