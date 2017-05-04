package org.polyfill.interfaces;

import org.polyfill.components.Query;

/**
 * Created by smo on 4/21/17.
 */
public interface QueryLoaderService {
    /**
     * Load query configuration from a file
     * The query object will be used for getting polyfills
     * @param filePath file path to the configuration file
     * @return query object
     */
    Query loadQuery(String filePath);
}
