package org.polyfill.components;

import org.polyfill.interfaces.UserAgent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by smo on 3/28/17.
 */
public class Filters {

    private UserAgent userAgent;
    private Set<String> excludes;
    private boolean doLoadOnUnknownUA;
    private boolean doIncludeDependencies;

    private Filters(Builder builder) {
        this.userAgent = builder.userAgent;
        this.excludes = builder.excludes;
        this.doLoadOnUnknownUA = builder.doLoadOnUnknownUA;
        this.doIncludeDependencies = builder.doIncludeDependencies;
    }

    public UserAgent getUserAgent() {
        return this.userAgent;
    }

    public Set<String> getExcludes() {
        return this.excludes;
    }

    public boolean doLoadOnUnknownUA() {
        return this.doLoadOnUnknownUA;
    }

    public boolean doIncludeDependencies() {
        return this.doIncludeDependencies;
    }

    public static class Builder {
        private UserAgent userAgent = null;
        private Set<String> excludes = new HashSet<>();
        private boolean doLoadOnUnknownUA = false;
        private boolean doIncludeDependencies = true;

        public Builder userAgent(UserAgent userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Filters build() {
            return new Filters(this);
        }

        public Builder excludeFeatures(String ... features) {
            for (String feature : features) {
                this.excludes.add(feature);
            }
            return this;
        }

        public Builder excludeFeatures(List<String> features) {
            this.excludes.addAll(features);
            return this;
        }

        public Builder doLoadOnUnknownUA(boolean doLoadOnUnknownUA) {
            this.doLoadOnUnknownUA = doLoadOnUnknownUA;
            return this;
        }

        public Builder doIncludeDependencies(boolean doIncludeDependencies) {
            this.doIncludeDependencies = doIncludeDependencies;
            return this;
        }
    }
}
