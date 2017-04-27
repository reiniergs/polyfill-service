package org.polyfill.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.polyfill.api.interfaces.ConfigLoaderService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by bvenkataraman on 10/13/16.
 */
@Service("json")
@Primary
class JSONConfigLoaderService implements ConfigLoaderService {

    @Override
    public Map<String, Object> getConfig(String path) throws IOException {
        File configFile = new File(path);
        Map<String,Object> configMap = convertJSONFileToMap(configFile);
        return configMap;
    }

    /**
     * Converts the JSON file to a HashMap
     *
     * @param jsonFile - config file in a JSON format
     * @return map - HashMap which was converted from the JSON file
     * @throws IOException
     */
    private Map<String, Object> convertJSONFileToMap(File jsonFile) throws IOException {
        return new ObjectMapper().readValue(jsonFile, Map.class);
    }
}
