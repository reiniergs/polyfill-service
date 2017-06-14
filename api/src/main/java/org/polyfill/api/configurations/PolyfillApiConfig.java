package org.polyfill.api.configurations;

import org.polyfill.api.components.Feature;
import org.polyfill.api.components.Query;
import org.polyfill.api.components.ServiceConfig;
import org.polyfill.api.interfaces.PolyfillServiceConfigLocation;
import org.polyfill.api.interfaces.ServiceConfigLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Collections;

/**
 * Created by reinier.guerra on 1/24/17.
 * Configuration class to load API classes and hold project info and service-wide configs.
 */
@Configuration
@PropertySource("classpath:config.properties")
@ComponentScan(basePackages = {"org.polyfill.api"})
public class PolyfillApiConfig {

    @Autowired
    ServiceConfigLoaderService serviceConfigLoaderService;
    @Autowired(required = false)
    private PolyfillServiceConfigLocation serviceConfigLocation;

    @Value("${project.version}")
    private String projectVersion;

    @Value("${project.url}")
    private String projectUrl;

    @Bean
    public String projectVersion() {
        return projectVersion;
    }

    @Bean
    public String projectUrl() {
        return projectUrl;
    }

    @Bean
    public ServiceConfig serviceConfig() {
        return serviceConfigLoaderService.loadConfig(serviceConfigLocation);
    }

    @Bean
    public Query defaultQuery() {
        ServiceConfig serviceConfig = this.serviceConfig();
        return new Query.Builder(Collections.singletonList(new Feature("all")))
            .setMinify(serviceConfig.shouldMinify())
            .setLoadOnUnknownUA(serviceConfig.shouldLoadOnUnknownUA())
            .setGatedForAll(serviceConfig.shouldGate())
            .build();
    }
}
