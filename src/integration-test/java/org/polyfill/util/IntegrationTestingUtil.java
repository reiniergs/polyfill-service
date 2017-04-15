package org.polyfill.util;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by smo on 4/12/17.
 */
public class IntegrationTestingUtil {

    public String loadResource(String filename) throws Exception {
        File file = ResourceUtils.getFile("classpath:" + filename);
        return loadFile(file);
    }

    public String loadFile(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }
}
