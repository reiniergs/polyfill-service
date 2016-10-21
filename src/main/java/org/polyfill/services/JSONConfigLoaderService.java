package org.polyfill.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.polyfill.interfaces.ConfigLoaderServiceInterface;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bvenkataraman on 10/13/16.
 */
@Service
public class JSONConfigLoaderService implements ConfigLoaderServiceInterface{


    /**
     * Converts the JSON file to a HashMap
     *
     * @param JsonConfigFile - config file in a JSON format
     * @return map - HashMap which was converted from the JSON file
     * @throws IOException
     */
    private HashMap<String, Object> convertJSONConfigToMap(File JsonConfigFile) throws IOException{
        System.out.println(JsonConfigFile.isFile());
        HashMap<String,Object> map = new ObjectMapper().readValue(JsonConfigFile, HashMap.class);
        return map;
    }

    /**
     * Retrieve JSON config file from specified path and convert it into a Map
     *
     * @param path - File path to retrieve the config file (ex. ./polyfills/Element/config.json)
     * @return configMap - Returns the JSON config in a HashMap format
     * @throws Exception
     */
    public HashMap<String, Object> getConfig(String path) throws Exception {

        if(!path.contains("config")) {
            throw new Exception("Not a config file");
        }

        File configFile = new File(path);
        HashMap<String,Object> configMap = convertJSONConfigToMap(configFile);

        return configMap;
    }

    /**
     * Retrieves attribute values from the converted configMap based on keyPath
     *
     * @param configMap - HashMap of the converted config.json file
     * @param keyPath - String containing keys and indices to follow separated by '.' (ex. "my.myBrowser.ios_saf.0")
     * @return object - Object can be of type HashMap or ArrayList or String, depending on the attribute being retrieved
     * @throws Exception
     */
    public Object getInConfig(HashMap<String,Object> configMap, String keyPath) throws Exception {
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
