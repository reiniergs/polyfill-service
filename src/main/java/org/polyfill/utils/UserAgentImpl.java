package org.polyfill.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.uadetector.OperatingSystem;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.VersionNumber;
import net.sf.uadetector.service.UADetectorServiceFactory;

// TODO: cache user agent output
public class UserAgentImpl implements UserAgent {

    // static definitions
    private static final UserAgentStringParser uaParser = UADetectorServiceFactory.getResourceModuleParser();
    private static final Pattern normalizePattern = Pattern.compile("^\\w+\\/\\d+(\\.\\d+(\\.\\d+)?)?$", Pattern.CASE_INSENSITIVE);

    private static final Map<String, String> baselineVersions;
    private static final Map<String, String> aliases;

    // TODO: this is ugly:( need to isolate these constant maps from implementation
    static {
        Map<String, String> baselineVersionsInit  = new HashMap<String, String>();
        baselineVersionsInit.put("ie", ">=7");
        baselineVersionsInit.put("ie_mob", ">=8");
        baselineVersionsInit.put("chrome", "*");
        baselineVersionsInit.put("safari", ">=4");
        baselineVersionsInit.put("ios_saf", ">=4");
        baselineVersionsInit.put("ios_chr", ">=4");
        baselineVersionsInit.put("firefox", ">=3.6");
        baselineVersionsInit.put("firefox_mob", ">=4");
        baselineVersionsInit.put("android", ">=3");
        baselineVersionsInit.put("opera", ">=11");
        baselineVersionsInit.put("op_mob", ">=10");
        baselineVersionsInit.put("op_mini", ">=5");
        baselineVersionsInit.put("bb", ">=6");
        baselineVersionsInit.put("samsung_mob", ">=4");
        baselineVersions = Collections.unmodifiableMap(baselineVersionsInit);

        Map<String, String> aliasesInit  = new HashMap<String, String>();
        aliasesInit.put("blackberry webkit", "bb");
        aliasesInit.put("blackberry", "bb");

        aliasesInit.put("pale moon (firefox variant)", "firefox");
        aliasesInit.put("firefox mobile", "firefox_mob");
        aliasesInit.put("firefox namoroka", "firefox");
        aliasesInit.put("firefox shiretoko", "firefox");
        aliasesInit.put("firefox minefield", "firefox");
        aliasesInit.put("firefox alpha", "firefox");
        aliasesInit.put("firefox beta", "firefox");
        aliasesInit.put("microb", "firefox");
        aliasesInit.put("mozilladeveloperpreview", "firefox");
        aliasesInit.put("iceweasel", "firefox");

        aliasesInit.put("opera tablet", "opera");

        aliasesInit.put("opera mobile", "op_mob");
        aliasesInit.put("opera mini", "op_mini");

        aliasesInit.put("chrome mobile", "chrome");
        aliasesInit.put("chrome frame", "chrome");
        aliasesInit.put("chromium", "chrome");

        aliasesInit.put("ie mobile", "ie_mob");

        aliasesInit.put("ie large screen", "ie");
        aliasesInit.put("internet explorer", "ie");
        aliasesInit.put("edge", "ie");
        aliasesInit.put("edge mobile", "ie");

        aliasesInit.put("uc browser/9.9", "ie/10");

        aliasesInit.put("chrome mobile ios", "ios_chr");

        aliasesInit.put("mobile safari", "ios_saf");
        aliasesInit.put("iphone", "ios_saf");
        aliasesInit.put("iphone simulator", "ios_saf");
        aliasesInit.put("mobile safari uiwebview", "ios_saf");

        aliasesInit.put("facebook", "ios_saf");

        aliasesInit.put("samsung internet", "samsung_mob");

        aliasesInit.put("phantomjs", "safari/5");

        aliasesInit.put("yandex browser/14.10", "chrome/37");
        aliasesInit.put("yandex browser/14.8", "chrome/36");
        aliasesInit.put("yandex browser/14.7", "chrome/35");
        aliasesInit.put("yandex browser/14.5", "chrome/34");
        aliasesInit.put("yandex browser/14.4", "chrome/33");
        aliasesInit.put("yandex browser/14.2", "chrome/32");
        aliasesInit.put("yandex browser/13.12", "chrome/30");
        aliasesInit.put("yandex browser/13.10", "chrome/28");
        aliases = Collections.unmodifiableMap(aliasesInit);
    }

    public static String normalize(String uaString) {
        if (isNormalized(uaString)) {
            return uaString.toLowerCase();
        } else {
            UserAgent ua = new UserAgentImpl(uaString);
            return ua.toString();
        }
    }

    public static Map<String, String> getBaselineVersions() {
        return baselineVersions;
    }

    private static boolean isNormalized(String uaString) {
        return normalizePattern.matcher(uaString).find();
    }

    // instance definitions
    private ReadableUserAgent ua;
    private String family;
    private String majorVersion;
    private String minorVersion;
    private String version;

    public UserAgentImpl(String userAgentString) {
        this.ua = uaParser.parse(stripiOSWebViewBrowsers(userAgentString));
        this.family = this.ua.getName().toLowerCase();

        VersionNumber versionNumber = this.ua.getVersionNumber();
        this.majorVersion = versionNumber.getMajor();
        this.minorVersion = versionNumber.getMinor();
        this.version = versionNumber.toVersionString();

        resolveAlias();
    }
    
    public String getFamily() {
        return this.family;
    }

    public String getMajorVersion() {
        return this.majorVersion;
    }

    public String getMinorVersion() {
        return this.minorVersion;
    }

    public String getVersion() {
        return this.version;
    }

    public boolean isUnknown() {
        return baselineVersions.get(getFamily()) == null;
    }
    
    public boolean meetsBaseline() {
        String baselineRange = baselineVersions.get(getFamily());
        return baselineRange != null && isVersionInRange(baselineRange);
    }

    public boolean satisfies(String range) {
        return isVersionInRange(range) && meetsBaseline();
    }

    public String toString() {
        return getFamily() + "/" + getVersion();
    }

    /**
     * Chrome, Opera, and Firefox on iOS use webview, so stripping them away so that uaDetector
     * falls back to mobile safari and we can map it to ios_saf for more accurate results
     */
    private String stripiOSWebViewBrowsers(String uaString) {
        return uaString.replaceAll("((CriOS|OPiOS)\\/(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)|(FxiOS\\/(\\d+)\\.(\\d+)))", "");
    }

    /**
     * check if user agent version is within range
     * @param range - range of user agent version (e.g. 4 - 6, >=5, <3, *, 10 - *)
     */
    private boolean isVersionInRange(String range) {

        double major = Double.parseDouble(getMajorVersion());

        // *
        if (range.equals("*")) {
            return true;
        }

        // xxx - xxx
        if (range.indexOf('-') > -1) {
            String[] rangeParts = range.split(" - ");
            double min = rangeParts[0].equals("*") ? 0 : Double.parseDouble(rangeParts[0]);
            double max = rangeParts[1].equals("*") ? Double.MAX_VALUE : Double.parseDouble(rangeParts[1]);
            return major >= min && major <= max;
        }

        // </<=/>/>=xxx
        if (range.charAt(0) == '<') {
            if (range.charAt(1) == '=') {
                double max = Double.parseDouble(range.substring(2));
                return major <= max;
            } else {
                double max = Double.parseDouble(range.substring(1));
                return major < max;
            }
        }

        if (range.charAt(0) == '>') {
            if (range.charAt(1) == '=') {
                double max = Double.parseDouble(range.substring(2));
                return major >= max;
            } else {
                double max = Double.parseDouble(range.substring(1));
                return major > max;
            }
        }

        // range is a specific version
        if (range.indexOf('.') > -1) {
            String[] rangeParts = range.split("\\.");
            String[] versionParts = getVersion().split("\\.");
            // if range is more specific than version, version
            // cannot be in range
            if (rangeParts.length > versionParts.length) {
                return false;
            }
            for (int i = 0; i < rangeParts.length; i++) {
                if (!rangeParts[i].equals(versionParts[i])) {
                    return false;
                }
            }
            return true;
        }

        // range only checks for major version
        return getMajorVersion().equals(range);
    }

    /**
     * update the attributes of this class based on the new
     * userAgent string
     * @param userAgent - simple user agent (e.g. ios_saf/10.0.2) to update our attributes
     */
    private void updateUA(String userAgent) {
        String[] aliasParts = userAgent.split("/");
        this.family = aliasParts[0];
        if (aliasParts.length > 1) {
            this.version = aliasParts[1];

            String[] versionParts = aliasParts[1].split("\\.");
            this.majorVersion = versionParts[0];
            if (versionParts.length > 1) this.minorVersion = versionParts[1];
        }
    }

    /**
     * convert ua family to caniuse name equivalents
     */
    private void resolveAlias() {
        // Try getting the alias from the mapping
        // browserName or browserName/Major or browserName/Major.Minor
        String userAgent = aliases.get(getFamily());
        userAgent = userAgent != null ? userAgent : aliases.get(getFamily() + "/" + getMajorVersion());
        userAgent = userAgent != null ? userAgent : aliases.get(getFamily() + "/" + getMajorVersion() + "." + getMinorVersion());

        // if facebook mobile is on iOS, it's using native webview
        if ("facebook".equals(getFamily()) || "ios_saf".equals(userAgent)) {
            OperatingSystem os = this.ua.getOperatingSystem();
            if (os.getFamilyName().toLowerCase().equals("ios")) {
                VersionNumber osVersion = os.getVersionNumber();
                userAgent = userAgent + "/" + osVersion.toVersionString();
            }
        }
        if (userAgent != null) {
            updateUA(userAgent);
        }
    }
}
