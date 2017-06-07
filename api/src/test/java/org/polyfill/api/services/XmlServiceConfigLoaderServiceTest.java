package org.polyfill.api.services;

import org.junit.Test;
import org.polyfill.api.components.PolyfillServiceConfigLocation;
import org.polyfill.api.components.ServiceConfig;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by smo on 6/7/17.
 */
public class XmlServiceConfigLoaderServiceTest {

    private final XmlServiceConfigLoaderService serviceConfigLoaderService = new XmlServiceConfigLoaderService();

    @Test
    public void testLoadConfig() {
        PolyfillServiceConfigLocation serviceConfigLocation = new PolyfillServiceConfigLocation(
            new File("./src/test/resources/service_configs/config.xml"));
        ServiceConfig actualConfig = serviceConfigLoaderService.loadConfig(serviceConfigLocation);
        ServiceConfig expectedConfig = new ServiceConfig()
            .setPolyfills(Arrays.asList("a.a", "a.b", "c"))
            .setGated(false)
            .setMinify(false)
            .setLoadOnUnknownUA(false);
        assertEquals(expectedConfig.toString(), actualConfig.toString());
    }

    @Test
    public void testLoadConfigWithInvalidPath() {
        PolyfillServiceConfigLocation serviceConfigLocation = new PolyfillServiceConfigLocation(
            new File("wrong/path"));
        ServiceConfig actualConfig = serviceConfigLoaderService.loadConfig(serviceConfigLocation);
        ServiceConfig expectedConfig = new ServiceConfig();
        assertEquals(expectedConfig.toString(), actualConfig.toString());
    }

    @Test
    public void testLoadConfigWithNull() {
        ServiceConfig actualConfig = serviceConfigLoaderService.loadConfig(null);
        ServiceConfig expectedConfig = new ServiceConfig();
        assertEquals(expectedConfig.toString(), actualConfig.toString());
    }
}
