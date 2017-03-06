package org.polyfill.interfaces;

import org.polyfill.components.FeatureOptions;

import java.util.List;

/**
 * Created by smo on 2/4/17.
 * Service to handle polyfill queries.
 */
public interface PolyfillQueryService {

    /**
     * Return a string containing all sources of polyfills filtered by user agent and exclude list
     * Alias feature in {@code featureOptionsList} will expand into specific features.
     * Dependencies will also be included in the resultant list.
     * @param userAgent user agent object
     * @param doMinify whether to use minified source
     * @param featureOptionsList list of polyfill/alias names with options like always and/or gated
     * @param excludeList list of names of features to exclude
     * @return a string of polyfills' sources
     */
    String getPolyfillsSource(UserAgent userAgent, boolean doMinify, List<FeatureOptions> featureOptionsList, List<String> excludeList);
}
