package org.polyfillservice.api.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by smo on 3/28/17.
 * Config object used to retrieve polyfills
 */
public class Query {
    private List<Feature> features = new ArrayList<>();
    private Set<String> excludes = new HashSet<>();

    // using Boolean object to indicate 3 states: true, false, null(unset)
    private Boolean shouldMinify;
    private Boolean shouldGateForAll;
    private Boolean shouldAlwaysLoadForAll;
    private Boolean shouldLoadOnUnknownUA;
    private Boolean shouldIncludeDependencies;
    private Boolean isDebugMode;

    private Query() {}

    private Query(Query query) {
        this.features = query.features;
        this.excludes = query.excludes;
        this.shouldMinify = query.shouldMinify;
        this.shouldGateForAll = query.shouldGateForAll;
        this.shouldAlwaysLoadForAll = query.shouldAlwaysLoadForAll;
        this.shouldLoadOnUnknownUA = query.shouldLoadOnUnknownUA;
        this.shouldIncludeDependencies = query.shouldIncludeDependencies;
        this.isDebugMode = query.isDebugMode;
    }

    public List<Feature> getFeatures() {
        return this.features;
    }

    public Set<String> getExcludes() {
        return this.excludes;
    }

    public Boolean shouldMinify() {
        return this.shouldMinify;
    }

    public Boolean shouldGateForAll() {
        return this.shouldGateForAll;
    }

    public Boolean shouldAlwaysLoadForAll() {
        return this.shouldAlwaysLoadForAll;
    }

    public Boolean shouldLoadOnUnknownUA() {
        return this.shouldLoadOnUnknownUA;
    }

    public Boolean shouldIncludeDependencies() {
        return this.shouldIncludeDependencies;
    }

    public Boolean isDebugMode() {
        return this.isDebugMode;
    }

    public static class Builder {
        private Query query;

        public Builder() {
            this.query = new Query();
        }

        public Query build() {
            return new Query(this.query);
        }

        public Builder includeFeatures(Collection<Feature> features) {
            if (features != null) {
                this.query.features.addAll(features);
            }
            return this;
        }

        public Builder excludeFeatures(String ... features) {
            if (features != null) {
                this.query.excludes.addAll(Arrays.asList(features));
            }
            return this;
        }

        public Builder excludeFeatures(Collection<String> features) {
            if (features != null) {
                this.query.excludes.addAll(features);
            }
            return this;
        }

        public Builder setMinify(boolean shouldMinify) {
            this.query.shouldMinify = shouldMinify;
            return this;
        }

        public Builder setGatedForAll(boolean shouldGateForAll) {
            this.query.shouldGateForAll = shouldGateForAll;
            return this;
        }

        public Builder setAlwaysLoadForAll(boolean shouldAlwaysLoadForAll) {
            this.query.shouldAlwaysLoadForAll = shouldAlwaysLoadForAll;
            return this;
        }

        public Builder setLoadOnUnknownUA(boolean shouldLoadOnUnknownUA) {
            this.query.shouldLoadOnUnknownUA = shouldLoadOnUnknownUA;
            return this;
        }

        public Builder setIncludeDependencies(boolean shouldIncludeDependencies) {
            this.query.shouldIncludeDependencies = shouldIncludeDependencies;
            return this;
        }

        public Builder setDebugMode(boolean isDebugMode) {
            this.query.isDebugMode = isDebugMode;
            return this;
        }
    }
}
