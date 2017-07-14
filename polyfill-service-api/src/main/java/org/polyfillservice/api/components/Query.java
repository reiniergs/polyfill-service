package org.polyfillservice.api.components;

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

    private Query() {}

    private Query(Query query) {
        this.features = query.features;
        this.excludes = query.excludes;
        this.loadOnUnknownUA = query.loadOnUnknownUA;
        this.minify = query.minify;
        this.gatedForAll = query.gatedForAll;
        this.alwaysForAll = query.alwaysForAll;
        this.includeDependencies = query.includeDependencies;
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
        private Query query;

        public Builder(List<Feature> features) {
            this.query = new Query();
            this.query.features = features;
        }

        public Query build() {
            return new Query(this.query);
        }

        public Builder excludeFeatures(String ... features) {
            return excludeFeatures(Arrays.asList(features));
        }

        public Builder excludeFeatures(List<String> features) {
            this.query.excludes.addAll(features);
            return this;
        }

        public Builder setLoadOnUnknownUA(boolean loadOnUnknownUA) {
            this.query.loadOnUnknownUA = loadOnUnknownUA;
            return this;
        }

        public Builder setMinify(boolean minify) {
            this.query.minify = minify;
            return this;
        }

        public Builder setAlwaysForAll(boolean alwaysForAll) {
            this.query.alwaysForAll = alwaysForAll;
            return this;
        }

        public Builder setGatedForAll(boolean gatedForAll) {
            this.query.gatedForAll = gatedForAll;
            return this;
        }

        public Builder setIncludeDependencies(boolean includeDependencies) {
            this.query.includeDependencies = includeDependencies;
            return this;
        }
    }
}
