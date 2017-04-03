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
    private Set<String> excludes = new HashSet<>();
    private boolean loadOnUnknownUA = false;

    private Filters(Builder builder) {
        this.userAgent = builder.userAgent;
        this.excludes = builder.excludes;
        this.loadOnUnknownUA = builder.loadOnUnknown;
    }

    public UserAgent getUserAgent() {
        return this.userAgent;
    }

    public Set<String> getExcludes() {
        return this.excludes;
    }

    public boolean getLoadOnUnknownUA() {
        return this.loadOnUnknownUA;
    }

    public static class Builder {
        private UserAgent userAgent;
        private Set<String> excludes = new HashSet<>();
        private boolean loadOnUnknown = false;

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

        public Builder loadOnUnknown(boolean loadOnUnknown) {
            this.loadOnUnknown = loadOnUnknown;
            return this;
        }

        public Builder userAgent(UserAgent userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Filters build() {
            return new Filters(this);
        }
    }
}
