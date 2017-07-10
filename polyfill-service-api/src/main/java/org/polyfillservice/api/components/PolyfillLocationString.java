package org.polyfillservice.api.components;

import org.polyfillservice.api.interfaces.PolyfillLocation;

/**
 * Created by smo on 6/17/17.
 */
public class PolyfillLocationString implements PolyfillLocation {
    private String path;
    public PolyfillLocationString(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return this.path;
    }
}
