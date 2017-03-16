package org.polyfill.views;

import org.polyfill.components.Feature;
import org.springframework.web.servlet.View;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by reinier.guerra on 10/12/16.
 */
public class PolyfillsView implements View {

    private boolean minify;
    private List<Feature> features;

    public PolyfillsView(List<Feature> features, boolean minify) {
        this.features = features;
        this.minify = minify;
    }

    @Override
    public String getContentType() {
        return "text/javascript;charset=UTF-8";
    }

    @Override
    public void render(Map<String, ?> map, HttpServletRequest request, HttpServletResponse response) {

        String comments = this.minify ? "" : getHeaderComments();
        String sources = toPolyfillsSource(this.features);
        if (!this.minify) {
            sources = wrapInClosure(sources);
        }

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType(getContentType());

        try (ServletOutputStream stream = response.getOutputStream()) {
            stream.print(comments);
            stream.print(sources);
        } catch (IOException e) {
            System.err.println("Failed to send response!");
        }
    }

    /**
     * Concatenate features to source
     * @param features list of feature options
     * @return source of features requested
     */
    private String toPolyfillsSource(List<Feature> features) {
        return features.stream()
                .map(feature -> feature.getSource(this.minify))
                .collect(Collectors.joining());
    }

    // wrap source in closure to hide private functions
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

    // TODO: add header comment
    private String getHeaderComments() {
        return "/* Polyfill service */\n";
    }
}
