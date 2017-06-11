package org.polyfill.perf.services;

import com.inamik.text.tables.SimpleTable;
import org.polyfill.api.components.Feature;
import org.polyfill.api.components.Polyfill;
import org.polyfill.api.components.Query;
import org.polyfill.api.components.ServiceConfig;
import org.polyfill.api.interfaces.PolyfillService;
import org.polyfill.perf.components.TablePrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
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
    private BundleSizeMeasureService sizeMeasureService;

    @Autowired
    private PolyfillService polyfillService;

    private Query rawSourceQuery;
    private Query minSourceQuery;

    @PostConstruct
    private void init() {
        List<Feature> featureRequest = Collections.singletonList(new Feature("all"));
        rawSourceQuery = new Query.Builder(featureRequest)
            .setGatedForAll(true)
            .setIncludeDependencies(true)
            .setLoadOnUnknownUA(true)
            .setMinify(false)
            .build();

        minSourceQuery = new Query.Builder(featureRequest)
            .setGatedForAll(true)
            .setIncludeDependencies(true)
            .setLoadOnUnknownUA(true)
            .setMinify(true)
            .build();
    }

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

        TablePrinter tablePrinter = new TablePrinter("\t");

        // headers
        tablePrinter.addRow(
            "User Agent",
            "Polyfills",
            "Number of Polyfills",
            "Average Query Time",
            "Raw Source Size",
            "Min Source Size",
            "Gzipped Min Source Size"
        );

        for (Map.Entry<String, String> entry : uaMap.entrySet()) {
            String uaName = entry.getKey();
            String uaString = entry.getValue();

            List<Polyfill> polyfillsLoaded = polyfillService.getPolyfills(uaString);
            List<String> polyfillNames = polyfillsLoaded.stream()
                .map(Polyfill::getName)
                .collect(Collectors.toList());

            double avgElapsedTime = cpuTimeMeasureService.getMeasurement(uaString);

            String rawSource = polyfillService.getPolyfillsSource(uaString, rawSourceQuery);
            String minSource = polyfillService.getPolyfillsSource(uaString, minSourceQuery);
            int rawSourceByteSize = sizeMeasureService.getByteSize(rawSource);
            int minSourceByteSize = sizeMeasureService.getByteSize(minSource);
            int gzipMinSourceByteSize = sizeMeasureService.getGzipByteSize(minSource);

            tablePrinter.addRow(
                uaName,
                polyfillNames.toString(),
                polyfillNames.size() + "",
                formatMs(avgElapsedTime),
                formatBytes(rawSourceByteSize),
                formatBytes(minSourceByteSize),
                formatBytes(gzipMinSourceByteSize)
                );
        }

        tablePrinter.printToFile("./perf.tsv");
    }

    private String formatMs(double ms) {
        return String.format("%.3f ms", ms);
    }

    private String formatBytes(int bytes) {
        return String.format("%.3f kb", (bytes+0.0)/1000);
    }

    private void print(Object input) {
        System.out.println(input);
    }
}
