package org.polyfill.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by reinier.guerra on 1/24/17.
 */
@Configuration
public class AppConfig {

    @Bean
    public String polyfillDir() {
        return "./polyfills/__dist/";
    }

    @Bean
    public String baselineVersionFile() {
        return "./configs/baselineVersions.json";
    }

    @Bean
    public String userAgentAliasesFile() {
        return "./configs/userAgentAliases.json";
    }
}
