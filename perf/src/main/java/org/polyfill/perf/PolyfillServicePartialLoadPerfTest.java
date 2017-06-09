package org.polyfill.perf;

import org.polyfill.api.components.Polyfill;
import org.polyfill.api.components.PolyfillServiceConfigLocation;
import org.polyfill.api.components.ServiceConfig;
import org.polyfill.api.configurations.PolyfillApiConfig;
import org.polyfill.api.interfaces.PolyfillService;
import org.polyfill.api.interfaces.UserAgentParserService;
import org.polyfill.perf.util.UA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by smo on 6/7/17.
 */
@Component
@Import(PolyfillApiConfig.class)
public class PolyfillServicePartialLoadPerfTest {

    private static final List<String> UA_LIST = Arrays.asList(UA.CHROME, UA.FF, UA.IE11, UA.IOS, UA.SAFARI, UA.SAFARI_9);
    private static final int TEST_ITERATIONS = 15000;
    private static final int WARM_UP_ITERATIONS = 100000;

    @Autowired
    private PolyfillService polyfillService;

    @Autowired
    private UserAgentParserService userAgentParserService;

    @Bean
    private PolyfillServiceConfigLocation location() {
        return new PolyfillServiceConfigLocation(new File("./perf/src/main/resources/service-config.xml"));
    }

    private void testMethod(String uaString) {
        String output = polyfillService.getPolyfillsSource(uaString);
    }

    private Map<String, String> getUaMap(List<String> uaList) {
        return uaList.stream().collect(Collectors.toMap(
            uaString -> userAgentParserService.parse(uaString).toString(),
            Function.identity()));
    }

    private void printPolyfillsLoaded() {
        System.out.println(polyfillService.getAllPolyfills().keySet());
    }

    private void printPolyfillResponseInfo(String uaName, String uaString) {
        List<Polyfill> polyfillList = polyfillService.getPolyfills(uaString);
        List<String> polyfillNames = polyfillList.stream()
            .map(polyfill -> polyfill.getName())
            .collect(Collectors.toList());
        System.out.println("\n" + uaName);
        System.out.println(uaString);
        System.out.println("Polyfills loaded: " + polyfillNames);
        System.out.println("Number of polyfills loaded: " + polyfillList.size());
    }

    private void warmUp(int warmUpIterations) {
        System.out.println("\nStart warming up");
        for (int i = 0; i < warmUpIterations; i++) {
            testMethod("other/1.2.3");
        }
        System.out.println("Finished warming up\n");
    }

    private void startTest(int N, String uaName, String uaString) {
        long startTime = new Date().getTime();

        for (int i = 0; i < N; i++) {
            testMethod(uaString);
        }

        long endTime = new Date().getTime();
        long elapsedTime = endTime - startTime;
        System.out.println("\nFor user agent: " + uaName);
        System.out.println("Elapsed time: " + elapsedTime + " ms");
        System.out.println("Average elapsed time for a test: " + (elapsedTime+0.0)/N + " ms");
    }

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(PolyfillServicePartialLoadPerfTest.class);
        PolyfillServicePartialLoadPerfTest test = context.getBean(PolyfillServicePartialLoadPerfTest.class);

        System.out.println("Test iterations: " + TEST_ITERATIONS);
        System.out.println("Warm up iterations: " + WARM_UP_ITERATIONS);
        test.printPolyfillsLoaded();

        Map<String, String> uaMap = test.getUaMap(UA_LIST);

        for (Map.Entry<String, String> entry : uaMap.entrySet()) {
            test.printPolyfillResponseInfo(entry.getKey(), entry.getValue());
        }

        // run perf tests
        test.warmUp(WARM_UP_ITERATIONS);
        for (Map.Entry<String, String> entry : uaMap.entrySet()) {
            test.startTest(TEST_ITERATIONS, entry.getKey(), entry.getValue());
        }
    }
}
