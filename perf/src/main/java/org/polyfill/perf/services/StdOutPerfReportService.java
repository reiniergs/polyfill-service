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
import org.polyfill.api.interfaces.UserAgentParserService;
import org.polyfill.perf.interfaces.PerfReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by smo on 6/9/17.
 * PerfReportService to print measurements to standard output.
 */
@Service("stdout")
public class StdOutPerfReportService implements PerfReportService {

    @Resource(name = "uaList")
    private List<String> uaList;
    @Autowired
    private ServiceConfig serviceConfig;
    @Autowired
    private PolyfillApiPerfMeasureService apiPerfMeasureService;
    @Autowired
    private SourceSizeMeasureService sizeMeasureService;
    @Autowired
    private PolyfillService polyfillService;
    @Autowired
    private UserAgentParserService uaParserService;

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
        rawSourceQuery = new Query.Builder(featureRequest).setMinify(false).build();
        minSourceQuery = new Query.Builder(featureRequest).setMinify(true).build();
    }

    @Override
    public void showReport() {
        printConfigurations();
        printMeasurements();
    }

    private void printConfigurations() {
        Set<String> polyfillNames = polyfillService.getAllPolyfills().keySet();

        // output starts here
        print("# Configurations"
            + "\n" + serviceConfig.toString()
            + "\nNumber of polyfills loaded:" + polyfillNames.size()
            + "\nPolyfills loaded:" + polyfillNames
            + "\n"
        );
    }

    private void printMeasurements() {
        GridTable table = buildMeasurementsTable();

        // output starts here
        print("# Measurements");
        print(table);
    }

    private GridTable buildMeasurementsTable() {
        SimpleTable table = SimpleTable.of();

        // add headers
        table.nextRow();
        this.headers.forEach(table::nextCell);

        for (String uaString : uaList) {
            // measure performance
            double avgElapsedTime = apiPerfMeasureService.getAvgElapsedTime(uaString);

            // measure source sizes
            String rawSource = polyfillService.getPolyfillsSource(uaString, rawSourceQuery);
            String minSource = polyfillService.getPolyfillsSource(uaString, minSourceQuery);
            int rawSourceByteSize = sizeMeasureService.getByteSize(rawSource);
            int minSourceByteSize = sizeMeasureService.getByteSize(minSource);
            int gzipMinSourceByteSize = sizeMeasureService.getGzipByteSize(minSource);

            // get names of loaded polyfills for this user agent
            List<String> polyfillNames = polyfillService.getPolyfills(uaString).stream()
                .map(Polyfill::getName)
                .collect(Collectors.toList());

            // add row to table
            table.nextRow()
                .nextCell(normalizeUA(uaString))
                .nextCell(formatMs(avgElapsedTime))
                .nextCell(formatBytes(rawSourceByteSize))
                .nextCell(formatBytes(minSourceByteSize))
                .nextCell(formatBytes(gzipMinSourceByteSize))
                .nextCell(String.valueOf(polyfillNames.size()))
                .nextCell(polyfillNames);
        }

        GridTable gridTable = table.toGrid();
        gridTable = Border.of(Border.Chars.of('+', '-', '|')).apply(gridTable);

        return gridTable;
    }

    private String formatMs(double ms) {
        return String.format("%.3f ms", ms);
    }

    private String formatBytes(int bytes) {
        return String.format("%.3f kb", (bytes+0.0)/1000);
    }

    private String normalizeUA(String uaString) {
        return uaParserService.parse(uaString).toString();
    }

    private void print(Object input) {
        if (input instanceof GridTable) {
            Util.print((GridTable)input);
        } else {
            System.out.println(input);
        }
    }
}
