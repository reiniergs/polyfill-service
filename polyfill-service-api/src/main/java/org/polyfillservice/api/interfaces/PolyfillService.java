package org.polyfillservice.api.interfaces;

import org.polyfillservice.api.components.Polyfill;
import org.polyfillservice.api.components.Query;

import java.util.List;
import java.util.Map;

/**
 * Created by smo on 2/4/17.
 * API Service to handle polyfill queries.
 */
public interface PolyfillService {

    /**
     * Gets a Polyfill instance by the name of the polyfill.
     * @param name - the name of the polyfill
     * @return a Polyfill or null
     */
    Polyfill getPolyfill(String name);

    /**
     * Gets all the polyfills
     * @return - a Map with all the polyfill, keys are polyfill names values are instances of polyfill
     */
    Map<String, Polyfill> getAllPolyfills();

    /**
     * Return a list of polyfills containing all sources of polyfills using default query setting
     * Alias polyfill will expand into specific polyfills.
     * @param uaString user agent string; can be in normalized format e.g. chrome/53.0.0
     * @return list of polyfills
     */
    List<Polyfill> getPolyfills(String uaString);

    /**
     * Return a list of polyfills containing all sources of polyfills.
     * Alias polyfill will expand into specific polyfills.
     * @param uaString user agent string; can be in normalized format e.g. chrome/53.0.0
     * @param query config object with information about what polyfills to get
     * @return list of polyfills
     */
    List<Polyfill> getPolyfills(String uaString, Query query);

    /**
     * Return a string of the sources of requested polyfills using default query setting
     * @param uaString user agent string; can be in normalized format e.g. chrome/53.0.0
     * @return source of all requested and valid polyfills
     */
    String getPolyfillsSource(String uaString);

    /**
     * Return a string of the sources of requested polyfills
     * Alias polyfill will expand into specific polyfills.
     * @param uaString user agent string; can be in normalized format e.g. chrome/53.0.0
     * @param query config object with information about what polyfills to get
     * @return source of all requested and valid polyfills
     */
    String getPolyfillsSource(String uaString, Query query);

    /**
     * Return a string of the sources of requested polyfills
     * Alias polyfill will expand into specific polyfills.
     * @param uaString user agent string; can be in normalized format e.g. chrome/53.0.0
     * @param query config object with information about what polyfills to get
     * @param isDebugMode whether to add debug information in the header before the polyfills source
     * @return source of all requested and valid polyfills
     */
    String getPolyfillsSource(String uaString, Query query, boolean isDebugMode);
}
