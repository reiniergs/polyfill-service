package org.polyfill.rest.controllers;

import org.polyfill.api.components.Feature;
import org.polyfill.api.components.Query;
import org.polyfill.api.interfaces.PolyfillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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

    private static final String ONLY_SUPPORT_JS_MSG = "Sorry, we only support javascript polyfills :(";

    @Autowired
    PolyfillService polyfillService;

    @RequestMapping(
            value={"polyfill.{doMinify:min}.{type:[^.]+}", "polyfill.{type:[^.]+}" },
            method = RequestMethod.GET)
    public ResponseEntity polyfillApi(@RequestHeader("User-Agent") String headerUA,
                                      @RequestParam Map<String, String> params,
                                      @PathVariable Optional<String> doMinify,
                                      @PathVariable String type,
                                      Model model) {
        final HttpHeaders httpHeaders= new HttpHeaders();

        if (type.equals("js")) {
            httpHeaders.add("Content-Type", "text/javascript; charset=utf-8");
            httpHeaders.add("Access-Control-Allow-Origin", "*");
            String output = getPolyfillsSource(headerUA, params, doMinify.isPresent());
            return new ResponseEntity<>(output, httpHeaders, HttpStatus.OK);
        } else {
            httpHeaders.add("Content-Type", "text/html; charset=utf-8");
            return new ResponseEntity<>(ONLY_SUPPORT_JS_MSG, httpHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    /******************************************************
     * Helpers
     ******************************************************/

    private String getPolyfillsSource(String headerUA, Map<String, String> params, boolean doMinify) {
        List<Feature> featuresRequested = getFeatures(params);
        List<String> featuresToExclude = getFeaturesToExclude(params);
        String uaString = getUserAgent(headerUA, params);
        boolean loadOnUnknown = getLoadOnUnknown(params);

        Query query = new Query(featuresRequested)
                .setLoadOnUnknownUA(loadOnUnknown)
                .excludeFeatures(featuresToExclude)
                .setMinify(doMinify);

        return polyfillService.getPolyfillsSource(query, uaString, true);
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

    private String getUserAgent(String headerUA, Map<String, String> params) {
        return params.getOrDefault(UA_OVERRIDE, headerUA);
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
