package org.polyfill.components;

import org.polyfill.interfaces.UserAgent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by smo on 3/28/17.
 */
public class Filters {

    private UserAgent userAgent = null;
    private Set<String> excludes = new HashSet<>();
    private boolean doLoadOnUnknownUA = false;
    private boolean doIncludeDependencies = true;

    public UserAgent getUserAgent() {
        return this.userAgent;
    }

    public Filters setUserAgent(UserAgent userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public Set<String> getExcludes() {
        return this.excludes;
    }

    public Filters excludeFeatures(String ... features) {
        for (String feature : features) {
            this.excludes.add(feature);
        }
        return this;
    }

    public Filters excludeFeatures(List<String> features) {
        this.excludes.addAll(features);
        return this;
    }

    public boolean doLoadOnUnknownUA() {
        return this.doLoadOnUnknownUA;
    }

    public Filters setLoadOnUnknownUA(boolean doLoadOnUnknownUA) {
        this.doLoadOnUnknownUA = doLoadOnUnknownUA;
        return this;
    }

    public boolean doIncludeDependencies() {
        return this.doIncludeDependencies;
    }

    public Filters setIncludeDependencies(boolean doIncludeDependencies) {
        this.doIncludeDependencies = doIncludeDependencies;
        return this;
    }
}
