package org.polyfill.api.components;

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

    public Query(List<Feature> features) {
        this.features = features;
    }

    public List<Feature> getFeatures() {
        return this.features;
    }

    public Set<String> getExcludes() {
        return this.excludes;
    }

    public Query excludeFeatures(String ... features) {
        for (String feature : features) {
            this.excludes.add(feature);
        }
        return this;
    }

    public Query excludeFeatures(List<String> features) {
        this.excludes.addAll(features);
        return this;
    }

    public boolean shouldLoadOnUnknownUA() {
        return this.loadOnUnknownUA;
    }

    public Query setLoadOnUnknownUA(boolean loadOnUnknownUA) {
        this.loadOnUnknownUA = loadOnUnknownUA;
        return this;
    }

    public boolean shouldIncludeDependencies() {
        return this.includeDependencies;
    }

    public Query setIncludeDependencies(boolean includeDependencies) {
        this.includeDependencies = includeDependencies;
        return this;
    }

    public boolean shouldMinify() {
        return this.minify;
    }

    public Query setMinify(boolean minify) {
        this.minify = minify;
        return this;
    }

    public boolean isGatedForAll() {
        return this.gatedForAll;
    }

    public Query setGatedForAll(boolean gatedForAll) {
        this.gatedForAll = gatedForAll;
        return this;
    }

    public boolean isAlwaysForAll() {
        return this.alwaysForAll;
    }

    public Query setAlwaysForAll(boolean alwaysForAll) {
        this.alwaysForAll = alwaysForAll;
        return this;
    }
}
