package org.polyfill.api.services;

import org.polyfill.api.components.Polyfill;
import org.polyfill.api.components.ServiceConfig;
import org.polyfill.api.interfaces.PolyfillConfigLoaderService;
import org.polyfill.api.interfaces.PolyfillLoaderService;
import org.polyfill.api.interfaces.ResourceLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
@Service("all")
class FinancialTimesPolyfillLoaderService implements PolyfillLoaderService, ResourceLoaderService {

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
    private ServiceConfig serviceConfig;

    @Autowired
    private PolyfillConfigLoaderService configLoader;

    @Override
    public Map<String, Polyfill> loadPolyfills(String polyfillsPath) throws IOException {
        Map<String, Polyfill> polyfills = new HashMap<>();
        List<String> activePolyfills = serviceConfig.getActivePolyfills();

        if (activePolyfills.isEmpty()) {
            activePolyfills = getAllPolyfillNames(polyfillsPath);
        }

        activePolyfills.forEach(polyfillName -> {
            Path polyfillDir = Paths.get(polyfillsPath, polyfillName);
            try {
                Polyfill polyfill = loadPolyfill(polyfillDir);
                polyfills.put(polyfill.getName(), polyfill);
            } catch (IOException e) {
                System.err.println("Error loading polyfill from directory: " + polyfillDir.toString());
            }
        });

        return Collections.unmodifiableMap(polyfills);
    }

    /*
     * Implementation-specific helpers
     */
    private List<String> getAllPolyfillNames(String polyfillsPath) throws IOException {
        return getResources(polyfillsPath, "*", "meta.json").stream()
                .map(polyfillResource -> {
                    try {
                        return getBaseDirectoryName(polyfillResource);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(polyfillName -> polyfillName != null)
                .collect(Collectors.toList());
    }

    private Polyfill loadPolyfill(Path polyfillDirPath) throws IOException {
        String polyfillDir = polyfillDirPath.toString();
        Map<String, Object> meta = configLoader.getConfig(polyfillDir, META_FILENAME);
        String rawSource = resourceToString(getResource(polyfillDir, RAW_FILENAME));
        String minSource = resourceToString(getResource(polyfillDir, MIN_FILENAME));

        return new Polyfill.Builder(polyfillDirPath.getFileName().toString())
                .rawSource( rawSource )
                .minSource( minSource )
                .aliases( getList(meta, ALIASES_KEY) )
                .browserRequirements( getMap(meta, BROWSER_REQUIREMENTS_KEY) )
                .dependencies( getList(meta, DEPENDENCIES_KEY) )
                .license( getString(meta, LICENSE_KEY) )
                .repository( getString(meta, REPO_KEY) )
                .detectSource( getString(meta, DETECT_SOURCE_KEY) )
                .testsSource( getString(meta, TESTS_SOURCE_KEY) )
                .isTestable( getIsTestable(meta) )
                .build();
    }

    private boolean getIsTestable(Map<String, Object> meta) {
        Map<String, Object> testMap = getMap(meta, TEST_KEY);
        String testsSource = getString(meta, TESTS_SOURCE_KEY);
        return testsSource != null && !testsSource.isEmpty() && (Boolean)testMap.getOrDefault("ci", true);
    }

    /*
     * Generic helpers
     */

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

    private String getBaseDirectoryName(Resource resource) throws IOException {
        Path path = FileSystems.getDefault().getPath(resource.getURI().toString());
        return path.getParent().getFileName().toString();
    }
}
