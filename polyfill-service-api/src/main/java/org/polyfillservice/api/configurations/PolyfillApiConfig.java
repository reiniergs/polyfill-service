package org.polyfillservice.api.configurations;

import org.polyfillservice.api.components.ServiceConfig;
import org.polyfillservice.api.interfaces.PolyfillServiceConfigLocation;
import org.polyfillservice.api.interfaces.ServiceConfigLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by reinier.guerra on 1/24/17.
 * Configuration class to load API classes and hold project info and service-wide configs.
 */
@Configuration
@PropertySource("classpath:config.properties")
@ComponentScan(basePackages = {"org.polyfillservice.api"})
public class PolyfillApiConfig {

    @Autowired
    private ServiceConfigLoaderService serviceConfigLoaderService;

    @Autowired(required = false)
    private PolyfillServiceConfigLocation serviceConfigLocation;

    // Link properties file's project version to a bean for a unified way to access it
    @Bean
    @Value("${project.version}")
    public String projectVersion(String projectVersion) {
        return projectVersion;
    }

    // Link properties file's project url to a bean for a unified way to access it
    @Bean
    @Value("${project.url}")
    public String projectUrl(String projectUrl) {
        return projectUrl;
    }

    // Service configuration for setting what polyfills to load initially
    // and determine values of fields of default query
    @Bean
    public ServiceConfig serviceConfig() {
        return serviceConfigLoaderService.loadConfig(serviceConfigLocation);
    }
}
