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

    private final String polyfillsDist = "polyfills";

    @Autowired
    private ConfigLoaderService configLoaderService;

    @Autowired
    private PolyfillLoaderService polyfillLoaderService;

    @Bean
    public Map<String, Object> aliases() throws IOException {
        return configLoaderService.getConfig(polyfillsDist, "aliases.json");
    }

    @Bean
    public Map<String, Object> browserAliases() throws IOException {
        return configLoaderService.getConfig(polyfillsDist, "browserAliases.json");
    }

    @Bean
    public Map<String, Object> browserBaselines() throws IOException {
        return configLoaderService.getConfig(polyfillsDist, "browserBaselines.json");
    }

    @Bean
    public Map<String, Polyfill> polyfills() throws IOException {
        return polyfillLoaderService.loadPolyfills(polyfillsDist);
    }
}
