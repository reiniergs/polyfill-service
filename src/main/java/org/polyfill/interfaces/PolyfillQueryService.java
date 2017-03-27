package org.polyfill.interfaces;

import org.polyfill.components.Feature;
import org.polyfill.components.Polyfill;

import java.util.List;
import java.util.Map;

/**
 * Created by smo on 2/4/17.
 * Service to handle polyfill queries.
 */
public interface PolyfillQueryService {

    /**
     * Return a list of features containing all sources of polyfills filtered by user agent and exclude list
     * Alias feature in {@code featureList} will expand into specific features.
     * Dependencies will also be included in the resultant list.
     * @param userAgent user agent object
     * @param featureList list of polyfill/alias names with options like always and/or gated
     * @param excludeList list of names of features to exclude
     * @param loadOnUnknownUA whether to load default features when user agent is unknown
     * @return a list of features
     */
    List<Feature> getFeatures(UserAgent userAgent,
            List<Feature> featureList, List<String> excludeList, boolean loadOnUnknownUA);


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
