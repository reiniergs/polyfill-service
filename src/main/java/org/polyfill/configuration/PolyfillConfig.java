package org.polyfill.configuration;

import org.polyfill.interfaces.SourceService;
import org.polyfill.services.SourceServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by reinier.guerra on 12/5/16.
 */
@Configuration
public class PolyfillConfig {

    @Bean
    public SourceService SourceServiceAdapter() throws Exception {
        return new SourceServiceImpl();
    }
}
