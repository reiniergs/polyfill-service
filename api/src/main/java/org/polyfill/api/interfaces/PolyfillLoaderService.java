package org.polyfill.api.interfaces;

import org.polyfill.api.components.Polyfill;

import java.io.IOException;
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
     * @return map of loaded polyfills
     * @throws IOException if {@code polyfillsPath} is a nonexistent directory
     */
    Map<String, Polyfill> loadPolyfills(List<PolyfillLocation> locations) throws IOException;
}
