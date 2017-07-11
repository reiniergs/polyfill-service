package org.polyfillservice.perf.configurations;

import org.polyfillservice.api.components.PolyfillServiceConfigFileLocation;
import org.polyfillservice.api.configurations.PolyfillApiConfig;
import org.polyfillservice.api.interfaces.PolyfillServiceConfigLocation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by smo on 6/9/17.
 */
@Import(PolyfillApiConfig.class)
@Configuration
@ComponentScan(basePackages = {"org.polyfillservice.perf"})
public class RunnerConfig {

    @Bean
    public int testIterations() {
        return 30000;
    };

    @Bean
    public int warmUpIterations() {
        return 100000;
    }

    @Bean
    public List<String> uaList() {
        return Arrays.asList(
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.86 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:54.0) Gecko/20100101 Firefox/54.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/9.3.2 Safari/537.75.14",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.1.1 Safari/603.2.4",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko"
        );
    }

    @Bean
    public PolyfillServiceConfigLocation location() {
        File configFile = new File("./polyfill-service-perf/src/main/resources/service-config.xml");
        return new PolyfillServiceConfigFileLocation(configFile);
    }
}
