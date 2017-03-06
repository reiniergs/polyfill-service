package org.polyfill.configurations;

import org.polyfill.components.Polyfill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by smo on 3/4/17.
 */
@Configuration
public class MockPolyfillsConfig {

    @Bean
    public Map<String, Object> aliases() {
        return new HashMap<String, Object>(){{
            put("default", Arrays.asList("a", "b", "c"));
            put("es6", Arrays.asList("b", "c", "d"));
            put("foo", Arrays.asList("c", "e"));
        }};
    }

    @Bean
    public Map<String, Object> browserBaselines() {
        return new HashMap<String, Object>(){{
            put("chrome", "*");
            put("firefox", ">=3.6");
        }};
    }

    @Bean
    public Map<String, Polyfill> polyfills() {
        Map<String, Object> polyfillA = new HashMap<String, Object>(){{
            put(Polyfill.ALIASES_KEY, Arrays.asList("default", "foo"));
            put(Polyfill.BROWSER_REQUIREMENTS_KEY, new HashMap<String, Object>(){{
                put("chrome", "*");
                put("firefox", "4 - 31");
            }});
            put(Polyfill.DEPENDENCIES_KEY, Arrays.asList("b", "d"));
            put(Polyfill.DETECT_SOURCE_KEY, "a.detectSource");
            put(Polyfill.MIN_SOURCE_KEY, "a.min");
            put(Polyfill.RAW_SOURCE_KEY, "a.raw_");
        }};

        Map<String, Object> polyfillB = new HashMap<String, Object>(){{
            put(Polyfill.ALIASES_KEY, Arrays.asList("default", "es6"));
            put(Polyfill.BROWSER_REQUIREMENTS_KEY, new HashMap<String, Object>(){{
                put("chrome", "*");
                put("firefox", "31");
            }});
            put(Polyfill.DEPENDENCIES_KEY, Arrays.asList("c"));
            put(Polyfill.DETECT_SOURCE_KEY, "b.detectSource");
            put(Polyfill.MIN_SOURCE_KEY, "b.min");
            put(Polyfill.RAW_SOURCE_KEY, "b.raw_");
        }};

        Map<String, Object> polyfillC = new HashMap<String, Object>(){{
            put(Polyfill.ALIASES_KEY, Arrays.asList("default", "es6", "foo"));
            put(Polyfill.BROWSER_REQUIREMENTS_KEY, new HashMap<String, Object>(){{
                put("chrome", "*");
                put("firefox", "4 - 31");
            }});
            put(Polyfill.DEPENDENCIES_KEY, new ArrayList<String>());
            put(Polyfill.DETECT_SOURCE_KEY, "c.detectSource");
            put(Polyfill.MIN_SOURCE_KEY, "c.min");
            put(Polyfill.RAW_SOURCE_KEY, "c.raw_");
        }};

        Map<String, Object> polyfillD = new HashMap<String, Object>(){{
            put(Polyfill.ALIASES_KEY, Arrays.asList("es6"));
            put(Polyfill.BROWSER_REQUIREMENTS_KEY, new HashMap<String, Object>(){{
                put("chrome", "<50");
                put("firefox", "31");
            }});
            put(Polyfill.DEPENDENCIES_KEY, Arrays.asList("b", "c"));
            put(Polyfill.DETECT_SOURCE_KEY, "d.detectSource");
            put(Polyfill.MIN_SOURCE_KEY, "d.min");
            put(Polyfill.RAW_SOURCE_KEY, "d.raw_");
        }};

        Map<String, Object> polyfillE = new HashMap<String, Object>(){{
            put(Polyfill.ALIASES_KEY, Arrays.asList("foo"));
            put(Polyfill.BROWSER_REQUIREMENTS_KEY, new HashMap<String, Object>(){{
                put("chrome", "*");
            }});
            put(Polyfill.DEPENDENCIES_KEY, new ArrayList<>());
            put(Polyfill.DETECT_SOURCE_KEY, "e.detectSource");
            put(Polyfill.MIN_SOURCE_KEY, "e.min");
            put(Polyfill.RAW_SOURCE_KEY, "e.raw_");
        }};

        return new HashMap<String, Polyfill>(){{
            put("a", new Polyfill("a", polyfillA));
            put("b", new Polyfill("b", polyfillB));
            put("c", new Polyfill("c", polyfillC));
            put("d", new Polyfill("d", polyfillD));
            put("e", new Polyfill("e", polyfillE));
        }};
    }
}
