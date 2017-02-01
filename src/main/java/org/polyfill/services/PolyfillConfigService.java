package org.polyfill.services;

import org.polyfill.interfaces.ConfigLoaderService;
import org.polyfill.interfaces.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PolyfillConfigService {
    private final String POLYFILLS_DIR = "./polyfills/__dist/";
    private final String BASELINE_VERSIONS_FILE = "./configs/baselineVersions.json";
    private List<Map<String, Object>> polyfillConfigs;
    private Map<String, Object> baselineVersions;

    @Autowired
    @Qualifier("json")
    private ConfigLoaderService configLoaderService;

    @Autowired
    private MapUtilService mapUtilService;

    @Autowired
    private SemVerUtilService semVerUtilService;

    @PostConstruct
    public void loadConfigs() {
        List<Path> polyfillPaths = getAllConfigPaths(Paths.get(POLYFILLS_DIR));
        this.polyfillConfigs = loadPolyfills(polyfillPaths);
        this.baselineVersions = loadConfig(BASELINE_VERSIONS_FILE);
    }

// TODO:
//    public Map<String, Object> getPolyfillByFeatureName(String feature) {
//        return (Map<String, Object>)this.polyfillConfigs.get(feature);
//    }
//
//    public List<Map<String, Object>> getPolyfillsByAlias(String alias) {
//        List<Map<String, Object>> filteredPolyfillConfigs = new ArrayList<>();
//        Object featuresObj = this.polyfillAliases.get(alias);
//        if (featuresObj instanceof List) {
//            List<String> features = (List<String>)featuresObj;
//            for (String featureKey : features) {
//                Object featureConfigsObj = this.polyfillConfigs.get(featureKey);
//                if (featureConfigsObj instanceof Map) {
//                    filteredPolyfillConfigs.add((Map<String, Object>)featureConfigsObj);
//                }
//            }
//        }
//        return filteredPolyfillConfigs;
//    }

    public List<Map<String, Object>> getPolyfillsByUserAgent(UserAgent userAgent) {
        List<Map<String, Object>> filteredPolyfillConfigs = new ArrayList<>();

        if (meetsBaseline(userAgent)) {
            String browser = userAgent.getFamily();
            String browserVersion = userAgent.getVersion();
            // for each poly, check if userAgent satisfies the required browser version
            for (Map<String, Object> polyfill : this.polyfillConfigs) {
                String keyPath = String.join(".", "browsers", browser);
                Object requiredVersionObj = mapUtilService.getFromMap(polyfill, keyPath);
                if (requiredVersionObj instanceof String) {
                    String requiredVersion = (String)requiredVersionObj;
                    if (isVersionInRange(browserVersion, requiredVersion)) {
                        filteredPolyfillConfigs.add(polyfill);
                    }
                }
            }
        }
        return filteredPolyfillConfigs;
    }

    private List<Path> getAllConfigPaths(Path dir) {
        List<Path> fileNames = new ArrayList<>();
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if(path.toFile().isDirectory()) {
                    fileNames.addAll(getAllConfigPaths(path));
                } else if (isPolyfillFile(path)){
                    fileNames.add(path);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    private List<Map<String, Object>> loadPolyfills(List<Path> polyfillPaths) {
        List<Map<String, Object>> polyfills = new ArrayList<>();
        for (Path polyfillPath : polyfillPaths) {
            Map<String, Object> featureConfigs = loadConfig(polyfillPath);
            if (featureConfigs != null) {
                polyfills.add(featureConfigs);
            }
        }
        return polyfills;
    }

    private Map<String, Object> loadConfig(Path path) {
        return this.loadConfig(path.toString());
    }

    private Map<String, Object> loadConfig(String path) {
        try {
            return this.configLoaderService.getConfig(path);
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getPolyfillKey(String featurePath) {
        String key = Paths.get(featurePath).getFileName().toString();
        return key.substring(0, key.lastIndexOf("."));
    }

    private boolean isPolyfillFile(Path fileName) {
        return fileName.toString().toLowerCase().endsWith(".json")
                && !fileName.equals("aliases.json");
    }

    private boolean meetsBaseline(UserAgent userAgent) {
        String browserFamily = userAgent.getFamily();
        String browserVersion = userAgent.getVersion();
        Object baselineVersionObj = baselineVersions.get(browserFamily);
        if (baselineVersionObj instanceof String) {
            String baselineVersion = (String) baselineVersionObj;
            return isVersionInRange(browserVersion, baselineVersion);
        }
        return false;
    }

    private boolean isVersionInRange(String version, String range) {
        return semVerUtilService.isVersionInRange(version, range);
    }
}
