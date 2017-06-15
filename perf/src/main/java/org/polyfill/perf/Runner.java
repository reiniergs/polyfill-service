package org.polyfill.perf;

import org.polyfill.perf.interfaces.PerfReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * Created by smo on 6/7/17.
 * Simple runner class that can be executed directly to do
 * performance measurements and reporting.
 */
@Component
@ComponentScan(basePackages = {"org.polyfill.perf"})
public class Runner {

    @Autowired
    private PerfReportService perfReportService;

    private void start() {
        perfReportService.showReport();
    }

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Runner.class);
        Runner runner = context.getBean(Runner.class);
        runner.start();
    }
}
