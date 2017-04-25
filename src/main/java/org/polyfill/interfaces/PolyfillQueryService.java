package org.polyfill.interfaces;

import org.polyfill.components.Feature;
import org.polyfill.components.Query;
import org.polyfill.components.Polyfill;

import java.util.List;
import java.util.Map;

/**
 * Created by smo on 2/4/17.
 * Service to handle polyfill queries.
 */
public interface PolyfillQueryService {

    /**
     * Return a list of features containing all sources of polyfills.
     * Alias feature will expand into specific features.
     * Query fields:
     * - featureList - list of polyfill/alias names with options like always and/or gated
     * - exclude list - list of features to exclude
     * - user agent
     * - whether to load features when user agent is unknown
     * - whether to include dependencies
     * @param query query with information about what features to get
     * @return list of features
     */
    List<Feature> getFeatures(Query query);

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
}
