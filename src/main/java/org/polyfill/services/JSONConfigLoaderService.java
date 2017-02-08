package org.polyfill.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.polyfill.interfaces.ConfigLoaderService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bvenkataraman on 10/13/16.
 */
@Service("json")
public class JSONConfigLoaderService implements ConfigLoaderService {


    /**
     * Retrieve JSON config file from specified path and convert it into a Map
     *
     * @param path File path to retrieve the config file (ex. ./polyfills/Element/config.json)
     * @return configMap - Returns the JSON config in a HashMap format
     * @throws IOException throws exception if file is not found at the specified path
     */
    public Map<String, Object> getConfig(String path) throws IOException {

        File configFile = new File(path);
        Map<String,Object> configMap = convertJSONConfigToMap(configFile);

        return configMap;
    }

    /**
     * Converts the JSON file to a HashMap
     *
     * @param JsonConfigFile - config file in a JSON format
     * @return map - HashMap which was converted from the JSON file
     * @throws IOException
     */
    private Map<String, Object> convertJSONConfigToMap(File JsonConfigFile) throws IOException {
        Map<String,Object> map = new ObjectMapper().readValue(JsonConfigFile, HashMap.class);
        return map;
    }
}
