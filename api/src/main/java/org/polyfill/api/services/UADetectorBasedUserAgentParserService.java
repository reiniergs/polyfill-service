package org.polyfill.api.services;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.VersionNumber;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.polyfill.api.components.LRUCache;
import org.polyfill.api.interfaces.UserAgent;
import org.polyfill.api.interfaces.UserAgentParserService;
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
class UADetectorBasedUserAgentParserService implements UserAgentParserService {

    private static final int MAX_UA_STRING_LENGTH = 300; // real ua string is usually less than 255
    private static final int MAX_NORMALIZED_UA_LENGTH = 22; // XXXXXXXXXX/###.###.### (22 chars)

    private final UserAgentStringParser uaParser = UADetectorServiceFactory.getResourceModuleParser();
    private final Pattern normalizePattern = Pattern.compile("^(\\w+)\\/(\\d+(\\.\\d+(\\.\\d+)?)?)$");
    private final LRUCache<String, UserAgent> cache = new LRUCache<>(5000);

    @Resource(name = "browserAliases")
    private Map<String, Object> browserAliases;

    @Override
    public UserAgent parse(String uaString) {
        uaString = (uaString == null)
            ? "unknown/0.0.0"
            : uaString.substring(0, Math.min(MAX_UA_STRING_LENGTH, uaString.length())).trim();

        String family, versionString;

        // normalized user agent e.g. firefox/1.2.3
        Matcher uaMatcher = normalizePattern.matcher(uaString);
        if (uaString.length() < MAX_NORMALIZED_UA_LENGTH && uaMatcher.find()) {
            family = uaMatcher.group(1);
            versionString = uaMatcher.group(2);

        } else if (cache.containsKey(uaString)) {
            return cache.get(uaString);

        } else {
            ReadableUserAgent readableUA = uaParser.parse(stripIOSWebViewBrowsers(uaString));
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

        UserAgent userAgent = new UserAgentImpl(family, versionString);
        cache.put(uaString, userAgent);

        return userAgent;
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

    /**
     * Implementation of UserAgent interface to store user agent info
     */
    private class UserAgentImpl implements UserAgent {

        private String family;
        private VersionNumber version;

        public UserAgentImpl(String family, String versionString) {
            this.family = family;
            this.version = VersionNumber.parseVersion(versionString);
        }

        @Override
        public String getFamily() {
            return this.family;
        }

        @Override
        public String getVersion() {
            return version.toVersionString();
        }

        @Override
        public String getMajorVersion() {
            return version.getMajor();
        }

        @Override
        public String getMinorVersion() {
            return version.getMinor();
        }

        @Override
        public String toString() {
            return getFamily() + "/" + getVersion();
        }
    }
}
