package org.polyfillservice.api.configurations;

import org.polyfillservice.api.components.Feature;
import org.polyfillservice.api.components.Polyfill;
import org.polyfillservice.api.components.Query;
import org.polyfillservice.api.components.ServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * Created by smo on 3/4/17.
 */
@Configuration
public class PolyfillsConfig {

    @Bean
    public ServiceConfig serviceConfig() {
        return new ServiceConfig();
    }

    @Bean
    public Query defaultQuery() {
        return new Query.Builder(Collections.singletonList(new Feature("all")))
            .setMinify(true)
            .setLoadOnUnknownUA(true)
            .setGatedForAll(true)
            .build();
    }

    @Bean
    public Map<String, List<String>> aliases() {
        return new HashMap<String, List<String>>(){{
            put("default", Arrays.asList("a", "b", "c"));
            put("es6", Arrays.asList("b", "c", "d"));
            put("foo", Arrays.asList("c", "e"));
            put("eee", Arrays.asList("e"));
        }};
    }

    @Bean
    public Map<String, String> browserBaselines() {
        return new HashMap<String, String>(){{
            put("chrome", "*");
            put("firefox", ">=3.6");
        }};
    }

    @Bean
    public Map<String, Polyfill> polyfills() {
        Polyfill polyfillA = new Polyfill.Builder("a")
            .aliases(Arrays.asList("default", "foo"))
            .browserRequirements(new HashMap<String, String>(){{
                put("chrome", "*");
                put("firefox", "4 - 31");
            }})
            .dependencies(Arrays.asList("b", "d"))
            .detectSource("a.detectSource")
            .minSource("a.min")
            .rawSource("a.raw")
            .build();

        Polyfill polyfillB = new Polyfill.Builder("b")
            .aliases(Arrays.asList("default", "es6"))
            .browserRequirements(new HashMap<String, String>(){{
                put("chrome", "*");
                put("firefox", "31");
            }})
            .dependencies(Arrays.asList("c"))
            .detectSource("b.detectSource")
            .minSource("b.min")
            .rawSource("b.raw")
            .build();

        Polyfill polyfillC = new Polyfill.Builder("c")
            .aliases(Arrays.asList("default", "es6", "foo"))
            .browserRequirements(new HashMap<String, String>(){{
                put("chrome", "*");
                put("firefox", "4 - 31");
            }})
            .dependencies(new ArrayList<String>())
            .detectSource("c.detectSource")
            .minSource("c.min")
            .rawSource("c.raw")
            .build();

        Polyfill polyfillD = new Polyfill.Builder("d")
            .aliases(Arrays.asList("es6"))
            .browserRequirements(new HashMap<String, String>(){{
                put("chrome", "<50");
                put("firefox", "31");
            }})
            .dependencies(Arrays.asList("b", "c"))
            .detectSource("d.detectSource")
            .minSource("d.min")
            .rawSource("d.raw")
            .build();

        Polyfill polyfillE = new Polyfill.Builder("e")
            .aliases(Arrays.asList("foo", "eee"))
            .browserRequirements(new HashMap<String, String>(){{
                put("chrome", "*");
            }})
            .dependencies(new ArrayList<>())
            .detectSource("e.detectSource")
            .minSource("e.min")
            .rawSource("e.raw")
            .build();

        return new HashMap<String, Polyfill>(){{
            put(polyfillA.getName(), polyfillA);
            put(polyfillB.getName(), polyfillB);
            put(polyfillC.getName(), polyfillC);
            put(polyfillD.getName(), polyfillD);
            put(polyfillE.getName(), polyfillE);
        }};
    }
}
