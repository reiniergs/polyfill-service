package org.polyfill.interfaces;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by bvenkataraman on 10/19/16.
 */
public interface ConfigLoaderService {
    Map<String, Object> getConfig(String path) throws IOException;
}
