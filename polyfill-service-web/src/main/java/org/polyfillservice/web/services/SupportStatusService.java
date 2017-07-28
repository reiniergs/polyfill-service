package org.polyfillservice.web.services;

import com.google.common.collect.ImmutableMap;
import org.polyfillservice.api.interfaces.PolyfillService;
import org.polyfillservice.api.interfaces.VersionUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by smo on 7/26/17.
 */
@Service
public class SupportStatusService {

    private static final int STATUS_MISSING = 0;
    private static final int STATUS_POLYFILLED = 1;
    private static final int STATUS_NATIVE = 2;

    private Map<String, List<String>> compatBrowsers =
        ImmutableMap.<String, List<String>>builder()
            .put("chrome", Arrays.asList("35", "40", "42", "46", "48", "58"))
            .put("firefox", Arrays.asList("30", "33", "41", "42", "44", "49", "53"))
            .put("ie", Arrays.asList("7", "8", "9", "10", "11", "13", "14", "15"))
            .put("safari", Arrays.asList("5.1", "6", "7", "8", "9", "10"))
            //.put("android", Arrays.asList("4.4", "5", "5.1", "6", "7", "7.1"))
            //.put("ios_saf", Arrays.asList("4", "5", "6", "7", "8", "9.1", "10.3"))
            .build();

    @Autowired
    private PolyfillService polyfillService;

    @Autowired
    private VersionUtilService versionUtilService;

    public Map<String, Object> querySupportStatusData() {
        Map<String, Object> compatData = new HashMap<>();

        polyfillService.getAllPolyfills().values().stream()
            // hide all the different locales for Intl
            .filter(polyfill -> !polyfill.getName().startsWith("Intl.~locale"))
            // hide private polyfills
            .filter(polyfill -> !polyfill.getName().startsWith("_"))
            .forEach(polyfill -> {
                Map<String, Map<String, Integer>> polyfillCompat = new HashMap<>();
                compatBrowsers.forEach((browserName, versionList) -> {
                    String range = polyfill.getBrowserRequirement(browserName);
                    Map<String, Integer> browserCompat = buildBrowserCompatMap(versionList, range);
                    polyfillCompat.put(browserName, browserCompat);
                });
                compatData.put(polyfill.getName(), polyfillCompat);
            });

        return compatData;
    }

    // Notes: with a more feature-rich version util service, this impl might become simpler
    private Map<String, Integer> buildBrowserCompatMap(List<String> versionList, String range) {
        Map<String, Integer> browserCompat = new HashMap<>();

        // assuming version list is in ascending order, state will go from
        // missing -> polyfilled -> native
        // when range doesn't exist, then it's assumed feature is natively supported
        // when all missing, that means natively supported, no need for polyfills
        int status = range == null ? STATUS_NATIVE : STATUS_MISSING;
        boolean allMissing = true;
        for (String version : versionList) {
            if (status == STATUS_MISSING) {
                // 1st state, outside of range
                if (!versionUtilService.isVersionInRange(version, range)) {
                    browserCompat.put(version, STATUS_MISSING);
                }
                // entering 2nd state, moving into the supported range
                else {
                    browserCompat.put(version, STATUS_POLYFILLED);
                    status = STATUS_POLYFILLED;
                    allMissing = false;
                }
            } else if (status == STATUS_POLYFILLED) {
                // 2nd state, still in supported range
                if (versionUtilService.isVersionInRange(version, range)) {
                    browserCompat.put(version, STATUS_POLYFILLED);
                }
                // entering 3rd state, moving out of supported range because
                // it's already supported natively
                else {
                    browserCompat.put(version, STATUS_NATIVE);
                    status = STATUS_NATIVE;
                }
            }
            else { // 3rd state, natively supported
                browserCompat.put(version, STATUS_NATIVE);
            }
        }

        if (allMissing) {
            for (String version : browserCompat.keySet()) {
                browserCompat.put(version, STATUS_NATIVE);
            }
        }

        return browserCompat;
    }
}
