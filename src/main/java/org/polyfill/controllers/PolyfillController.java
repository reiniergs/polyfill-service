package org.polyfill.controllers;

import org.polyfill.components.Feature;
import org.polyfill.components.Filters;
import org.polyfill.interfaces.PolyfillQueryService;
import org.polyfill.interfaces.UserAgent;
import org.polyfill.interfaces.UserAgentParserService;
import org.polyfill.views.BadRequestView;
import org.polyfill.views.NotFoundView;
import org.polyfill.views.PolyfillsView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by reinier.guerra on 10/12/16.
 */
@Controller
@PropertySource("classpath:project.properties")
public class PolyfillController {

    // supported query params
    private static final String FEATURES = "features";
    private static final String EXCLUDES = "excludes";
    private static final String GLOBAL_FLAGS = "flags";
    private static final String UA_OVERRIDE = "ua";
    private static final String UNKNOWN_OVERRIDE = "unknown";

    private static final String ONLY_SUPPORT_JS_MSG = "Sorry we just support javascript polyfills.";

    @Autowired
    UserAgentParserService userAgentParserService;

    @Autowired
    PolyfillQueryService polyfillQueryService;

    @Value("${project.version}")
    private String projectVersion;

    @Value("${project.url}")
    private String projectUrl;

    @RequestMapping(
            value={"polyfill.{doMinify:min}.{type:[^.]+}", "/polyfill.{type:[^.]+}" },
            method = RequestMethod.GET)
    public View polyfillApi(@RequestHeader("User-Agent") String headerUA,
                            @RequestParam Map<String, String> params,
                            @PathVariable Optional<String> doMinify,
                            @PathVariable String type,
                            Model model) {
        if (type.equals("js")) {
            return getPolyfillsView(headerUA, params, doMinify.isPresent());
        } else {
            model.addAttribute("message", ONLY_SUPPORT_JS_MSG);
            return new BadRequestView("badRequest", model);
        }
    }

    @RequestMapping(value = "/notfound", method = RequestMethod.GET)
    public View polyfillNotFound(@RequestHeader("User-Agent") String uaString, Model model) {
        return new NotFoundView("notFound");
    }

    /******************************************************
     * Helpers
     ******************************************************/

    private View getPolyfillsView(String headerUA, Map<String, String> params, boolean doMinify) {

        List<Feature> featuresRequested = getFeatures(params);
        List<String> featuresToExclude = getFeaturesToExclude(params);
        UserAgent userAgent = getUserAgent(headerUA, params);
        boolean loadOnUnknown = getLoadOnUnknown(params);

        Filters filters = new Filters.Builder()
                .userAgent(userAgent)
                .excludeFeatures(featuresToExclude)
                .doLoadOnUnknownUA(loadOnUnknown)
                .build();

        List<Feature> featuresLoaded = polyfillQueryService.getFeatures(featuresRequested, filters);

        return new PolyfillsView(projectVersion, projectUrl,
                userAgent, featuresRequested, featuresLoaded, doMinify);
    }

    private List<Feature> getFeatures(Map<String, String> params) {
        List<Feature> featureList = getList(params, FEATURES, ",").stream()
                .map(Feature::new)
                .collect(Collectors.toList());

        if (params.containsKey(GLOBAL_FLAGS)) {
            Set<String> globalOptions = new HashSet<>(getList(params, GLOBAL_FLAGS, ","));
            featureList.forEach(featureOption -> featureOption.addFlags(globalOptions));
        }

        return featureList;
    }

    private UserAgent getUserAgent(String headerUA, Map<String, String> params) {
        String uaString = params.getOrDefault(UA_OVERRIDE, headerUA);
        return userAgentParserService.parse(uaString);
    }

    private boolean getLoadOnUnknown(Map<String, String> params) {
        return "polyfill".equals(params.get(UNKNOWN_OVERRIDE));
    }

    private List<String> getFeaturesToExclude(Map<String, String> params) {
        return getList(params, EXCLUDES, ",");
    }

    private List<String> getList(Map<String, String> params, String field, String delimiter) {
        return params.containsKey(field)
                ? Arrays.asList(params.get(field).split(delimiter))
                : Collections.emptyList();
    }
}
