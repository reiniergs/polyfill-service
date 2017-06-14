package org.polyfill.api.services;

import com.thoughtworks.xstream.XStream;
import org.polyfill.api.components.ServiceConfig;
import org.polyfill.api.interfaces.PolyfillServiceConfigLocation;
import org.polyfill.api.interfaces.ServiceConfigLoaderService;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by smo on 6/6/17.
 */
@Service("xml")
public class XmlServiceConfigLoaderService implements ServiceConfigLoaderService {

    @Override
    public ServiceConfig loadConfig(PolyfillServiceConfigLocation serviceConfigLocation) {
        if (serviceConfigLocation != null) {
            XStream xStream = getConfiguredXStream();
            try {
                return (ServiceConfig)xStream.fromXML(serviceConfigLocation.getInputStream());
            } catch (IOException e) {
                System.err.println("Error trying to load service configuration file! "
                        + "Will use default configurations.");
                e.printStackTrace();
            }
        }

        return new ServiceConfig();
    }

    /**
     * Set up xstream to load service configuration xml
     * @return xstream object
     */
    private XStream getConfiguredXStream() {
        XStream xstream = new XStream();
        // only allow ServiceConfig object for xstream to de/serialize due to security reasons
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypes(new Class[]{ServiceConfig.class});

        // map fields to tags for different names
        xstream.alias("configurations", ServiceConfig.class);
        xstream.alias("polyfill", String.class);
        xstream.aliasField("load-on-unknown-ua", ServiceConfig.class, "loadOnUnknownUA");

        return xstream;
    }
}
