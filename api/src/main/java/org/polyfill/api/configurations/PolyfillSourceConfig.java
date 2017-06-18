package org.polyfill.api.configurations;

import org.polyfill.api.components.Polyfill;
import org.polyfill.api.components.PolyfillLocationString;
import org.polyfill.api.interfaces.PolyfillConfigLoaderService;
import org.polyfill.api.interfaces.PolyfillLoaderService;
import org.polyfill.api.interfaces.PolyfillLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by smo on 6/6/17.
 * Configurations for polyfills.
 */
@Configuration
public class PolyfillSourceConfig {

    private static final String DEFAULT_POLYFILLS_LOCATION = "polyfills";

    @Autowired(required = false)
    private List<PolyfillLocation> customPolyfillLocations;

    @Autowired
    private PolyfillConfigLoaderService polyfillConfigLoaderService;

    @Autowired
    private PolyfillLoaderService polyfillLoaderService;

    @Bean
    public Map<String, List<String>> aliases() throws IOException, ClassCastException {
        // Just cast here directly, no need to do the casting later.
        // If it fails the cast, we know something is wrong.
        return (Map<String, List<String>>)(Map)polyfillConfigLoaderService.getConfig(
            DEFAULT_POLYFILLS_LOCATION, "aliases.json");
    }

    @Bean
    public Map<String, Object> browserAliases() throws IOException {
        return polyfillConfigLoaderService.getConfig(
            DEFAULT_POLYFILLS_LOCATION, "browserAliases.json");
    }

    @Bean
    public Map<String, String> browserBaselines() throws IOException, ClassCastException {
        // Just cast here directly, no need to do the casting later.
        // If it fails the cast, we know something is wrong.
        return (Map<String, String>)(Map)polyfillConfigLoaderService.getConfig(
            DEFAULT_POLYFILLS_LOCATION, "browserBaselines.json");
    }

    @Bean
    public Map<String, Polyfill> polyfills() throws IOException {
        List<PolyfillLocation> polyfillLocationList = new ArrayList<>();
        // custom polyfill directories
        if (customPolyfillLocations != null && !customPolyfillLocations.isEmpty()) {
            polyfillLocationList.addAll(customPolyfillLocations);
        }
        // add default polyfill directory last to let custom polyfills take priority
        polyfillLocationList.add(new PolyfillLocationString(DEFAULT_POLYFILLS_LOCATION));

        return polyfillLoaderService.loadPolyfills(polyfillLocationList);
    }
}
