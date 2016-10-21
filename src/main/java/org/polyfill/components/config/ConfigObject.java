package org.polyfill.components.config;

import org.polyfill.services.JSONConfigLoaderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

/**
 * Created by bvenkataraman on 10/21/16.
 */
public class ConfigObject {

    private HashMap<String,Object> configMap = new HashMap<String, Object>();

    @Autowired
    JSONConfigLoaderService jsonConfigLoaderService;

    public void getConfig(String path) throws Exception {
        configMap = jsonConfigLoaderService.getConfig(path);
    }

    public Object getIn(String keyPath) throws Exception {
        return jsonConfigLoaderService.getInConfig(configMap, keyPath);
    }

}
