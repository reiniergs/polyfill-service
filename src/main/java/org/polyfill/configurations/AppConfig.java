package org.polyfill.configurations;

import org.polyfill.components.Polyfill;
import org.polyfill.interfaces.ConfigLoaderService;
import org.polyfill.interfaces.PolyfillLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * Created by reinier.guerra on 1/24/17.
 */
@Configuration
public class AppConfig {

    @Resource(name = "polyfillsDirPath")
    private String polyfillsDirPath;

    @Resource(name = "browserBaselinesPath")
    private String browserBaselinesPath;

    @Resource(name = "aliasesPath")
    private String aliasesPath;

    @Resource(name = "browserAliasesPath")
    private String browserAliasesPath;

    @Autowired
    private ConfigLoaderService configLoaderService;

    @Autowired
    private PolyfillLoaderService polyfillLoaderService;

    @Bean
    public Map<String, Object> aliases() throws IOException {
        return configLoaderService.getConfig(aliasesPath);
    }

    @Bean
    public Map<String, Object> browserAliases() throws IOException {
        return configLoaderService.getConfig(browserAliasesPath);
    }

    @Bean
    public Map<String, Object> browserBaselines() throws IOException {
        return configLoaderService.getConfig(browserBaselinesPath);
    }

    @Bean
    public Map<String, Polyfill> polyfills() throws IOException {
        return polyfillLoaderService.loadPolyfillsToMap(polyfillsDirPath);
    }
}
