package org.polyfill.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by smo on 2/27/17.
 */
@Configuration
public class TestConfigs {

    private static final String TEST_RESOURCES_DIR = "./src/test/resources/";
    private static final String DIST_DIR = "./polyfills/__dist/";

    @Bean
    public String testRootPath() {
        return TEST_RESOURCES_DIR;
    }

    @Bean
    public String browserBaselinesPath() {
        return DIST_DIR + "browserBaselines.json";
    }

    @Bean
    public String browserAliasesPath() {
        return DIST_DIR + "browserAliases.json";
    }

    @Bean
    public String aliasesPath() {
        return TEST_RESOURCES_DIR + "polyfills/aliases.json";
    }
}
