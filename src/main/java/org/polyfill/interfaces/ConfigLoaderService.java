package org.polyfill.interfaces;

import java.io.IOException;
import java.util.Map;

/**
 * Created by bvenkataraman on 10/19/16.
 */
public interface ConfigLoaderService {
    Map<String, Object> getConfig(String path) throws IOException;
}
