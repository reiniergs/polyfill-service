package org.polyfill.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class TestConfig {
    @Bean
    public String polyfillDir() {
        return "./src/test/testPolyfill/polyfills/";
    }

    @Bean
    public String baselineVersionFile() {
        return "./configs/baselineVersions.json";
    }
}
