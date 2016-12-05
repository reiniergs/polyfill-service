package org.polyfill.services;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bvenkataraman on 11/14/16.
 */

@Service
public class HashMapUtilService {

    /**
     * Retrieves attribute values from the converted configMap based on keyPath
     *
     * @param configMap - HashMap of the converted config.json file
     * @param keyPath - String containing keys and indices to follow separated by '.' (ex. "my.myBrowser.ios_saf.0")
     * @return object - Object can be of type HashMap or ArrayList or String, depending on the attribute being retrieved
     * @throws Exception
     */
    public static Object getFromMap(HashMap<String,Object> configMap, String keyPath) throws Exception {
        String[] keys = keyPath.split("\\.");
        Object obj = configMap.get(keys[0]);

        int i = 1;
        while (i < keys.length) {

            try {
                if(obj instanceof Map) {
                    obj = ((HashMap<String, Object>) obj).get(keys[i]);
                }
                else if(obj instanceof List) {
                    int index = Integer.parseInt(keys[i]);
                    obj = ((ArrayList<String>) obj).get(index);
                }

                i++;
            }
            catch (Exception e) {
                System.out.println(e.toString() + "\nCheck if the path \"" + keyPath + "\" is valid");
                return null;
            }
        }
        return obj;
    }

}
