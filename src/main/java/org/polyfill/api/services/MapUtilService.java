package org.polyfill.api.services;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by bvenkataraman on 11/14/16.
 */
@Service
class MapUtilService {

    /**
     * Retrieves attribute values from the converted configMap based on keyPath
     *
     * @param configMap - HashMap of the converted config.json file
     * @param keyPath - String containing keys and indices to follow separated by '.' (ex. "my.myBrowser.ios_saf.0")
     * @return object - Object can be of type HashMap or ArrayList or String, depending on the attribute being retrieved
     */
    public Object getFromMap(Map<String,Object> configMap, String... keyPath) {
        String[] keys = keyPath.length == 1 ? keyPath[0].split("\\.") : keyPath;
        Object obj = configMap;
        try {
            for (String key : keys) {
                if(obj instanceof Map) {
                    obj = ((Map<String, Object>) obj).get(key);
                }
                else if(obj instanceof List) {
                    int index = Integer.parseInt(key);
                    obj = ((List<String>) obj).get(index);
                }
            }
        } catch (Exception e) {
            System.err.println("Check if the path \"" + keyPath + "\" is valid");
            return null;
        }
        return obj;
    }
}
