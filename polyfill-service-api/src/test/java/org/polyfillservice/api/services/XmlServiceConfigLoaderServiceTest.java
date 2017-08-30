package org.polyfillservice.api.services;

import org.junit.Test;
import org.polyfillservice.api.components.PolyfillServiceConfigFileLocation;
import org.polyfillservice.api.components.ServiceConfig;
import org.polyfillservice.api.interfaces.PolyfillServiceConfigLocation;
import org.polyfillservice.api.interfaces.ServiceConfigLoaderService;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by smo on 6/7/17.
 */
public class XmlServiceConfigLoaderServiceTest {

    private final ServiceConfigLoaderService service = new XmlServiceConfigLoaderService();

    /**
     * Should load service config with fields mapped correctly
     */
    @Test
    public void testLoadConfig() {
        PolyfillServiceConfigLocation serviceConfigLocation = new PolyfillServiceConfigFileLocation(
            new File("./src/test/resources/service_configs/config.xml"));
        ServiceConfig actualConfig = service.loadConfig(serviceConfigLocation);
        ServiceConfig expectedConfig = new ServiceConfig.Builder()
            .setPolyfills(Arrays.asList("a.a", "a.b", "c"))
            .setGated(false)
            .setMinify(false)
            .setLoadOnUnknownUA(false)
            .setDebugMode(true)
            .build();
        assertEquals(expectedConfig.toString(), actualConfig.toString());
    }

    /**
     * Should return service config with default settings when unable to load service config
     */
    @Test
    public void testLoadConfigWithInvalidPath() {
        PolyfillServiceConfigLocation serviceConfigLocation = new PolyfillServiceConfigFileLocation(
            new File("wrong/path"));
        ServiceConfig actualConfig = service.loadConfig(serviceConfigLocation);
        ServiceConfig expectedConfig = new ServiceConfig.Builder().build();
        assertEquals(expectedConfig.toString(), actualConfig.toString());
    }

    /**
     * Should return service config with default settings when locator is null
     */
    @Test
    public void testLoadConfigWithNull() {
        ServiceConfig actualConfig = service.loadConfig(null);
        ServiceConfig expectedConfig = new ServiceConfig.Builder().build();
        assertEquals(expectedConfig.toString(), actualConfig.toString());
    }
}
