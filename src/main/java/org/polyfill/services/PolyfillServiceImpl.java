package org.polyfill.services;

import org.polyfill.interfaces.PolyfillService;
import org.polyfill.interfaces.SourceService;
import org.polyfill.interfaces.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by reinier.guerra on 12/5/16.
 */
@Service
public class PolyfillServiceImpl implements PolyfillService {

    @Autowired
    UserAgentParserService userAgentParserService;

    @Autowired
    SourceService sourceService;

    public String getPolyfillString(Map params) {
        UserAgent ua = (UserAgent) params.get("ua");
        String uaDebugName = ua.getFamily() + "/" + ua.getVersion() + (ua.isUnknown() || !ua.meetsBaseline() ? "(unknown/unsupported;)" : "");
        String lf = (Boolean) params.get("minify") ? "" : "\n";
        String allWarnText = "Using the `all` alias with polyfill.io is a very bad idea. In a future version of the service, `all` will deliver the same behaviour as `default`, so we recommend using `default` instead.";

        Boolean unsupportedUA = !ua.meetsBaseline() || ua.isUnknown() && params.get("unknown") != "polyfill";

        List<String> targetedFeatures = getPolyfills(params);

        return "alert('OK')";
    }

    private List<String> getPolyfills(Map params) {
        UserAgent ua = (UserAgent) params.get("ua");
        return null;
    }
}
