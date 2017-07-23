package org.polyfillservice.perf;

import org.polyfillservice.perf.configurations.RunnerConfig;
import org.polyfillservice.perf.interfaces.PerfReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by smo on 6/7/17.
 * Simple runner class that can be executed directly to do
 * performance measurements and reporting.
 */
@Component
public class Runner {

    @Autowired
    private PerfReportService perfReportService;

    private void start() {
        perfReportService.showReport();
    }

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(RunnerConfig.class);
        Runner runner = context.getBean(Runner.class);
        runner.start();
    }
}
