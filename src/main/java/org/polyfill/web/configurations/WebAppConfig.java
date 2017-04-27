package org.polyfill.web.configurations;

import org.polyfill.api.configurations.ServiceConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by smo on 4/27/17.
 */
@EnableWebMvc
@Configuration
@Import(ServiceConfig.class)
@ComponentScan(basePackages = {"org.polyfill.web"})
public class WebAppConfig {
}
