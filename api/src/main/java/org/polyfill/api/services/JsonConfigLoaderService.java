package org.polyfill.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.polyfill.api.interfaces.PolyfillConfigLoaderService;
import org.polyfill.api.interfaces.ResourceLoaderService;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by bvenkataraman on 10/13/16.
 */
@Service("json")
class JsonConfigLoaderService implements PolyfillConfigLoaderService, ResourceLoaderService {

    ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    public Map<String, Object> getConfig(String first, String ... more) throws IOException {
        String resourcePath = Paths.get(first, more).toString();
        Resource config = getResource(resourcePath);
        try (InputStream is = config.getInputStream()) {
            return jsonMapper.readValue(config.getInputStream(), Map.class);
        }
    }
}
