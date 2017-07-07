package org.polyfillservice.api.configurations;

import org.polyfillservice.api.components.Polyfill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by smo on 6/18/17.
 */
@Configuration
public class CustomPolyfillsConfig {

    @Bean
    public Map<String, Polyfill> customPolyfills() {
        Polyfill polyfillD = new Polyfill.Builder("d")
            .browserRequirements(new HashMap<String, String>(){{
                put("chrome", "<50");
                put("firefox", "31");
            }})
            .dependencies(Arrays.asList("c", "g"))
            .detectSource("d.detectSource")
            .minSource("d.min")
            .rawSource("d.raw")
            .build();

        Polyfill polyfillG = new Polyfill.Builder("g")
            .browserRequirements(new HashMap<String, String>(){{
                put("chrome", "<50");
            }})
            .detectSource("g.detectSource")
            .minSource("g.min")
            .rawSource("g.raw")
            .build();

        return new HashMap<String, Polyfill>(){{
            put(polyfillD.getName(), polyfillD);
            put(polyfillG.getName(), polyfillG);
        }};
    }
}
