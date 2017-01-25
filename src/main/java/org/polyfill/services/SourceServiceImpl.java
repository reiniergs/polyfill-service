package org.polyfill.services;

import org.polyfill.interfaces.SourceService;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class SourceServiceImpl implements SourceService {
    private Path polyfillsPath = Paths.get(System.getProperty("user.dir")).resolve("polyfills/__dist");
    private Map<String, Object> configuredAliases;
    private List<File> features;
    private Map<String, Object> metadata = new HashMap<>();

    public SourceServiceImpl() throws Exception {
        try {
            configuredAliases = getConfiguredAliases();
            features = getFeatures();

            features.forEach(file -> {
                try {
                    metadata.put(file.getName(), getMetadata(file.getAbsolutePath()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean polyfillExists(String featureName) {
        return features.stream()
                .filter(file -> file.getName().equals(featureName))
                .findFirst() != null;
    }

    public Map<String, Object> getPolyfillMetaSync(String featureName) {
        return (Map<String, Object>) metadata.get(featureName);
    }

    public List<String> listPolyfills() {
        return features.stream().map(File::getName).collect(Collectors.toList());
    }

    public Map<String, Object> getConfigAliases(String featureName) {
        return (Map<String, Object>) configuredAliases.get(featureName);
    }

    public String streamPolyfillSource(String featureName) {
        return null;
    }

    private Object getMetadata(String absolutePath) throws Exception {
        return JSONConfigLoaderService.getConfig(absolutePath);
    }

    private Map<String, Object> getConfiguredAliases() throws Exception {
        return JSONConfigLoaderService.getConfig(polyfillsPath.resolve("aliases.json").toString());
    }

    private List<File> getFeatures() {
        File[] features = new File(polyfillsPath.toString()).listFiles(JSONFilter());
        return Arrays.asList(features);
    }

    private FilenameFilter JSONFilter() {
        return new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".json");
            }
        };
    }
}
