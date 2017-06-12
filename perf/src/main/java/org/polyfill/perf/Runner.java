package org.polyfill.perf;

import org.polyfill.perf.services.PerfReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * Created by smo on 6/7/17.
 */
@Component
@ComponentScan(basePackages = {"org.polyfill.perf"})
public class Runner {

    @Autowired
    private PerfReportService perfReportService;

    private void start() {
        perfReportService.printConfigurations();
        perfReportService.printMeasurements();
    }

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Runner.class);
        Runner runner = context.getBean(Runner.class);
        runner.start();
    }
}
