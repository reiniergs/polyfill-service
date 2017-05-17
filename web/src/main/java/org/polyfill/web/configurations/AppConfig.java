package org.polyfill.web.configurations;

import org.polyfill.api.configurations.PolyfillApiConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by smo on 5/17/17.
 */
@Import(PolyfillApiConfig.class)
@Configuration
public class AppConfig {
}
