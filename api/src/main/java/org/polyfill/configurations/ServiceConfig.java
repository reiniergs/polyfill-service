package org.polyfill.configurations;

import org.polyfill.components.Polyfill;
import org.polyfill.interfaces.ConfigLoaderService;
import org.polyfill.interfaces.PolyfillLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.util.Map;

/**
 * Created by reinier.guerra on 1/24/17.
 */
@Configuration
@PropertySource("classpath:config.properties")
@ComponentScan(basePackages = {"org.polyfill"})
public class ServiceConfig {

    private static final String POLYFILLS_DIST = "polyfills";
    private static final String ALIASES_CONFIG = "aliases.json";
    public static final String BROWSER_ALIASES_CONFIG = "browserAliases.json";
    public static final String BROWSER_BASELINES_CONFIG = "browserBaselines.json";

    @Autowired
    private ConfigLoaderService configLoaderService;

    @Autowired
    private PolyfillLoaderService polyfillLoaderService;

    @Bean
    public Map<String, Object> aliases() throws IOException {
        return configLoaderService.getConfig(POLYFILLS_DIST, ALIASES_CONFIG);
    }

    @Bean
    public Map<String, Object> browserAliases() throws IOException {
        return configLoaderService.getConfig(POLYFILLS_DIST, BROWSER_ALIASES_CONFIG);
    }

    @Bean
    public Map<String, Object> browserBaselines() throws IOException {
        return configLoaderService.getConfig(POLYFILLS_DIST, BROWSER_BASELINES_CONFIG);
    }

    @Bean
    public Map<String, Polyfill> polyfills() throws IOException {
        return polyfillLoaderService.loadPolyfills(POLYFILLS_DIST);
    }
}
