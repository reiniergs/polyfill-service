package org.polyfill.interfaces;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bvenkataraman on 10/19/16.
 */
public interface ConfigLoaderServiceInterface {

    HashMap<String, Object> getConfig(String path) throws Exception;

    Object getInConfig(HashMap<String, Object> configMap, String keyPath) throws Exception;

}
