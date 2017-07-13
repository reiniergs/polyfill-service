package org.polyfillservice.api.configurations;

import org.polyfillservice.api.components.ServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Created by smo on 6/7/17.
 */
@Configuration
public class CustomPolyfillListConfig {
    @Bean
    public ServiceConfig serviceConfig() {
        return new ServiceConfig.Builder()
            .setPolyfills( Arrays.asList("d", "eee") )
            .build();
    }
}
