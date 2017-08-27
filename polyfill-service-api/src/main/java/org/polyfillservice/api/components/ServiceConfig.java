package org.polyfillservice.api.components;

import java.util.Collections;
import java.util.List;

/**
 * Created by smo on 6/6/17.
 * Simple object to hold polyfill service configurations.
 */
public class ServiceConfig {

    private List<String> polyfills = Collections.emptyList();
    private boolean shouldGate = true;
    private boolean shouldMinify = true;
    private boolean shouldLoadOnUnknownUA = true;
    private boolean isDebugMode = false;

    // default constructor to be used by builder
    private ServiceConfig() {}

    // copy constructor to be used by builder
    private ServiceConfig(ServiceConfig fromBuilder) {
        this.shouldGate = fromBuilder.shouldGate;
        this.shouldMinify = fromBuilder.shouldMinify;
        this.polyfills = fromBuilder.polyfills;
        this.isDebugMode = fromBuilder.isDebugMode;
        this.shouldLoadOnUnknownUA = fromBuilder.shouldLoadOnUnknownUA;
    }

    public String toString() {
        return "ServiceConfig: {"
                + "\n\tpolyfills: " + this.polyfills
                + ",\n\tshouldGate: " + this.shouldGate
                + ",\n\tshouldMinify: " + this.shouldMinify
                + ",\n\tload-on-unknown-ua: " + this.shouldLoadOnUnknownUA
                + ",\n\tdebug-mode: " + this.isDebugMode
                + "\n}";
    }

    public List<String> getPolyfills() {
        return this.polyfills;
    }

    public boolean shouldGate() {
        return this.shouldGate;
    }

    public boolean shouldMinify() {
        return this.shouldMinify;
    }

    public boolean shouldLoadOnUnknownUA() {
        return this.shouldLoadOnUnknownUA;
    }

    public boolean isDebugMode() {
        return this.isDebugMode;
    }

    public static class Builder {
        private ServiceConfig serviceConfig;

        public Builder() {
            this.serviceConfig = new ServiceConfig();
        }

        public ServiceConfig build() {
            return new ServiceConfig(this.serviceConfig);
        }

        public Builder setPolyfills(List<String> polyfills) {
            this.serviceConfig.polyfills = polyfills;
            return this;
        }

        public Builder setGated(boolean gated) {
            this.serviceConfig.shouldGate = gated;
            return this;
        }

        public Builder setMinify(boolean minify) {
            this.serviceConfig.shouldMinify = minify;
            return this;
        }

        public Builder setLoadOnUnknownUA(boolean loadOnUnknownUA) {
            this.serviceConfig.shouldLoadOnUnknownUA = loadOnUnknownUA;
            return this;
        }

        public Builder setDebugMode(boolean isDebugMode) {
            this.serviceConfig.isDebugMode = isDebugMode;
            return this;
        }
    }
}
