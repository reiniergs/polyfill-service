package org.polyfill.api.interfaces;

import org.polyfill.api.components.Query;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by smo on 4/21/17.
 */
public interface QueryLoaderService {
    /**
     * Load query configuration from a file
     * The query object will be used for getting polyfills
     * @param filePath file path to the configuration file
     * @return query object
     * @throws IOException if resource doesn't exist, is invalid, or cannot be parsed
     */
    Query loadQuery(String filePath) throws IOException;

    /**
     * Load query configuration from an input stream
     * The query object will be used for getting polyfills
     * @param inputStream input stream of query configuration
     * @return query object
     * @throws IOException if inputStream is invalid, or cannot be parsed
     */
    Query loadQuery(InputStream inputStream) throws IOException;
}
