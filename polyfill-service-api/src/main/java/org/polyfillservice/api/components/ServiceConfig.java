package org.polyfillservice.api.components;

import java.util.Collections;
import java.util.List;

/**
 * Created by smo on 6/6/17.
 * Simple object to hold polyfill service configurations.
 */
public class ServiceConfig {
    private List<String> polyfills = Collections.emptyList();
    private boolean gated = true;
    private boolean minify = true;
    private boolean loadOnUnknownUA = true;

    public String toString() {
        return "ServiceConfig: {"
                + "\n\tpolyfills: " + this.polyfills
                + ",\n\tgated: " + this.gated
                + ",\n\tminify: " + this.minify
                + ",\n\tload-on-unknown-ua: " + this.loadOnUnknownUA
                + "\n}";
    }

    public List<String> getPolyfills() {
        return this.polyfills;
    }

    public ServiceConfig setPolyfills(List<String> polyfills) {
        this.polyfills = polyfills;
        return this;
    }

    public boolean shouldGate() {
        return this.gated;
    }

    public ServiceConfig setGated(boolean gated) {
        this.gated = gated;
        return this;
    }

    public boolean shouldMinify() {
        return this.minify;
    }

    public ServiceConfig setMinify(boolean minify) {
        this.minify = minify;
        return this;
    }

    public boolean shouldLoadOnUnknownUA() {
        return this.loadOnUnknownUA;
    }

    public ServiceConfig setLoadOnUnknownUA(boolean loadOnUnknownUA) {
        this.loadOnUnknownUA = loadOnUnknownUA;
        return this;
    }
}
