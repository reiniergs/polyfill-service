package org.polyfillservice.api.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by smo on 5/6/17.
 */
@Configuration
public class ProjectInfoConfig {
    @Bean
    public String projectVersion() {
        return "1.2.3";
    }

    @Bean
    public String projectUrl() {
        return "https://polyfills.com";
    }
}
