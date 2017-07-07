package org.polyfillservice.api.interfaces;

import org.polyfillservice.api.components.Polyfill;

import java.util.List;
import java.util.Map;

/**
 * Created by smo on 2/26/17.
 * Service to load and construct polyfill objects.
 */
public interface PolyfillLoaderService {

    /**
     * Load all polyfills within {@code polyfillsPath}
     * @param locations directories containing a list of polyfill directories
     * @return map of loaded polyfills; empty map if locations are invalid
     */
    Map<String, Polyfill> loadPolyfills(List<PolyfillLocation> locations);
}
