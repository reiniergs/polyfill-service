package org.polyfill.services;

import org.polyfill.components.Polyfill;
import org.polyfill.interfaces.ConfigLoaderService;
import org.polyfill.interfaces.PolyfillLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by smo on 2/25/17.
 * Service to load and construct polyfill from file system.
 * This service specifically handles the case where polyfill is stored in the following structure:
 * polyfillName
 * - meta.json
 * - min.js
 * - raw.js
 */
@Service("directory based")
@Primary
public class DirectoryBasedPolyfillLoaderService implements PolyfillLoaderService {

    private static final String META_FILENAME = "meta.json";
    private static final String MIN_FILENAME = "min.js";
    private static final String RAW_FILENAME = "raw.js";

    @Autowired
    ConfigLoaderService configLoaderService;

    @Override
    public Polyfill loadPolyfill(String polyfillPath) throws IOException {
        File metaFile = new File(polyfillPath, META_FILENAME);
        File rawFile = new File(polyfillPath, RAW_FILENAME);
        File minFile = new File(polyfillPath, MIN_FILENAME);

        Map<String, Object> meta = configLoaderService.getConfig(metaFile.getPath());
        meta.put(Polyfill.RAW_SOURCE_KEY, getFileSource(rawFile));
        meta.put(Polyfill.MIN_SOURCE_KEY, getFileSource(minFile));

        String polyfillName = new File(polyfillPath).getName();
        return new Polyfill(polyfillName, meta);
    }

    @Override
    public Map<String, Polyfill> loadPolyfillsToMap(String polyfillsPath) throws IOException {
        Map<String, Polyfill> polyfills = new HashMap<>();

        Files.newDirectoryStream(Paths.get(polyfillsPath), path -> path.toFile().isDirectory())
            .forEach(dir -> {
                try {
                    Polyfill polyfill = loadPolyfill(dir.toString());
                    polyfills.put(polyfill.getName(), polyfill);
                } catch (IOException e) {
                    System.err.println("Error loading polyfill from directory: " + dir);
                }
            });
        return Collections.unmodifiableMap(polyfills);

    }

    /**
     * Load file into string
     * @param file
     * @return {@param file}'s content as String
     */
    private String getFileSource(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            return "";
        }
    }
}
