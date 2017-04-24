package org.polyfill.controllers;

import org.polyfill.components.Feature;
import org.polyfill.components.Query;
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
@PropertySource("classpath:config.properties")
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

        Query query = new Query(featuresRequested)
                .setUserAgent(userAgent)
                .setLoadOnUnknownUA(loadOnUnknown)
                .excludeFeatures(featuresToExclude);

        List<Feature> featuresLoaded = polyfillQueryService.getFeatures(query);

        return new PolyfillsView(projectVersion, projectUrl,
                userAgent, featuresRequested, featuresLoaded, doMinify);
    }

    private List<Feature> getFeatures(Map<String, String> params) {
        List<Feature> featureList = getList(params, FEATURES, ",").stream()
                .map(this::parseFeature)
                .collect(Collectors.toList());

        if (featureList.isEmpty()) {
            featureList.add(new Feature("default"));
        }

        if (params.containsKey(GLOBAL_FLAGS)) {
            // use Feature as a utility to set global flags on other features
            List<String> flags = getList(params, GLOBAL_FLAGS, ",");
            Feature global = new Feature("flags", flags.contains(Feature.GATED), flags.contains(Feature.ALWAYS));
            featureList.forEach(featureOption -> featureOption.copyFlags(global));
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

    // e.g. Array.of|always|gated -> ["Array.of", "always", "gated"]
    // index 0 is name, index > 0 is flag
    private Feature parseFeature(String featureQuery) {
        String[] nameAndFlags = featureQuery.split("\\|");
        String name = nameAndFlags[0];
        boolean isGated = false;
        boolean isAlways = false;
        for (int i = 1; i < nameAndFlags.length; i++) {
            switch (nameAndFlags[i]) {
                case Feature.GATED:
                    isGated = true; break;
                case Feature.ALWAYS:
                    isAlways = true; break;
            }
        }
        return new Feature(name, isGated, isAlways);
    }
}
