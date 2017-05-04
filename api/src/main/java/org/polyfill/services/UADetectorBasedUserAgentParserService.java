package org.polyfill.services;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.VersionNumber;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.polyfill.components.UserAgentImpl;
import org.polyfill.interfaces.UserAgent;
import org.polyfill.interfaces.UserAgentParserService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by smo
 * Service to parse user agent string.
 */
@Service("uadetector")
@Primary
class UADetectorBasedUserAgentParserService implements UserAgentParserService {

    private final UserAgentStringParser uaParser = UADetectorServiceFactory.getResourceModuleParser();
    private final Pattern normalizePattern = Pattern.compile("^(\\w+)\\/(\\d+(\\.\\d+(\\.\\d+)?)?)$");

    @Resource(name = "browserAliases")
    private Map<String, Object> browserAliases;

    @Override
    public UserAgent parse(String userAgentString) {
        userAgentString = userAgentString.trim();

        String family, versionString;

        // normalized user agent e.g. firefox/1.2.3
        Matcher uaMatcher = normalizePattern.matcher(userAgentString);
        if (uaMatcher.find()) {
            family = uaMatcher.group(1);
            versionString = uaMatcher.group(2);

        // normal user agent, need to parse
        } else {
            ReadableUserAgent readableUA = uaParser.parse(stripIOSWebViewBrowsers(userAgentString));
            VersionNumber version = readableUA.getVersionNumber();
            family = readableUA.getName().toLowerCase();
            versionString = version.toVersionString();

            // resolve alias
            Object aliasObj = this.browserAliases.get(family);
            if (aliasObj != null) {
                if (aliasObj instanceof Map) {
                    // e.g. "yandex browser": {"14.10": ["chrome", "37"], ...}
                    String versionKey = version.getMajor() + "." + version.getMinor();
                    List userAgentAliasGroups = (List)((Map)aliasObj).get(versionKey);
                    if (userAgentAliasGroups != null) {
                        family = (String)userAgentAliasGroups.get(0);
                        versionString = (String)userAgentAliasGroups.get(1);
                    }
                } else if (aliasObj instanceof String) {
                    // "chrome mobile ios": "ios_chr"
                    family = (String)aliasObj;
                    if ("ios_saf".equals(family)) {
                        VersionNumber osVersion = readableUA.getOperatingSystem().getVersionNumber();
                        versionString = osVersion.toVersionString();
                    }
                }
            }
        }

        return new UserAgentImpl(family, versionString);
    }

    /**
     * Chrome, Opera, and Firefox on iOS use webview, so stripping them away so that uaDetector
     * falls back to mobile safari and we can map it to ios_saf for more accurate results
     * @param uaString user agent string
     * @return user agent string with CriOS/OPiOS/FxiOS removed
     */
    private String stripIOSWebViewBrowsers(String uaString) {
        return uaString.replaceAll("((CriOS|OPiOS)\\/(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)|(FxiOS\\/(\\d+)\\.(\\d+)))", "");
    }
}
