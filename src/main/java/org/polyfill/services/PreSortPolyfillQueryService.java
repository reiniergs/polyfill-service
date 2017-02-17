package org.polyfill.services;

import org.polyfill.components.Polyfill;
import org.polyfill.components.TSort;
import org.polyfill.interfaces.ConfigLoaderService;
import org.polyfill.interfaces.PolyfillQueryService;
import org.polyfill.interfaces.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service("presort")
public class PreSortPolyfillQueryService implements PolyfillQueryService {

    @Resource(name = "polyfillsDirPath")
    private String polyfillsDirPath;
    @Resource(name = "baselineVersionsPath")
    private String baselineVersionsPath;
    @Resource(name = "aliasesPath")
    private String aliasesPath;

    private final String[] nonPolyfillFiles = {"aliases.json"};

    private Map<String, Polyfill> polyfills;
    private List<Polyfill> sortedPolyfills;
    private Map<String, Object> baselineVersions;
    private Map<String, Object> aliases;

    @Autowired
    @Qualifier("json")
    private ConfigLoaderService configLoaderService;

    @Autowired
    private SemVerUtilService semVerUtilService;

    @PostConstruct
    public void loadConfigs() throws IOException {
        this.polyfills = getPolyfillsMap(polyfillsDirPath);
        this.sortedPolyfills = getDependencySortedPolyfills();
        this.baselineVersions = getConfig(baselineVersionsPath);
        this.aliases = getConfig(aliasesPath);
    }

    /**
     * Filter polyfill list by alias and user agent and order it by dependencies
     * @param userAgent user agent object
     * @param featureNames list of feature group alias names or feature names, delimited by ","
     *                     e.g. "es6,es5,Array.of"
     * @return the filtered list
     */
    public List<Polyfill> getPolyfillsByFeatures(UserAgent userAgent, String featureNames) {
        if (meetsBaseline(userAgent)) {
            Set<String> featureSet = new HashSet<>();

            List<String> featureNamesList = Arrays.asList(featureNames.split(","));
            for (String featureName : featureNamesList) {
                List<String> featureGroup = resolveAlias(featureName);
                if (featureGroup != null) {
                    featureSet.addAll(featureGroup);
                } else {
                    featureSet.add(featureName);
                }
            }

            return this.sortedPolyfills.stream()
                    .filter(polyfill -> featureSet.contains(polyfill.getName()))
                    .filter(polyfill -> isPolyfillNeeded(polyfill, userAgent))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Filter polyfill list by user agent and order it by dependencies
     * @param userAgent user agent object
     * @return the filtered list
     */
    public List<Polyfill> getPolyfillsByUserAgent(UserAgent userAgent) {
        if (meetsBaseline(userAgent)) {
            return this.sortedPolyfills.stream()
                    .filter(polyfill -> isPolyfillNeeded(polyfill, userAgent))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    /**************************** Helpers **************************/

    private List<Polyfill> getDependencySortedPolyfills() {
        return getPolyfillsFromNames(getDependencyGraph().sort());
    }

    private TSort getDependencyGraph() {
        TSort dependencyGraph = new TSort();
        for (Polyfill polyfill : this.polyfills.values()) {
            String polyfillName = polyfill.getName();
            List<String> dependencies = polyfill.getDependencies();
            dependencyGraph.addRelation(polyfillName, null);

            if (dependencies != null) {
                for (String dependency : dependencies) {
                    dependencyGraph.addRelation(dependency, polyfillName);
                }
            }
        }
        return dependencyGraph;
    }

    private List<Polyfill> getPolyfillsFromNames(List<String> polyfillNames) {
        List<Polyfill> polyfills = new ArrayList<>();
        for (String polyfillName : polyfillNames) {
            Polyfill polyfill = this.polyfills.get(polyfillName);
            if (polyfill != null) {
                polyfills.add(polyfill);
            }
        }
        return polyfills;
    }

    private boolean isPolyfillFile(Path filePath) {
        String fileName = filePath.getFileName().toString();
        for (String nonPolyfillFileName : nonPolyfillFiles) {
            if (fileName.equals(nonPolyfillFileName)) {
                return false;
            }
        }
        return true;
    }

    private Map<String, Polyfill> getPolyfillsMap(String polyfillsDir) throws IOException {

        Map<String, Polyfill> polyfills = new HashMap<>();

        File[] files = new File(polyfillsDir).listFiles();
        for (File dir : files) {
            if (dir.isDirectory()) {
                Polyfill polyfill = new Polyfill(dir, configLoaderService);
                polyfills.put(dir.getName(), polyfill);
            }
        }

//        List<Map<String, Object>> polyfillMaps = configLoaderService.getConfigsFromDirectory(
//                        polyfillsDir, false, path -> isPolyfillFile(path));
//
//        for (Map<String, Object> polyfillMap : polyfillMaps) {
//            Polyfill polyfill = new Polyfill(polyfillMap);
//            if (polyfill.getName() != null) {
//                polyfills.put(polyfill.getName(), polyfill);
//            }
//        }

        return polyfills;
    }

    /**
     * Check if userAgent meets the minimum browser versions we support
     */
    private boolean meetsBaseline(UserAgent userAgent) {
        Object baselineVersion = baselineVersions.get(userAgent.getFamily());
        if (baselineVersion instanceof String) {
            return isVersionInRange(userAgent.getVersion(), (String)baselineVersion);
        }
        return false;
    }

    /**
     * Mixin method for configLoaderService.getConfig
     */
    private Map<String, Object> getConfig(String path) throws IOException {
        return configLoaderService.getConfig(path);
    }

    /**
     * Mixin method for semVerUtilService.isVersionInRange
     */
    private boolean isVersionInRange(String version, String range) {
        return semVerUtilService.isVersionInRange(version, range);
    }

    private boolean isPolyfillNeeded(Polyfill polyfill, UserAgent ua) {
        String requiredVersion = polyfill.getBrowserRequirement(ua.getFamily());
        return requiredVersion != null && isVersionInRange(ua.getVersion(), requiredVersion);
    }

    private List<String> resolveAlias(String aliasName) {
        Object featureGroup = this.aliases.get(aliasName);
        return (featureGroup instanceof List) ? (List<String>)featureGroup : null;
    }
}
