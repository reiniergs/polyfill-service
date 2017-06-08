package org.polyfill.api.components;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by smo on 3/28/17.
 * Config object used to retrieve polyfills
 */
public class Query {
    // features to request
    private List<Feature> features;

    // filters
    private Set<String> excludes = new HashSet<>();
    private boolean loadOnUnknownUA = true;
    private boolean minify = true;
    private boolean gatedForAll = true;
    private boolean alwaysForAll = false;

    // extra options
    private boolean includeDependencies = true;

    private Query(Builder builder) {
        this.features = builder.features;
        this.excludes = builder.excludes;
        this.loadOnUnknownUA = builder.loadOnUnknownUA;
        this.minify = builder.minify;
        this.gatedForAll = builder.gatedForAll;
        this.alwaysForAll = builder.alwaysForAll;
        this.includeDependencies = builder.includeDependencies;
    }

    public List<Feature> getFeatures() {
        return this.features;
    }

    public Set<String> getExcludes() {
        return this.excludes;
    }

    public boolean shouldLoadOnUnknownUA() {
        return this.loadOnUnknownUA;
    }

    public boolean shouldIncludeDependencies() {
        return this.includeDependencies;
    }

    public boolean shouldMinify() {
        return this.minify;
    }

    public boolean isGatedForAll() {
        return this.gatedForAll;
    }

    public boolean isAlwaysForAll() {
        return this.alwaysForAll;
    }

    public static class Builder {
        // features to request
        private List<Feature> features;

        // filters
        private Set<String> excludes = new HashSet<>();
        private boolean loadOnUnknownUA = true;
        private boolean minify = true;
        private boolean gatedForAll = true;
        private boolean alwaysForAll = false;

        // extra options
        private boolean includeDependencies = true;

        public Builder(List<Feature> features) {
            this.features = features;
            this.excludes = new HashSet<>();
            this.loadOnUnknownUA = true;
            this.minify = true;
            this.gatedForAll = true;
            this.alwaysForAll = false;
            this.includeDependencies = true;
        }

        public Query build() {
            return new Query(this);
        }

        public Builder excludeFeatures(String ... features) {
            return excludeFeatures(Arrays.asList(features));
        }

        public Builder excludeFeatures(List<String> features) {
            this.excludes.addAll(features);
            return this;
        }

        public Builder setLoadOnUnknownUA(boolean loadOnUnknownUA) {
            this.loadOnUnknownUA = loadOnUnknownUA;
            return this;
        }

        public Builder setMinify(boolean minify) {
            this.minify = minify;
            return this;
        }

        public Builder setAlwaysForAll(boolean alwaysForAll) {
            this.alwaysForAll = alwaysForAll;
            return this;
        }

        public Builder setGatedForAll(boolean gatedForAll) {
            this.gatedForAll = gatedForAll;
            return this;
        }

        public Builder setIncludeDependencies(boolean includeDependencies) {
            this.includeDependencies = includeDependencies;
            return this;
        }
    }
}
