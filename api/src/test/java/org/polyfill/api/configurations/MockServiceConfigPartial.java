package org.polyfill.api.configurations;

import org.polyfill.api.components.ServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;

/**
 * Created by smo on 6/7/17.
 */
@Configuration
public class MockServiceConfigPartial {
    @Bean
    public ServiceConfig serviceConfig() {
        return new ServiceConfig().setPolyfills(Arrays.asList("d", "eee"));
    }
}
