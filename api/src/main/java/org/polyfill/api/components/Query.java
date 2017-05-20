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
    private boolean doLoadOnUnknownUA = false;
    private boolean doMinify = false;
    private boolean isGatedForAll = false;
    private boolean isAlwaysForAll = false;

    // extra options
    private boolean doIncludeDependencies = true;

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

    public boolean doLoadOnUnknownUA() {
        return this.doLoadOnUnknownUA;
    }

    public Query setLoadOnUnknownUA(boolean doLoadOnUnknownUA) {
        this.doLoadOnUnknownUA = doLoadOnUnknownUA;
        return this;
    }

    public boolean doIncludeDependencies() {
        return this.doIncludeDependencies;
    }

    public Query setIncludeDependencies(boolean doIncludeDependencies) {
        this.doIncludeDependencies = doIncludeDependencies;
        return this;
    }

    public boolean doMinify() {
        return this.doMinify;
    }

    public Query setMinify(boolean doMinify) {
        this.doMinify = doMinify;
        return this;
    }

    public boolean isGatedForAll() {
        return this.isGatedForAll;
    }

    public Query setGatedForAll(boolean isGatedForAll) {
        this.isGatedForAll = isGatedForAll;
        return this;
    }

    public boolean isAlwaysForAll() {
        return isAlwaysForAll;
    }

    public Query setAlwaysForAll(boolean isAlwaysForAll) {
        this.isAlwaysForAll = isAlwaysForAll;
        return this;
    }
}
