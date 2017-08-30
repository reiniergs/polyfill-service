package org.polyfillservice.api.configurations;

import org.polyfillservice.api.components.ServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;

/**
 * Created by smo on 8/26/17.
 */
@Configuration
public class CustomServiceConfig {
    @Primary
    @Bean
    public ServiceConfig customServiceConfig() {
        return new ServiceConfig.Builder()
            .setPolyfills(Arrays.asList("a", "b", "c", "d"))
            .setGated(false)
            .build();
    }
}
