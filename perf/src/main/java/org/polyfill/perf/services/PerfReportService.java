package org.polyfill.perf.services;

import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;
import org.polyfill.api.components.Feature;
import org.polyfill.api.components.Polyfill;
import org.polyfill.api.components.Query;
import org.polyfill.api.components.ServiceConfig;
import org.polyfill.api.interfaces.PolyfillService;
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

    private List<String> headers = Arrays.asList(
        "User Agent",
        "Average Query",
        "Raw Source",
        "Min Source",
        "Gzip Min Source",
        "# of Polyfills",
        "Polyfills");

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
        print("# Configurations");
        print(serviceConfig.toString());

        Set<String> polyfillNames = polyfillService.getAllPolyfills().keySet();
        print("\nNumber of polyfills loaded:" + polyfillNames.size());
        print("Polyfills loaded:" + polyfillNames);
        print("");
    }

    public void printMeasurements() {
        print("# Measurements");

        SimpleTable table = SimpleTable.of();

        table.nextRow();
        for (String header : this.headers) {
            table = table.nextCell().addLine(header);
        }

        for (Map.Entry<String, String> entry : uaMap.entrySet()) {
            String uaName = entry.getKey();
            String uaString = entry.getValue();

            List<Polyfill> polyfillsLoaded = polyfillService.getPolyfills(uaString);
            String rawSource = polyfillService.getPolyfillsSource(uaString, rawSourceQuery);
            String minSource = polyfillService.getPolyfillsSource(uaString, minSourceQuery);

            List<String> polyfillNames = polyfillsLoaded.stream()
                .map(Polyfill::getName)
                .collect(Collectors.toList());

            double avgElapsedTime = cpuTimeMeasureService.getMeasurement(uaString);
            int rawSourceByteSize = sizeMeasureService.getByteSize(rawSource);
            int minSourceByteSize = sizeMeasureService.getByteSize(minSource);
            int gzipMinSourceByteSize = sizeMeasureService.getGzipByteSize(minSource);

            List<Object> fields = Arrays.asList(
                uaName,
                formatMs(avgElapsedTime),
                formatBytes(rawSourceByteSize),
                formatBytes(minSourceByteSize),
                formatBytes(gzipMinSourceByteSize),
                polyfillNames.size() + "",
                polyfillNames
            );

            table = table.nextRow();
            for (Object field : fields) {
                if (field instanceof String) {
                    table = table.nextCell().addLine((String)field);
                } else if (field instanceof List) {
                    table = table.nextCell();
                    List<String> items = (List)field;
                    for (String item : items) {
                        table = table.addLine(item);
                    }
                }
            }
        }

        GridTable gridTable = table.toGrid();
        gridTable = Border.of(Border.Chars.of('+', '-', '|')).apply(gridTable);

        Util.print(gridTable);
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
