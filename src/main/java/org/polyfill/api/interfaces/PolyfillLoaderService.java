package org.polyfill.api.interfaces;

import org.polyfill.api.components.Polyfill;

import java.io.IOException;
import java.util.Map;

/**
 * Created by smo on 2/26/17.
 * Service to load and construct polyfill from file system
 */
public interface PolyfillLoaderService {

    /**
     * Load a polyfill from {@code polyfillPath}
     * @param polyfillPath directory containing the polyfill meta
     * @return a polyfill loaded from {@code polyfillPath}
     * @throws IOException if {@code polyfillPath} is a nonexistent directory
     */
    Polyfill loadPolyfill(String polyfillPath) throws IOException;

    /**
     * Load all polyfills within {@code polyfillsPath}
     * @param polyfillsPath directory containing a list of polyfill directories
     * @return map of loaded polyfills
     * @throws IOException if {@code polyfillsPath} is a nonexistent directory
     */
    Map<String, Polyfill> loadPolyfillsToMap(String polyfillsPath) throws IOException;
}
