package org.polyfillservice.api.configurations;

import org.polyfillservice.api.components.Feature;
import org.polyfillservice.api.components.Query;
import org.polyfillservice.api.components.ServiceConfig;
import org.polyfillservice.api.interfaces.PolyfillServiceConfigLocation;
import org.polyfillservice.api.interfaces.ServiceConfigLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by reinier.guerra on 1/24/17.
 * Configuration class to load API classes and hold project info and service-wide configs.
 */
@Configuration
@PropertySource("classpath:config.properties")
@ComponentScan(basePackages = {"org.polyfillservice.api"})
public class PolyfillApiConfig {

    @Autowired
    ServiceConfigLoaderService serviceConfigLoaderService;
    @Autowired(required = false)
    private PolyfillServiceConfigLocation serviceConfigLocation;

    /**
     * Link properties file's project version to a bean for a unified way to access it
     */
    @Bean
    @Value("${project.version}")
    public String projectVersion(String projectVersion) {
        return projectVersion;
    }

    /**
     * Link properties file's project url to a bean for a unified way to access it
     */
    @Bean
    @Value("${project.url}")
    public String projectUrl(String projectUrl) {
        return projectUrl;
    }

    /**
     * Service configuration for setting what polyfills to load initially
     * and determine values of fields of default query
     */
    @Bean
    public ServiceConfig serviceConfig() {
        return serviceConfigLoaderService.loadConfig(serviceConfigLocation);
    }

    /**
     * Default query used when query object is not supplied to query service
     */
    @Bean
    @Autowired
    public Query defaultQuery(ServiceConfig serviceConfig) {
        List<Feature> polyfillRequestList = serviceConfig.getPolyfills().stream()
            .map(Feature::new)
            .collect(Collectors.toList());

        return new Query.Builder( polyfillRequestList )
            .setMinify( serviceConfig.shouldMinify() )
            .setLoadOnUnknownUA( serviceConfig.shouldLoadOnUnknownUA() )
            .setGatedForAll( serviceConfig.shouldGate() )
            .build();
    }
}
