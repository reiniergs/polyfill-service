package org.polyfill.api.services;

import org.polyfill.api.components.Feature;
import org.polyfill.api.components.Polyfill;
import org.polyfill.api.components.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by smo on 4/27/17.
 */
@Service
class PolyfillsOutputService {

    private static final String NO_POLYFILLS_MESSAGE = "/* No polyfills found for current settings */";
    private static final String MIN_MESSAGE = "/* Disable minification (remove `.min` from URL path) for more info */";

    @Resource(name = "projectVersion")
    private String projectVersion;

    @Resource(name = "projectUrl")
    private String projectUrl;

    public String getPolyfillsSource(String uaString, Query query,
                                     List<Feature> featuresLoaded, boolean isDebugMode) {
        String debugInfo = isDebugMode ? getDebugInfo(uaString, query, featuresLoaded) : "";
        String sources = getSources(featuresLoaded, query.shouldMinify());
        return !debugInfo.isEmpty() && !sources.isEmpty()
                ? debugInfo + "\n\n" + sources
                : debugInfo + sources;
    }

    /**
     * Build sources of polyfills
     * @return sources of polyfills
     */
    private String getSources(List<Feature> features, boolean minify) {
        String sources = features.stream()
                .map(feature -> getSource(feature, minify))
                .collect(Collectors.joining());
        if (sources.isEmpty()) {
            return minify ? "" : NO_POLYFILLS_MESSAGE;
        }
        return wrapInClosure(sources, minify);
    }

    /**
     * Get source of a feature
     * @param feature feature
     * @param minify whether to minify source
     * @return source of feature
     */
    private String getSource(Feature feature, boolean minify) {
        Polyfill polyfill = feature.getPolyfill();
        String detectSource = polyfill.getDetectSource();
        String source = minify ? polyfill.getMinSource() : polyfill.getRawSource();
        source = (source == null) ? "" : source;

        boolean wrapInDetect = feature.isGated() && detectSource != null;

        if (wrapInDetect && !"".equals(detectSource)) {
            String lf = minify ? "" : "\n";
            return "if(!(" + detectSource + ")){" + lf + source + lf + "}" + lf + lf;
        }

        return source;
    }

    /**
     * Wrap source in closure to hide private functions
     * @return wrapped source
     */
    private String wrapInClosure(String source, boolean minify) {
        String lf = minify ? "" : "\n";
        return "(function(undefined) {" + lf + source + "})" + lf
                + ".call('object' === typeof window && window" // bind `this` to window in a browser
                + " || 'object' === typeof self && self"       // bind `this` to self in a web worker
                + " || 'object' === typeof global && global"   // bind `this` to global in Node
                + " || {});";
    }

    /**
     * Build header comments for debugging
     * @return header comments containing debug info
     */
    private String getDebugInfo(String uaString, Query query, List<Feature> featuresLoaded) {
        if (query.shouldMinify()) {
            return MIN_MESSAGE;
        }

        List<String> headers = new ArrayList<>();
        headers.add("Polyfill service v" + this.projectVersion);
        headers.add("For detailed credits and licence information see " + this.projectUrl);

        if (uaString != null) {
            headers.add(""); // new line
            headers.add("UA detected: " + uaString);
        }

        if (!query.getFeatures().isEmpty()) {
            String featuresRequestedLine = query.getFeatures().stream()
                    .map(Feature::getName)
                    .collect(Collectors.joining(","));
            headers.add("Features requested: " + featuresRequestedLine);
        }

        if (!featuresLoaded.isEmpty()) {
            headers.add(""); // new line
            List<String> featuresLoadedLines = featuresLoaded.stream()
                    .map(this::formatFeatureLoaded)
                    .collect(Collectors.toList());
            headers.addAll(featuresLoadedLines);
        }

        return buildCommentBlock(headers);
    }

    /**
     * Wrap lines with comment block
     * @return lines wrapped in a comment block
     */
    private String buildCommentBlock(List<String> lines) {
        String comments = lines.stream().collect(Collectors.joining("\n * "));
        return "/* " + comments + " */";
    }

    /**
     * Format feature loaded with license and reverse dependencies
     * @param feature feature to format
     * @return formatted line with the feature's info
     */
    private String formatFeatureLoaded(Feature feature) {
        String license = "";
        Polyfill polyfill = feature.getPolyfill();
        if (polyfill.getLicense() != null) {
            license = ", License: " + polyfill.getLicense();
        }

        String relatedFeatures = "";
        if (!feature.getRequiredBys().isEmpty()) {
            relatedFeatures = feature.getRequiredBys().stream()
                    .map(featureName -> "\"" + featureName + "\"")
                    .collect(Collectors.joining(", "));
            relatedFeatures = " (required by " + relatedFeatures + ")";
        }

        return "- " + feature.getName() + license + relatedFeatures;
    }
}
