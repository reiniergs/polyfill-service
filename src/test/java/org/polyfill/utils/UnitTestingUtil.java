package org.polyfill.utils;

import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.nio.file.Path;

/**
 * Created by smo on 4/15/17.
 */
public class UnitTestingUtil {

    public static Path getResourcesPath() throws FileNotFoundException {
        return ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + ".").toPath();
    }

    public static Path getPolyfillsPath() throws FileNotFoundException {
        return getResourcesPath().resolve("polyfills");
    }
}
