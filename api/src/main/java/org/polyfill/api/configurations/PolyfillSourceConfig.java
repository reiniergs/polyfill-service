package org.polyfill.api.configurations;

import org.polyfill.api.components.Polyfill;
import org.polyfill.api.interfaces.ConfigLoaderService;
import org.polyfill.api.interfaces.PolyfillLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;

/**
 * Created by smo on 6/6/17.
 */
@Configuration
public class PolyfillSourceConfig {

    private static final String POLYFILLS_DIST = "polyfills";

    @Autowired
    private ConfigLoaderService configLoaderService;

    @Autowired
    private PolyfillLoaderService polyfillLoaderService;

    @Bean
    public Map<String, Object> aliases() throws IOException {
        return configLoaderService.getConfig(POLYFILLS_DIST, "aliases.json");
    }

    @Bean
    public Map<String, Object> browserAliases() throws IOException {
        return configLoaderService.getConfig(POLYFILLS_DIST, "browserAliases.json");
    }

    @Bean
    public Map<String, Object> browserBaselines() throws IOException {
        return configLoaderService.getConfig(POLYFILLS_DIST, "browserBaselines.json");
    }

    @Bean
    public Map<String, Polyfill> polyfills() throws IOException {
        return polyfillLoaderService.loadPolyfills(POLYFILLS_DIST);
    }
}
