package org.polyfill.web.views;

import org.polyfill.api.components.Feature;
import org.polyfill.api.components.Query;
import org.polyfill.api.interfaces.UserAgent;
import org.springframework.web.servlet.View;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by reinier.guerra on 10/12/16.
 */
public class PolyfillsView implements View {

    private static final String NO_POLYFILLS_MESSAGE = "/* No polyfills found for current settings */";
    private static final String MIN_MESSAGE = "/* Disable minification (remove `.min` from URL path) for more info */";

    private String projectVersion;
    private String projectUrl;
    private UserAgent userAgent;
    private List<Feature> featuresRequested = new ArrayList<>();
    private List<Feature> featuresLoaded = new ArrayList<>();
    private boolean minify = false;

    public PolyfillsView(String projectVersion, String projectUrl,
            Query query, List<Feature> featuresLoaded) {
        this.projectVersion = projectVersion;
        this.projectUrl = projectUrl;
        this.featuresLoaded = featuresLoaded;
        this.userAgent = query.getUserAgent();
        this.featuresRequested = query.getFeatures();
        this.minify = query.doMinify();
    }

    @Override
    public String getContentType() {
        return "text/javascript;charset=UTF-8";
    }

    @Override
    public void render(Map<String, ?> map, HttpServletRequest request, HttpServletResponse response) {

        String output = getDebugInfo() + getSources();

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType(getContentType());

        try (ServletOutputStream stream = response.getOutputStream()) {
            stream.print(output);
        } catch (IOException e) {
            System.err.println("Failed to send response!");
        }
    }

    /**
     * Build sources of polyfills
     * @return sources of polyfills
     */
    private String getSources() {
        String sources = toPolyfillsSource(this.featuresLoaded);
        if (isEmpty(sources)) {
            return this.minify ? "" : "\n\n" + NO_POLYFILLS_MESSAGE;
        }
        return "\n\n" + wrapInClosure(sources);
    }

    /**
     * Concatenate featuresLoaded to source
     * @param features list of feature options
     * @return source of featuresLoaded requested
     */
    private String toPolyfillsSource(List<Feature> features) {
        return features.stream()
                .map(feature -> feature.getSource(this.minify))
                .collect(Collectors.joining());
    }

    /**
     * Wrap source in closure to hide private functions
     * @return wrapped source
     */
    private String wrapInClosure(String source) {
        String lf = this.minify ? "" : "\n";
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
    private String getDebugInfo() {
        if (this.minify) {
            return MIN_MESSAGE;
        }

        List<String> headers = new ArrayList<>();
        headers.add("Polyfill service v" + this.projectVersion);
        headers.add("For detailed credits and licence information see " + this.projectUrl);

        if (this.userAgent != null) {
            headers.add(""); // new line
            headers.add("UA detected: " + this.userAgent.toString());
        }

        if (!this.featuresRequested.isEmpty()) {
            String featuresRequestedLine = this.featuresRequested.stream()
                    .map(Feature::getName)
                    .collect(Collectors.joining(","));
            headers.add("Features requested: " + featuresRequestedLine);
        }

        if (!this.featuresLoaded.isEmpty()) {
            headers.add(""); // new line
            List<String> featuresLoadedLines = this.featuresLoaded.stream()
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
        if (feature.getLicense() != null) {
            license = ", License: " + feature.getLicense();
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

    private boolean isEmpty(String value) {
        return value == null || "".equals(value);
    }
}
