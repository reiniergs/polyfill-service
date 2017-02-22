package org.polyfill.services;

import org.polyfill.components.Polyfill;
import org.polyfill.components.TSort;
import org.polyfill.interfaces.ConfigLoaderService;
import org.polyfill.interfaces.PolyfillQueryService;
import org.polyfill.interfaces.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service("presort")
public class PreSortPolyfillQueryService implements PolyfillQueryService {

    @Resource(name = "polyfillsDirPath")
    private String polyfillsDirPath;
    @Resource(name = "browserBaselinesPath")
    private String browserBaselinesPath;
    @Resource(name = "aliasesPath")
    private String aliasesPath;

    private Map<String, Polyfill> polyfills;
    private List<Polyfill> sortedPolyfills;
    private Map<String, Object> browserBaselines;
    private Map<String, Object> aliases;

    @Autowired
    @Qualifier("json")
    private ConfigLoaderService configLoaderService;

    @Autowired
    private SemVerUtilService semVerUtilService;

    @PostConstruct
    public void loadConfigs() throws IOException {
        this.polyfills = getPolyfillsMap(polyfillsDirPath);
        this.sortedPolyfills = getDependencySortedPolyfills(this.polyfills);
        this.browserBaselines = getConfig(browserBaselinesPath);
        this.aliases = getConfig(aliasesPath);
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

    /**
     * Filter polyfill list by alias and user agent and order it by dependencies
     * @param userAgent user agent object
     * @param featureNames list of feature group alias names or feature names, delimited by ","
     *                     e.g. "es6,es5,Array.of"
     * @return the filtered list
     */
    public List<Polyfill> getPolyfillsByFeatures(UserAgent userAgent, String[] featureNames) {
        if (meetsBaseline(userAgent)) {
            Set<String> featureSet = new HashSet<>();

            List<String> featureNamesList = Arrays.asList(featureNames);
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

    /**************************** Helpers **************************/

    private List<Polyfill> getDependencySortedPolyfills(Map<String, Polyfill> polyfillMap) {
        List<String> polyfillNames = getDependencyGraph(polyfillMap).sort();
        return polyfillNames.stream().map(polyfillMap::get).collect(Collectors.toList());
    }

    private TSort getDependencyGraph(Map<String, Polyfill> polyfillMap) {
        TSort dependencyGraph = new TSort();
        for (Polyfill polyfill : polyfillMap.values()) {
            String polyfillName = polyfill.getName();
            List<String> dependencies = polyfill.getDependencies();
            dependencyGraph.addRelation(polyfillName, null);

            if (dependencies != null) {
                dependencies.stream().filter(polyfillMap::containsKey)
                        .forEach(dependency -> dependencyGraph.addRelation(dependency, polyfillName));
            }
        }
        return dependencyGraph;
    }

    private Map<String, Polyfill> getPolyfillsMap(String polyfillsDir) throws IOException {
        Map<String, Polyfill> polyfills = new HashMap<>();
        File[] files = new File(polyfillsDir).listFiles();
        if (files != null) {
            for (File dir : files) {
                if (dir.isDirectory()) {
                    Polyfill polyfill = new Polyfill(dir, configLoaderService);
                    polyfills.put(polyfill.getName(), polyfill);
                }
            }
        }

        return polyfills;
    }

    /**
     * Check if userAgent meets the minimum browser versions we support
     */
    private boolean meetsBaseline(UserAgent userAgent) {
        Object baselineVersion = browserBaselines.get(userAgent.getFamily());
        return baselineVersion instanceof String && isVersionInRange(userAgent.getVersion(), (String)baselineVersion);
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
