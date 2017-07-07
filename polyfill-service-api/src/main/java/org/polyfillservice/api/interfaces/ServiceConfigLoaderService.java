package org.polyfillservice.api.interfaces;

import org.polyfillservice.api.components.ServiceConfig;

/**
 * Created by smo on 6/1/17.
 * Service to load polyfill service/global configurations.
 */
public interface ServiceConfigLoaderService {
    /**
     * Load service configurations from file
     * @param serviceConfigLocation location of service configurations file
     * @return service configuration object
     */
    ServiceConfig loadConfig(PolyfillServiceConfigLocation serviceConfigLocation);
}
