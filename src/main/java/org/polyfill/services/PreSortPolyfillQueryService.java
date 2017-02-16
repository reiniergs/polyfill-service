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

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("presort")
public class PreSortPolyfillQueryService implements PolyfillQueryService {

    @Resource(name = "polyfillsDirPath")
    private String polyfillsDirPath;
    @Resource(name = "baselineVersionsPath")
    private String baselineVersionsPath;

    private final String[] nonPolyfillFiles = {"aliases.json"};

    private Map<String, Polyfill> polyfills;
    private List<Polyfill> sortedPolyfills;
    private Map<String, Object> baselineVersions;

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
    }

    /**
     * TODO:
     * Filter polyfill list by user agent, then get the requested feature from
     * polyfill list along with its dependencies.
     * The result list is ordered by dependencies.
     * @param userAgent user agent object
     * @param name feature name; e.g. Array.of
     * @return the filtered list
     */
    public List<Polyfill> getPolyfillByFeature(UserAgent userAgent, String name) {
        List<Polyfill> filteredPolyfill = new ArrayList<>();
        return filteredPolyfill;
    }

    /**
     * TODO:
     * Filter polyfill list by alias and user agent and order it by dependencies
     * @param userAgent user agent object
     * @param alias polyfill set alias; e.g. es6, then we return all es6 polyfills
     * @return the filtered list
     */
    public List<Polyfill> getPolyfillsByAlias(UserAgent userAgent, String alias) {
        List<Polyfill> filteredPolyfill = new ArrayList<>();
        return filteredPolyfill;
    }

    /**
     * Filter polyfill list by user agent and order it by dependencies
     * @param userAgent user agent object
     * @return the filtered list
     */
    public List<Polyfill> getPolyfillsByUserAgent(UserAgent userAgent) {
        List<Polyfill> filteredPolyfills = new ArrayList<>();

        if (meetsBaseline(userAgent)) {
            String browser = userAgent.getFamily();
            String browserVersion = userAgent.getVersion();

            // for each poly, check if userAgent satisfies the required browser version
            for (Polyfill polyfill : this.sortedPolyfills) {
                String requiredVersion = polyfill.getBrowserRequirement(browser);
                if (requiredVersion != null && isVersionInRange(browserVersion, requiredVersion)) {
                    filteredPolyfills.add(polyfill);
                }
            }
        }
        return filteredPolyfills;
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
            if (dependencies == null) {
                dependencyGraph.addRelation(polyfillName, null);
            } else {
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

        List<Map<String, Object>> polyfillMaps = configLoaderService.getConfigsFromDirectory(
                        polyfillsDir, false, path -> isPolyfillFile(path));

        for (Map<String, Object> polyfillMap : polyfillMaps) {
            Polyfill polyfill = new Polyfill(polyfillMap);
            if (polyfill.getName() != null) {
                polyfills.put(polyfill.getName(), polyfill);
            }
        }

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
}
