package org.polyfill.api.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by smo on 6/5/17.
 * Wrapper object to store pointer to service configuration file.
 * This should allow us to support other object type in the future. e.g. InputStream.
 */
public class PolyfillServiceConfigLocation {

    private File file;

    public PolyfillServiceConfigLocation(File file) {
        this.file = file;
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }
}
