package org.polyfill.api.services;

import org.junit.runner.RunWith;
import org.polyfill.api.components.Polyfill;
import org.polyfill.api.configurations.CustomPolyfillsConfig;
import org.polyfill.api.configurations.PolyfillsConfig;
import org.polyfill.api.configurations.CustomPolyfillListConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by smo on 6/18/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader=AnnotationConfigContextLoader.class,
        classes={PolyfillsConfig.class,
                CustomPolyfillsConfig.class,
                CustomPolyfillListConfig.class,
                JsonConfigLoaderService.class,
                FinancialTimesPolyfillLoaderService.class
        })
public class FinancialTimesPolyfillLoaderServiceCustomListTest
        extends FinancialTimesPolyfillLoaderServiceTest {

    @Override
    Map<String, Polyfill> getDefaultExpectedPolyfills() {
        return new HashMap<String, Polyfill>(){{
            put("d", defaultPolyfills.get("d")); // explicitly requested
            put("b", defaultPolyfills.get("b")); // depended by d
            put("c", defaultPolyfills.get("c")); // depended by d
            put("e", defaultPolyfills.get("e")); // contained by alias eee
        }};
    }

    @Override
    Map<String, Polyfill> getExpectedPolyfillsFromMultipleLocations() {
        return new HashMap<String, Polyfill>(){{
            put("d", customPolyfills.get("d")); // explicitly requested
            put("c", defaultPolyfills.get("c")); // depended by d
            put("g", customPolyfills.get("g")); // depended by d
            put("e", defaultPolyfills.get("e")); // contained by alias eee
        }};
    }
}
