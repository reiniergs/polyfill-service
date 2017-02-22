package org.polyfill.interfaces;

import org.polyfill.components.Polyfill;

import java.util.List;

/**
 * Created by smo on 2/4/17.
 */
// TODO: need to decide the shape later
public interface PolyfillQueryService {

    /**
     * Filter polyfill list by user agent and order it by dependencies
     * @param userAgent user agent object
     * @return the filtered list
     */
    List<Polyfill> getPolyfillsByUserAgent(UserAgent userAgent);

    /**
     * Filter polyfill list by alias and user agent and order it by dependencies
     * @param userAgent user agent object
     * @param featureNames list of feature group alias names or feature names, delimited by ","
     *                     e.g. "es6,es5,Array.of"
     * @return the filtered list
     */
    List<Polyfill> getPolyfillsByFeatures(UserAgent userAgent, String[] featureNames);
}
