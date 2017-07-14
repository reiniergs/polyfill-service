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

    // only construct object via builder
    private ServiceConfig() {}
    private ServiceConfig(ServiceConfig serviceConfig) {
        this.polyfills = serviceConfig.polyfills;
        this.gated = serviceConfig.gated;
        this.minify = serviceConfig.minify;
        this.loadOnUnknownUA = serviceConfig.loadOnUnknownUA;
    }

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

    public boolean shouldGate() {
        return this.gated;
    }

    public boolean shouldMinify() {
        return this.minify;
    }

    public boolean shouldLoadOnUnknownUA() {
        return this.loadOnUnknownUA;
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
            this.serviceConfig.gated = gated;
            return this;
        }

        public Builder setMinify(boolean minify) {
            this.serviceConfig.minify = minify;
            return this;
        }

        public Builder setLoadOnUnknownUA(boolean loadOnUnknownUA) {
            this.serviceConfig.loadOnUnknownUA = loadOnUnknownUA;
            return this;
        }
    }
}
