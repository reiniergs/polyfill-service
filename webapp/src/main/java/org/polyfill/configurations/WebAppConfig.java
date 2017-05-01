package org.polyfill.configurations;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by smo on 4/27/17.
 */
@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"org.polyfill"})
public class WebAppConfig {
}
