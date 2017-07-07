package org.polyfillservice.rest.utils;

import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by smo on 4/15/17.
 */
public class TestingUtil {

    public static Path getResourcesPath() throws FileNotFoundException {
        return ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + ".").toPath();
    }

    public static Path getPolyfillsPath() throws FileNotFoundException {
        return getResourcesPath().resolve("polyfills");
    }

    public static String loadResource(String filename) throws IOException {
        return loadFile(getResourcesPath().resolve(filename));
    }

    public static String loadFile(Path filepath) throws IOException {
        return new String(Files.readAllBytes(filepath));
    }
}
