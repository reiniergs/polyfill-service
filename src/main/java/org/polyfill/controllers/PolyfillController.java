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
import java.util.stream.Collectors;

/**
 * Created by reinier.guerra on 10/12/16.
 */
@Controller
public class PolyfillController {

    @Autowired
    UserAgentParserService userAgentParserService;

    @Autowired
    PolyfillQueryService polyfillQueryService;

    @RequestMapping(value = "/polyfill.{type}", method = RequestMethod.GET)
    public View polyfillApi(@RequestHeader("User-Agent") String uaString,
                            @RequestParam(value = "features", required = false) String features,
                            @RequestParam(value = "excludes", required = false) String excludes,
                            @RequestParam(value = "flags", required = false) String flags,
                            @PathVariable String type, Model model) {

        if (type.equals("js")) {
            boolean doMinify = false;
            String polyfillsSource = getPolyfillsSource(uaString, features, excludes, flags, doMinify);
            return new PolyfillsView(polyfillsSource, doMinify);
        } else {
            model.addAttribute("message", "Sorry we just support javascript polyfills.");
            return new BadRequestView("badRequest", model);
        }
    }

    @RequestMapping(value = "/polyfill.min.{type}", method = RequestMethod.GET)
    public View polyfillMinApi(@RequestHeader("User-Agent") String uaString,
                               @RequestParam(value = "features", required = false) String features,
                               @RequestParam(value = "excludes", required = false) String excludes,
                               @RequestParam(value = "flags", required = false) String flags,
                               @PathVariable String type, Model model) {

        if (type.equals("js")) {
            boolean doMinify = true;
            String polyfillsSource = getPolyfillsSource(uaString, features, excludes, flags, doMinify);
            return new PolyfillsView(polyfillsSource, doMinify);
        } else {
            model.addAttribute("message", "Sorry we just support javascript polyfills.");
            return new BadRequestView("badRequest", model);
        }
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

    private String getPolyfillsSource(String uaString,
            String features, String excludes, String globalFlags, boolean doMinify) {

        if (features == null) {
            features = "default";
        }

        UserAgent userAgent = userAgentParserService.parse(uaString);
        List<String> excludeList = splitToList(excludes, ",");

        List<FeatureOptions> featureOptionsList = splitToList(features, ",").stream()
                .map(FeatureOptions::new)
                .collect(Collectors.toList());

        if (globalFlags != null) {
            FeatureOptions globalOptions = new FeatureOptions("global", splitToList(globalFlags, ","));
            featureOptionsList.forEach(featureOption -> featureOption.copyOptions(globalOptions));
        }

        return polyfillQueryService.getPolyfillsSource(userAgent, doMinify, featureOptionsList, excludeList);
    }

    private List<String> splitToList(String string, String delimiter) {
        return string == null ? new ArrayList<>() : Arrays.asList(string.split(delimiter));
    }
}
