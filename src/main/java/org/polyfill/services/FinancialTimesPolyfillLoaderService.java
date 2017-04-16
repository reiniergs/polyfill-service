package org.polyfill.services;

import org.polyfill.components.Polyfill;
import org.polyfill.interfaces.ConfigLoaderService;
import org.polyfill.interfaces.PolyfillLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by smo on 2/25/17.
 * Service to load and construct Financial Times' polyfills from file system.
 * Financial Times' polyfill folder structure:
 * polyfillName
 * - meta.json
 * - min.js
 * - raw.js
 */
@Primary
@Service("directory based")
@SuppressWarnings("unchecked")
public class FinancialTimesPolyfillLoaderService implements PolyfillLoaderService {

    private static final String ALIASES_KEY = "aliases";
    private static final String BROWSER_REQUIREMENTS_KEY = "browsers";
    private static final String DETECT_SOURCE_KEY = "detectSource";
    private static final String DEPENDENCIES_KEY = "dependencies";
    private static final String LICENSE_KEY = "license";
    private static final String REPO_KEY = "repo";
    private static final String TEST_KEY = "test";
    private static final String TESTS_SOURCE_KEY = "testsSource";

    private static final String META_FILENAME = "meta.json";
    private static final String MIN_FILENAME = "min.js";
    private static final String RAW_FILENAME = "raw.js";

    @Autowired
    private ConfigLoaderService configLoader;

    @Override
    public Polyfill loadPolyfill(String polyfillPathString) throws IOException {

        Path polyfillPath = Paths.get(polyfillPathString);
        Path metaPath = polyfillPath.resolve(META_FILENAME);
        Map<String, Object> meta = configLoader.getConfig(metaPath.toString());

        return new Polyfill.Builder(polyfillPath.getFileName().toString())
                .aliases(getList(meta, ALIASES_KEY))
                .browserRequirements(getMap(meta, BROWSER_REQUIREMENTS_KEY))
                .dependencies(getList(meta, DEPENDENCIES_KEY))
                .license(getString(meta, LICENSE_KEY))
                .repository(getString(meta, REPO_KEY))
                .detectSource(getString(meta, DETECT_SOURCE_KEY))
                .rawSource(getFileSource(polyfillPath.resolve(RAW_FILENAME)))
                .minSource(getFileSource(polyfillPath.resolve(MIN_FILENAME)))
                .testsSource(getString(meta, TESTS_SOURCE_KEY))
                .isTestable(getIsTestable(meta))
                .build();
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

    /*
     * Implementation-specific helpers
     */

    private boolean getIsTestable(Map<String, Object> meta) {
        Map<String, Object> testMap = getMap(meta, TEST_KEY);
        String testsSource = getString(meta, TESTS_SOURCE_KEY);
        return testsSource != null && !testsSource.isEmpty() && (Boolean)testMap.getOrDefault("ci", true);
    }

    /*
     * Generic helpers
     */

    private String getFileSource(Path filePath) {
        try {
            return new String(Files.readAllBytes(filePath));
        } catch (IOException e) {
            return null;
        }
    }

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return (value instanceof String) ? (String)value : null;
    }

    private List<String> getList(Map<String, Object> map, String key) {
        return (List<String>)map.getOrDefault(key, Collections.emptyList());
    }

    private Map getMap(Map<String, Object> map, String key) {
        return (Map)map.getOrDefault(key, Collections.emptyMap());
    }
}
