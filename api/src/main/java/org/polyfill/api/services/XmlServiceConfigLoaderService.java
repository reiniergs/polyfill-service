package org.polyfill.api.services;

import com.thoughtworks.xstream.XStream;
import org.polyfill.api.components.PolyfillServiceConfigLocation;
import org.polyfill.api.components.ServiceConfig;
import org.polyfill.api.interfaces.ServiceConfigLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by smo on 6/6/17.
 */
@Primary
@Service("xml")
public class XmlServiceConfigLoaderService implements ServiceConfigLoaderService {

    @Autowired(required = false)
    private PolyfillServiceConfigLocation serviceConfigLocation;

    public ServiceConfig loadConfig() {
        if (serviceConfigLocation == null) {
            return new ServiceConfig();
        } else {
            return readSettings(serviceConfigLocation);
        }
    }

    private ServiceConfig readSettings(PolyfillServiceConfigLocation location) {
        XStream xStream = new XStream();
        xStream.alias("configurations", ServiceConfig.class);
        xStream.alias("polyfill", String.class);

        try {
            return (ServiceConfig)xStream.fromXML(location.getInputStream());
        } catch (IOException e) {
            System.err.println("Error trying to load service configuration file. "
                    + "Will use default configurations.");
            e.printStackTrace();
            return new ServiceConfig();
        }
    }
}
