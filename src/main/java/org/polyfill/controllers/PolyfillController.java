package org.polyfill.controllers;

import org.polyfill.components.FeatureOptions;
import org.polyfill.interfaces.PolyfillQueryService;
import org.polyfill.interfaces.UserAgent;
import org.polyfill.interfaces.UserAgentParserService;
import org.polyfill.views.BadRequestView;
import org.polyfill.views.HandlebarView;
import org.polyfill.views.NotFoundView;
import org.polyfill.views.PolyfillsView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by reinier.guerra on 10/12/16.
 */
@Controller
public class PolyfillController {

    // supported query params
    private static final String FEATURES = "features";
    private static final String EXCLUDES = "excludes";
    private static final String GLOBAL_FLAGS = "flags";
    private static final String UA_OVERRIDE = "ua";
    private static final String UNKNOWN_OVERRIDE = "unknown";

    @Autowired
    UserAgentParserService userAgentParserService;

    @Autowired
    PolyfillQueryService polyfillQueryService;

    @RequestMapping(value = "/polyfill.{type}", method = RequestMethod.GET)
    public View polyfillApi(@RequestHeader("User-Agent") String uaString,
                            @RequestParam Map<String, String> params,
                            @PathVariable String type,
                            Model model) {

        return handlePolyfillsRequest(uaString, params, type, model, false);
    }

    @RequestMapping(value = "/polyfill.min.{type}", method = RequestMethod.GET)
    public View polyfillMinApi(@RequestHeader("User-Agent") String headerUA,
                               @RequestParam Map<String, String> params,
                               @PathVariable String type,
                               Model model) {

        return handlePolyfillsRequest(headerUA, params, type, model, true);
    }

    @RequestMapping(value = "/user-agent", method = RequestMethod.GET)
    public View userAgentTest(@RequestHeader("User-Agent") String uaString, Model model) {
        UserAgent userAgent = userAgentParserService.parse(uaString);

        model.addAttribute("browserName", userAgent.getFamily());
        model.addAttribute("browserVersion", userAgent.getVersion());

        return new HandlebarView("userAgentDetector", model);
    }

    @RequestMapping(value = "/notfound", method = RequestMethod.GET)
    public View polyfillNotFound(@RequestHeader("User-Agent") String uaString, Model model) {
        return new NotFoundView("notFound");
    }

    /******************************************************
     * Helpers
     ******************************************************/

    private View handlePolyfillsRequest(String uaString, Map<String, String> params,
            String type, Model model, boolean doMinify) {

        if (type.equals("js")) {
            String polyfillsSource = getPolyfillsSource(uaString, params, doMinify);
            return new PolyfillsView(polyfillsSource, doMinify);
        } else {
            model.addAttribute("message", "Sorry we just support javascript polyfills.");
            return new BadRequestView("badRequest", model);
        }
    }

    private String getPolyfillsSource(String headerUA, Map<String, String> params, boolean doMinify) {
        String uaString = params.get(UA_OVERRIDE) != null ? params.get(UA_OVERRIDE) : headerUA;
        UserAgent userAgent = userAgentParserService.parse(uaString);
        List<String> excludeList = buildExcludeList(params.get(EXCLUDES));
        List<FeatureOptions> featureList = buildFeatureList(params.get(FEATURES), params.get(GLOBAL_FLAGS));
        boolean loadOnUnknown = "polyfill".equals(params.get(UNKNOWN_OVERRIDE));

        return polyfillQueryService.getPolyfillsSource(userAgent, featureList, excludeList, doMinify, loadOnUnknown);
    }

    private List<String> buildExcludeList(String excludes) {
        return splitToList(excludes, ",");
    }

    private List<FeatureOptions> buildFeatureList(String features, String globalFlags) {
        if (features == null) {
            features = "default";
        }

        List<FeatureOptions> featureList = splitToList(features, ",").stream()
                .map(FeatureOptions::new)
                .collect(Collectors.toList());

        if (globalFlags != null) {
            FeatureOptions globalOptions = new FeatureOptions("global", splitToList(globalFlags, ","));
            featureList.forEach(featureOption -> featureOption.copyOptions(globalOptions));
        }

        return featureList;
    }

    private List<String> splitToList(String string, String delimiter) {
        return string == null ? new ArrayList<>() : Arrays.asList(string.split(delimiter));
    }
}
