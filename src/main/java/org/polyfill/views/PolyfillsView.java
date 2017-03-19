package org.polyfill.views;

import org.polyfill.components.Feature;
import org.polyfill.interfaces.UserAgent;
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

    private String projectVersion;
    private String projectUrl;
    private UserAgent userAgent;
    private List<Feature> featuresRequested = new ArrayList<>();
    private List<Feature> featuresLoaded = new ArrayList<>();
    private boolean minify = false;

    private static final String NO_POLYFILLS_MESSAGE = "/* No polyfills found for current settings */";

    public PolyfillsView(String projectVersion, String projectUrl, UserAgent userAgent,
            List<Feature> featuresRequested, List<Feature> featuresLoaded, boolean minify) {
        this.projectVersion = projectVersion;
        this.projectUrl = projectUrl;
        this.userAgent = userAgent;
        this.featuresRequested = featuresRequested;
        this.featuresLoaded = featuresLoaded;
        this.minify = minify;
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
        if (!this.minify) {
            sources = "".equals(sources) ? NO_POLYFILLS_MESSAGE : wrapInClosure(sources);
        }
        return sources;
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
        String lf = minify ? "" : "\n";
        return "(function(undefined) {" + lf
                    + source
                + "})" + lf
                + ".call("
                    + "'object' === typeof window && window" // bind `this` to window in a browser
                    + "|| 'object' === typeof self && self" // bind `this` to self in a web worker
                    + "|| 'object' === typeof global && global" // bind `this` to global in Node
                    + "|| {}"
                + ");" + lf;
    }

    /**
     * Build header comments for debugging
     * @return header comments containing debug info
     */
    private String getDebugInfo() {
        if (this.minify) {
            return "";
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
        return "/* " + comments + "\n */\n\n";
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
}
