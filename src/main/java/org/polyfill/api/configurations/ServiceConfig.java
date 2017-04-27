package org.polyfill.api.configurations;

import org.polyfill.api.components.Polyfill;
import org.polyfill.api.interfaces.ConfigLoaderService;
import org.polyfill.api.interfaces.PolyfillLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@ComponentScan(basePackages = {"org.polyfill.api"})
public class ServiceConfig {

    @Value("${path.polyfillsDist}")
    private String polyfillsDirPath;

    @Value("${path.browserBaselines}")
    private String browserBaselinesPath;

    @Value("${path.featureAliases}")
    private String aliasesPath;

    @Value("${path.browserAliases}")
    private String browserAliasesPath;

    @Autowired
    private ConfigLoaderService configLoaderService;

    @Autowired
    private PolyfillLoaderService polyfillLoaderService;

    @Bean
    public Map<String, Object> aliases() throws IOException {
        return configLoaderService.getConfig(this.aliasesPath);
    }

    @Bean
    public Map<String, Object> browserAliases() throws IOException {
        return configLoaderService.getConfig(this.browserAliasesPath);
    }

    @Bean
    public Map<String, Object> browserBaselines() throws IOException {
        return configLoaderService.getConfig(this.browserBaselinesPath);
    }

    @Bean
    public Map<String, Polyfill> polyfills() throws IOException {
        return polyfillLoaderService.loadPolyfillsToMap(this.polyfillsDirPath);
    }
}