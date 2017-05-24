package org.polyfill.api.interfaces;

import org.polyfill.api.components.Feature;
import org.polyfill.api.components.Polyfill;
import org.polyfill.api.components.Query;

import java.util.List;
import java.util.Map;

/**
 * Created by smo on 2/4/17.
 * Service to handle polyfill queries.
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
     * @return - a Map with all the polyfill, keys are polyfill names values are instance of Polyfill
     */
    Map<String, Polyfill> getAllPolyfills();

    /**
     * Return a list of features containing all sources of polyfills.
     * Alias feature will expand into specific features.
     * @param query config object with information about what features to get
     * @param userAgentString user agent string; can be in normalized format e.g. chrome/53.0.0
     * @return list of polyfills
     */
    List<Polyfill> getPolyfills(Query query, String userAgentString);

    /**
     * Return a string of the sources of requested polyfills
     * Alias feature will expand into specific features.
     * @param query config object with information about what features to get
     * @param userAgentString user agent string; can be in normalized format e.g. chrome/53.0.0
     * @return source of all requested and valid polyfills
     */
    String getPolyfillsSource(Query query, String userAgentString);

    /**
     * Return a string of the sources of requested polyfills
     * Alias feature will expand into specific features.
     * @param query config object with information about what features to get
     * @param userAgentString user agent string; can be in normalized format e.g. chrome/53.0.0
     * @param isDebugMode whether to add debug information in the header before the polyfills source
     * @return source of all requested and valid polyfills
     */
    String getPolyfillsSource(Query query, String userAgentString, boolean isDebugMode);
}
