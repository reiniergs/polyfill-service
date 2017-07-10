package org.polyfillservice.api.interfaces;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by smo on 6/13/17.
 * Wrapper object to store pointer to service configuration file.
 * Users can implement their own and supply us inputStream to use.
 */
public interface PolyfillServiceConfigLocation {
    InputStream getInputStream() throws IOException;
}
