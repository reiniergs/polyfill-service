package org.polyfillservice.perf.services;

import org.polyfillservice.api.interfaces.PolyfillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Created by smo on 6/9/17.
 * Service to measure polyfill service's main API's performance.
 */
@Service
class PolyfillApiPerfMeasureService {

    @Resource(name = "testIterations")
    private int testIterations;

    @Resource(name = "warmUpIterations")
    private int warmUpIterations;

    @Autowired
    private PolyfillService polyfillService;

    @PostConstruct
    private void warmUp() {
        System.out.println("\nStart warming up");
        for (int i = 0; i < warmUpIterations; i++) {
            // use fake ua to load all polyfills
            polyfillService.getPolyfillsSource("other/1.2.3");
        }
        System.out.println("Finished warming up\n");
    }

    double getAvgElapsedTime(String uaString) {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < testIterations; i++) {
            polyfillService.getPolyfillsSource(uaString);
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        return (elapsedTime+0.0)/testIterations;
    }
}
