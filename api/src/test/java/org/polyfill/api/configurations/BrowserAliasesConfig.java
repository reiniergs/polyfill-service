package org.polyfill.api.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by smo on 3/4/17.
 */
@Configuration
public class BrowserAliasesConfig {
    @Bean
    public Map<String, Object> browserAliases() {
        return new HashMap<String, Object>(){{
            put("mobile safari", "ios_saf");
            put("firefox (namoroka)", "firefox");
            put("yandex.browser", new HashMap<String, Object>(){{
                put("14.2", Arrays.asList("chrome", "32"));
            }});
        }};
    }
}
