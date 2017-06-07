package org.polyfill.api.components;

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

    public List<String> getActivePolyfills() {
        return this.polyfills;
    }

    public boolean shouldGate() {
        return this.gated;
    }

    public boolean shouldMinify() {
        return this.minify;
    }

    public boolean shouldLoadOnUnknownUA() {
        return this.loadOnUnknownUA;
    }

    public String toString() {
        return "ServiceConfig: {"
                + "\n\tpolyfills: " + this.polyfills
                + ",\n\tgated: " + this.gated
                + ",\n\tminify: " + this.minify
                + ",\n\tload-on-unknown-ua: " + this.loadOnUnknownUA
                + "\n}";
    }
}
