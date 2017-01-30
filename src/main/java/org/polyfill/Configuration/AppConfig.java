package org.polyfill.configuration;

import org.polyfill.components.userAgent.PolyfillConfigImpl;
import org.polyfill.interfaces.PolyfillConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by reinier.guerra on 1/24/17.
 */
@Configuration
public class AppConfig {

    @Bean
    public PolyfillConfig ScottTest() {
        return new PolyfillConfigImpl("path/to/polyfill/confis");
    }

}
