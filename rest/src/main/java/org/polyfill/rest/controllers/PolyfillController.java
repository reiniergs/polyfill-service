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
            value={"polyfill.{minify:min}.{type:[^.]+}", "polyfill.{type:[^.]+}" },
            method = RequestMethod.GET)
    public ResponseEntity polyfillApi(@RequestHeader("User-Agent") String headerUA,
                                      @RequestParam Map<String, String> params,
                                      @PathVariable Optional<String> minify,
                                      @PathVariable String type,
                                      Model model) {
        final HttpHeaders httpHeaders= new HttpHeaders();

        if (type.equals("js")) {
            httpHeaders.add("Content-Type", "text/javascript; charset=utf-8");
            httpHeaders.add("Access-Control-Allow-Origin", "*");
            String output = getPolyfillsSource(headerUA, params, minify.isPresent());
            return new ResponseEntity<>(output, httpHeaders, HttpStatus.OK);
        } else {
            httpHeaders.add("Content-Type", "text/html; charset=utf-8");
            return new ResponseEntity<>(ONLY_SUPPORT_JS_MSG, httpHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    /******************************************************
     * Helpers
     ******************************************************/

    private String getPolyfillsSource(String headerUA, Map<String, String> params, boolean minify) {
        List<Feature> featuresRequested = getFeatures(params);
        List<String> featuresToExclude = getFeaturesToExclude(params);
        String uaString = getUserAgent(headerUA, params);
        boolean loadOnUnknown = getLoadOnUnknown(params);
        List<String> globalFlags = getList(params, GLOBAL_FLAGS, ",");
        boolean isAlwaysForAll = globalFlags.contains(Feature.ALWAYS);
        boolean isGatedForAll = globalFlags.contains(Feature.GATED);

        Query query = new Query.Builder(featuresRequested)
            .setLoadOnUnknownUA(loadOnUnknown)
            .excludeFeatures(featuresToExclude)
            .setMinify(minify)
            .setAlwaysForAll(isAlwaysForAll)
            .setGatedForAll(isGatedForAll)
            .build();

        return polyfillService.getPolyfillsSource(uaString, query, true);
    }

    private List<Feature> getFeatures(Map<String, String> params) {
        List<Feature> featureList = getList(params, FEATURES, ",").stream()
                .map(this::parseFeature)
                .collect(Collectors.toList());

        if (featureList.isEmpty()) {
            featureList.add(new Feature("default"));
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
        List<String> nameAndFlags = Arrays.asList(featureQuery.split("\\|"));
        String name = nameAndFlags.get(0);
        boolean isGated = nameAndFlags.contains(Feature.GATED);
        boolean isAlways = nameAndFlags.contains(Feature.ALWAYS);
        return new Feature(name, isGated, isAlways);
    }
}
