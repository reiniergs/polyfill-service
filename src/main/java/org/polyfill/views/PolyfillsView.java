package org.polyfill.views;

import org.springframework.web.servlet.View;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by reinier.guerra on 10/12/16.
 */
public class PolyfillsView implements View {

    private boolean doMinify;
    private String polyfillsSource;

    public PolyfillsView(String polyfillsSource, boolean doMinify) {
        this.polyfillsSource = polyfillsSource;
        this.doMinify = doMinify;
    }

    @Override
    public String getContentType() {
        return "text/javascript;charset=UTF-8";
    }

    @Override
    public void render(Map<String, ?> map, HttpServletRequest request, HttpServletResponse response) {

        String comments = this.doMinify ? "" : getHeaderComments();
        String sources = "".equals(this.polyfillsSource) ? "" : wrapInClosure(this.polyfillsSource);

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType(getContentType());

        try (ServletOutputStream stream = response.getOutputStream()) {
            stream.print(comments);
            stream.print(sources);
        } catch (IOException e) {
            System.err.println("Failed to send response!");
        }
    }

    // wrap source in closure to hide private functions
    private String wrapInClosure(String source) {
        String lf = doMinify ? "" : "\n";
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
