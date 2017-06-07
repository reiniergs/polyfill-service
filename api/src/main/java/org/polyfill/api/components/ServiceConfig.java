package org.polyfill.api.components;

import java.util.Collections;
import java.util.List;

/**
 * Created by smo on 6/6/17.
 */
public class ServiceConfig {
    private List<String> polyfills;

    public ServiceConfig() {
        this.polyfills = Collections.emptyList();
    }

    public ServiceConfig(List<String> polyfills) {
        this.polyfills = polyfills;
    }

    public List<String> getActivePolyfills() {
        return this.polyfills;
    }
}
