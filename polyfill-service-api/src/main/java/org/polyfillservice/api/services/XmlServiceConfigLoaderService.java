package org.polyfillservice.api.services;

import com.thoughtworks.xstream.XStream;
import org.polyfillservice.api.components.ServiceConfig;
import org.polyfillservice.api.interfaces.PolyfillServiceConfigLocation;
import org.polyfillservice.api.interfaces.ServiceConfigLoaderService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by smo on 6/6/17.
 */
@Service("xml")
public class XmlServiceConfigLoaderService implements ServiceConfigLoaderService {

    @Override
    public ServiceConfig loadConfig(PolyfillServiceConfigLocation serviceConfigLocation) {
        if (serviceConfigLocation != null) {
            XStream xStream = getConfiguredXStream();
            try (InputStream is = serviceConfigLocation.getInputStream()) {
                return (ServiceConfig)xStream.fromXML(is);
            } catch (IOException e) {
                System.err.println("Error trying to load service configuration file! "
                        + "Will use default configurations.");
                e.printStackTrace();
            }
        }

        return new ServiceConfig.Builder().build();
    }

    /**
     * Set up xstream to load service configuration xml
     * @return xstream object
     */
    private XStream getConfiguredXStream() {
        XStream xstream = new XStream();

        // map fields to tags for different names
        xstream.alias("configurations", ServiceConfig.class);
        xstream.alias("polyfill", String.class);
        xstream.aliasField("load-on-unknown-ua", ServiceConfig.class, "loadOnUnknownUA");

        return xstream;
    }
}
