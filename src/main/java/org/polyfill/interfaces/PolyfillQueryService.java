package org.polyfill.interfaces;

import org.polyfill.components.Feature;
import org.polyfill.components.Filters;
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
     * Alias feature in {@code featureList} will expand into specific features.
     * Dependencies will also be included in the resultant list.
     * Filters can be applied to the list to remove unwanted features:
     * - user agent
     * - exclude list
     * - whether to load features when user agent is unknown
     * @param featureList list of polyfill/alias names with options like always and/or gated
     * @param filters filters to remove features
     * @return
     */
    List<Feature> getFeatures(List<Feature> featureList, Filters filters);


    /**
     * Return a list of polyfills
     * @param polyfillNames names of requested polyfills
     * @param userAgent user agent for filtering
     * @return a list of polyfills
     */
    List<Polyfill> getPolyfills(List<String> polyfillNames, UserAgent userAgent);

    /**
     * Gets a Polyfill instance by the name of the polyfill.
     * @param name - the name of the polyfill
     * @return a Polyfill or null
     */
    public Polyfill getPolyfill(String name);

    /**
     * Gets all the polyfills
     * @return - a Map with all the polyfill, keys are polyfill names values are instance of Polyfill
     */
    public Map<String, Polyfill> getAllPolyfills();
}
