package org.polyfillservice.web.configurations;

import org.polyfillservice.api.configurations.PolyfillApiConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by smo on 5/17/17.
 */
@Import(PolyfillApiConfig.class)
@Configuration
public class AppConfig {
}
