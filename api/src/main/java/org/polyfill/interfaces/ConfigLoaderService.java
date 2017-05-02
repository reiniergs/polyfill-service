package org.polyfill.interfaces;

import java.io.IOException;
import java.util.Map;

/**
 * Created by bvenkataraman on 10/19/16.
 */
public interface ConfigLoaderService {

    /**
     * Retrieve a JSON config file from specified path and convert it into a Map
     *
     * @param first first segment of the config file path
     * @param more rest of the segments of the config file path
     * @return configMap - Returns the JSON config in a HashMap format
     * @throws IOException throws exception if file is not found at the specified path
     */
    Map<String, Object> getConfig(String first, String ... more) throws IOException;
}
