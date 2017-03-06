package org.polyfill.services;

import net.sf.uadetector.OperatingSystem;
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

/**
 * Created by smo
 * Service to parse user agent string.
 */
@Service("uadetector")
@Primary
public class UADetectorBasedUserAgentParserService implements UserAgentParserService {

    private final UserAgentStringParser uaParser = UADetectorServiceFactory.getResourceModuleParser();

    @Resource(name = "browserAliases")
    private Map<String, Object> browserAliases;

    @Override
    public UserAgent parse(String userAgentString) {
        ReadableUserAgent readableUA = uaParser.parse(stripIOSWebViewBrowsers(userAgentString));
        String family = readableUA.getName().toLowerCase();
        VersionNumber version = readableUA.getVersionNumber();
        OperatingSystem os = readableUA.getOperatingSystem();
        String[] userAgentAlias = getUserAgentAlias(family, version);

        if (userAgentAlias == null) {
            // unsupported browser or browser that already uses acceptable names, return as is
            return new UserAgentImpl(family, version);

        } else if (userAgentAlias.length == 1) {
            // common case, user caniuse family name
            if (userAgentAlias[0].equals("ios_saf")) {
                // ios webview version uses os version
                return new UserAgentImpl(userAgentAlias[0], os.getVersionNumber());
            } else {
                return new UserAgentImpl(userAgentAlias[0], version);
            }

        } else {
            // some browsers use another browser's engine, so use the config info for user agent
            return new UserAgentImpl(userAgentAlias[0], userAgentAlias[1]);
        }
    }

    /**
     * Chrome, Opera, and Firefox on iOS use webview, so stripping them away so that uaDetector
     * falls back to mobile safari and we can map it to ios_saf for more accurate results
     */
    private String stripIOSWebViewBrowsers(String uaString) {
        return uaString.replaceAll("((CriOS|OPiOS)\\/(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)|(FxiOS\\/(\\d+)\\.(\\d+)))", "");
    }

    private String[] getUserAgentAlias(String family, VersionNumber version) {
        String[] userAgentAlias = null;
        Object alias = this.browserAliases.get(family);
        if (alias instanceof Map) {
            String versionKey = version.getMajor() + "." + version.getMinor();
            List userAgentAliasGroups = (List)((Map)alias).get(versionKey);
            userAgentAlias = new String[2];
            userAgentAlias[0] = (String)userAgentAliasGroups.get(0);
            userAgentAlias[1] = (String)userAgentAliasGroups.get(1);
        } else if (alias instanceof String) {
            userAgentAlias = new String[1];
            userAgentAlias[0] = (String)alias;
        }
        return userAgentAlias;
    }
}
