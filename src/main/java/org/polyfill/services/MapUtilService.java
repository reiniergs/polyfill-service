package org.polyfill.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by bvenkataraman on 11/14/16.
 */

@Service
public class MapUtilService {

    /**
     * Retrieves attribute values from the converted configMap based on keyPath
     *
     * @param configMap - HashMap of the converted config.json file
     * @param keyPath - String containing keys and indices to follow separated by '.' (ex. "my.myBrowser.ios_saf.0")
     * @return object - Object can be of type HashMap or ArrayList or String, depending on the attribute being retrieved
     */
    public Object getFromMap(Map<String,Object> configMap, String keyPath) {
        String[] keys = keyPath.split("\\.");
        Object obj = configMap.get(keys[0]);

        int i = 1;
        while (i < keys.length) {
            try {
                if(obj instanceof Map) {
                    obj = ((Map<String, Object>) obj).get(keys[i]);
                }
                else if(obj instanceof List) {
                    int index = Integer.parseInt(keys[i]);
                    obj = ((List<String>) obj).get(index);
                }
                i++;
            }
            catch (Exception e) {
                System.err.println(e.toString() + "\nCheck if the path \"" + keyPath + "\" is valid");
                return null;
            }
        }
        return obj;
    }

    public void prettyPrintMap(Map map) {
        try {
            System.out.println(new ObjectMapper().writeValueAsString(map));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
