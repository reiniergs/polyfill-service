package org.polyfill.perf.services;

import org.polyfill.api.components.Polyfill;
import org.polyfill.api.components.ServiceConfig;
import org.polyfill.api.interfaces.PolyfillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by smo on 6/9/17.
 */
@Service
public class PerfReportService {

    @Resource(name = "uaMap")
    private Map<String, String> uaMap;

    @Autowired
    private ServiceConfig serviceConfig;

    @Autowired
    private CpuTimeMeasureService cpuTimeMeasureService;

    @Autowired
    private PolyfillService polyfillService;

    public void printConfigurations() {
        print("Configurations:\n");
        print(serviceConfig.toString());

        Set<String> polyfillNames = polyfillService.getAllPolyfills().keySet();
        print("\nNumber of polyfills loaded:" + polyfillNames.size());
        print("Polyfills loaded:" + polyfillNames);
        print("");
    }

    public void printMeasurements() {
        print("Measurements:");
        for (Map.Entry<String, String> entry : uaMap.entrySet()) {
            String uaName = entry.getKey();
            String uaString = entry.getValue();

            List<Polyfill> polyfillsLoaded = polyfillService.getPolyfills(uaString);
            List<String> polyfillNames = polyfillsLoaded.stream()
                .map(polyfill -> polyfill.getName())
                .collect(Collectors.toList());
            double avgElapsedTime = cpuTimeMeasureService.getMeasurement(uaString);

            print(uaName);
            print(polyfillNames);
            print(polyfillNames.size());
            print(avgElapsedTime + " ms");
            print("");
        }
        print("");
    }

    private void print(Object input) {
        System.out.println(input);
    }
}
