package org.polyfillservice.api.services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.VersionNumber;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.polyfillservice.api.components.LRUCache;
import org.polyfillservice.api.interfaces.UserAgent;
import org.polyfillservice.api.interfaces.UserAgentParserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by smo
 * Service to parse user agent string.
 */
@Service("uadetector")
class UADetectorAdapterParserService implements UserAgentParserService {

    private static final int MAX_UA_STRING_LENGTH = 300; // real ua string is usually less than 255
    private static final int MAX_NORMALIZED_UA_LENGTH = 22; // XXXXXXXXXX/###.###.### (22 chars)

    private final LRUCache<String, UserAgent> cache = new LRUCache<>(5000);
    private final UserAgentStringParser uaParser = UADetectorServiceFactory.getResourceModuleParser();
    private final Pattern normalizePattern = Pattern.compile("^(\\w+)\\/(\\d+(\\.\\d+(\\.\\d+)?)?)$");
    private final Pattern simpleVersion = Pattern.compile("Version/([0-9a-z\\+\\-\\.]+)");
    private final UserAgent unknownUA = new UserAgentImpl("unknown", "0.0.0");
    private final UserAgentMapper uaMapper = new UserAgentMapper();

    @Override
    public UserAgent parse(String uaString) {
        if (uaString == null) {
            return this.unknownUA;
        }

        uaString = uaString.trim();

        // case 1: normalized ua string e.g. firefox/1.2.3
        UserAgent normalizedUA = parseNormalized(uaString);
        if (normalizedUA != null) {
            return normalizedUA;
        }

        // preprocess uaString before continuing to case 2 and 3
        uaString = preprocessUserAgentString(uaString);

        // case 2: we've already parsed this one before, get from cache
        if (this.cache.containsKey(uaString)) {
            return this.cache.get(uaString);
        }

        // case 3: parse using library and our mapper
        ReadableUserAgent readableUA = this.uaParser.parse(uaString);
        String uaVersion = getVersionString(readableUA.getVersionNumber());
        String osVersion = getVersionString(readableUA.getOperatingSystem().getVersionNumber());

        // many weird user agent string has Version/xx.xx.xx, which the library might miss.
        // in this case we do a simple version matching and see if we can find something.
        if ("0.0".equals(uaVersion)) {
            String simpleVersion = getSimpleVersion(uaString);
            if (simpleVersion != null) {
                uaVersion = simpleVersion;
            }
        }

        UserAgent userAgent = this.uaMapper.resolve(readableUA.getName(), uaVersion, osVersion);

        this.cache.put(uaString, userAgent);
        return userAgent;
    }

    private UserAgent parseNormalized(String uaString) {
        // use regex to split them
        if (uaString.length() < MAX_NORMALIZED_UA_LENGTH) {
            Matcher uaMatcher = this.normalizePattern.matcher(uaString);
            if (uaMatcher.find()) {
                String family = uaMatcher.group(1);
                String versionString = uaMatcher.group(2);
                return new UserAgentImpl(family, versionString);
            }
        }
        return null;
    }

    private String preprocessUserAgentString(String uaString) {
        // keep ua string's length reasonable to guard performance
        uaString = uaString.substring(0, Math.min(MAX_UA_STRING_LENGTH, uaString.length()));
        // Chrome, Opera, and Firefox on iOS use webview, so stripping them away so that uaDetector
        // falls back to mobile safari and we can map it to ios_saf for more accurate results
        uaString = uaString.replaceAll("((CriOS|OPiOS)\\/(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)|(FxiOS\\/(\\d+)\\.(\\d+)))", "");
        // Microsoft Edge spoof itself as Chrome and Safari. Strip those and replace edge with msie
        // so that it's recognized as IE 12+
        uaString = uaString.replaceAll("Chrome.+Edge/", "msie ");

        return uaString;
    }

    private String getVersionString(VersionNumber vn) {
        String major = vn.getMajor().isEmpty() ? "0" : vn.getMajor();
        String minor = vn.getMinor().isEmpty() ? "0" : vn.getMinor();
        return major + "." + minor;
    }

    private String getSimpleVersion(String uaString) {
        Matcher matcher = this.simpleVersion.matcher(uaString);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Implementation of UserAgent interface to store user agent info
     */
    private class UserAgentImpl implements UserAgent {

        private String family;
        private VersionNumber version;

        private UserAgentImpl(String family, String versionString) {
            this.family = family;
            this.version = VersionNumber.parseVersion(zeroPatchVersion(versionString));
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

        /**
         * Remove everything after minor version for cacheability.
         * Those version groups don't affect polyfills anyways.
         * @param versionString version string to process
         * @return a new version string with patch version set to 0
         */
        private String zeroPatchVersion(String versionString) {
            // add two zeros in case minor and patch are missing
            versionString = versionString.isEmpty() ? "0.0.0" : versionString + ".0.0";
            VersionNumber versionNumber = VersionNumber.parseVersion(versionString);
            return versionNumber.getMajor() + "." + versionNumber.getMinor() + ".0";
        }
    }

    /**
     * Aliases used to remap the parsed result of the parser library to what we use
     * in the polyfill meta files.
     */
    private class UserAgentMapper {
        private Map<String, Object> aliases = ImmutableMap.<String, Object>builder()
            .put("blackberry webkit", "bb")
            .put("blackberry", "bb")
            .put("blackberry os", "bb")
            .put("blackberry browser", "bb")
            .put("pale moon (firefox variant)", "firefox")
            .put("firefox mobile", "firefox_mob")
            .put("firefox (namoroka)", "firefox")
            .put("firefox shiretoko", "firefox")
            .put("firefox minefield", "firefox")
            .put("firefox alpha", "firefox")
            .put("firefox beta", "firefox")
            .put("microb", "firefox")
            .put("mozilladeveloperpreview", "firefox")
            .put("iceweasel", "firefox")
            .put("opera tablet", "opera")
            .put("opera mobile", "op_mob")
            .put("opera mini", "op_mini")
            .put("chrome mobile", "chrome")
            .put("chrome frame", "chrome")
            .put("chromium", "chrome")
            .put("ie mobile", "ie_mob")
            .put("ie large screen", "ie")
            .put("internet explorer", "ie")
            .put("chrome mobile ios", "ios_chr")
            .put("mobile safari", "ios_saf")
            .put("iphone", "ios_saf")
            .put("iphone simulator", "ios_saf")
            .put("mobile safari uiwebview", "ios_saf")
            .put("samsung internet", "samsung_mob")
            .put("uc browser", ImmutableMap.builder()
                .put("9.9", ImmutableList.of("ie", "10"))
                .build()
            )
            .put("yandex.browser", ImmutableMap.builder()
                .put("14.10", ImmutableList.of("chrome", "37.0"))
                .put("14.8",  ImmutableList.of("chrome", "36.0"))
                .put("14.7",  ImmutableList.of("chrome", "35.0"))
                .put("14.5",  ImmutableList.of("chrome", "34.0"))
                .put("14.4",  ImmutableList.of("chrome", "33.0"))
                .put("14.2",  ImmutableList.of("chrome", "32.0"))
                .put("13.12", ImmutableList.of("chrome", "30.0"))
                .put("13.10", ImmutableList.of("chrome", "28.0"))
                .build()
            )
            .build();

        public UserAgent resolve(String family, String version, String osVersion) {
            family = family.toLowerCase();

            Object mapObject = this.aliases.get(family); // temp object used for map traversal
            if (mapObject instanceof Map) {
                // e.g. "yandex browser": {"14.10": ["chrome", "37"], ...}
                Map aliasMap = (Map)mapObject;
                mapObject = aliasMap.get(version);
                if (mapObject instanceof  List) {
                    List uaGroups = (List)mapObject;
                    family = (String)(uaGroups.get(0));
                    version = (String)(uaGroups.get(1));
                }
            } else if (mapObject instanceof String) {
                family = (String)mapObject;
                if ("ios_saf".equals(family)) {
                    version = osVersion;
                }
            }

            return new UserAgentImpl(family, version);
        }
    }
}
