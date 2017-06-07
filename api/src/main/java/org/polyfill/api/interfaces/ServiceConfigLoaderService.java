package org.polyfill.api.interfaces;

import org.polyfill.api.components.PolyfillServiceConfigLocation;
import org.polyfill.api.components.ServiceConfig;

/**
 * Created by smo on 6/1/17.
 * Service to load polyfill service/global configurations.
 */
public interface ServiceConfigLoaderService {
    /**
     * Load service configurations from file
     * @return service configuration object
     */
    ServiceConfig loadConfig(PolyfillServiceConfigLocation serviceConfigLocation);
}
