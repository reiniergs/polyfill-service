package org.polyfill.controllers;

import org.polyfill.components.Feature;
import org.polyfill.components.Polyfill;
import org.polyfill.interfaces.PolyfillQueryService;
import org.polyfill.interfaces.UserAgent;
import org.polyfill.interfaces.UserAgentParserService;
import org.polyfill.views.BadRequestView;
import org.polyfill.views.HandlebarView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by smo on 3/25/17.
 */
@Controller
public class TestController {

    // supported query params
    private static final String FEATURE = "feature";
    private static final String UA_OVERRIDE = "ua";
    private static final String MODE = "mode";

    @Autowired
    UserAgentParserService userAgentParserService;

    @Autowired
    PolyfillQueryService polyfillQueryService;

    /**
     * Modes:
     *   control:  All features are allowed, tests served, no polyfills
     *   all:      All features are allowed, tests and polyfills both served
     *   targeted: Only targeted features are allowed, tests and polyfills both served
     */
    @RequestMapping(value = "/test/tests", method = RequestMethod.GET)
    public View polyfillApi(@RequestHeader("User-Agent") String headerUA,
                            @RequestParam Map<String, String> params,
                            Model model) {

        String mode = params.getOrDefault(MODE, "all");
        String featureName = params.getOrDefault(FEATURE, "all");
        List<String> requestedFeatureList = Collections.singletonList(featureName);

        List<Polyfill> polyfillList;
        if ("targeted".equals(MODE)) {
            String uaString = params.get(UA_OVERRIDE) != null ? params.get(UA_OVERRIDE) : headerUA;
            UserAgent userAgent = userAgentParserService.parse(uaString);
            polyfillList = polyfillQueryService.getPolyfills(requestedFeatureList, userAgent);
        } else {
            polyfillList = polyfillQueryService.getPolyfills(requestedFeatureList, null);
        }

        List<Map<String, Object>> testFeatures = getTestFeatures(polyfillList);

        model.addAttribute("loadPolyfill", !"control".equals(mode));
        model.addAttribute("forceAlways", !"targeted".equals(mode));
        model.addAttribute("features", testFeatures);
        model.addAttribute("mode", mode);

        return new HandlebarView("tests/browsers/runner", model);
    }

    private List<Map<String, Object>> getTestFeatures(List<Polyfill> polyfillList) {
        return polyfillList.stream()
                .filter(polyfill -> !polyfill.getName().startsWith("_"))
                .filter(polyfill -> polyfill.isTestable())
                .map(polyfill -> {
                    Map<String, Object> testFeature = new HashMap<>();
                    testFeature.put("feature", polyfill.getName());

                    String detectSource = polyfill.getDetectSource();
                    testFeature.put("detect", !isEmpty(detectSource) ? detectSource : false);

                    String testsSource = polyfill.getTestsSource();
                    testFeature.put("tests", !isEmpty(testsSource) ? testsSource : false);

                    return testFeature;
                })
                .collect(Collectors.toList());
    }

    private boolean isEmpty(String value) {
        return value == null || "".equals(value);
    }
}
