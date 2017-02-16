package org.polyfill.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class TestConfig {
    @Bean
    public String polyfillsDirPath() {
        return "./src/test/testPolyfill/polyfills/";
    }

    @Bean
    public String baselineVersionsPath() {
        return "./configs/baselineVersions.json";
    }

    @Bean
    public String userAgentAliasesPath() {
        return "./configs/userAgentAliases.json";
    }
}
