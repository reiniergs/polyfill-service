package org.polyfill.services;

import org.polyfill.components.Feature;
import org.polyfill.components.Polyfill;
import org.polyfill.components.TSort;
import org.polyfill.interfaces.PolyfillQueryService;
import org.polyfill.interfaces.UserAgent;
import org.polyfill.interfaces.VersionUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by smo
 * Service to handle polyfill queries. This service presorts all the polyfills based on
 * dependencies to avoid doing topological sorting on every query.
 */
@Service("presort")
@Primary
public class PreSortPolyfillQueryService implements PolyfillQueryService {

    @Resource(name = "polyfills")
    private Map<String, Polyfill> polyfills;

    @Resource(name = "browserBaselines")
    private Map<String, Object> browserBaselines;

    @Resource(name = "aliases")
    private Map<String, Object> aliases;

    @Autowired
    private VersionUtilService versionChecker;

    private List<String> sortedPolyfills;

    @PostConstruct
    public void init() {
        this.sortedPolyfills = getDependencySortedPolyfills(this.polyfills);
    }

    @Override
    public Map<String, Polyfill> getAllPolyfills() {
        return this.polyfills;
    }

    @Override
    public List<Feature> getFeatures(UserAgent userAgent,
            List<Feature> featureList, List<String> excludeList, boolean loadOnUnknownUA) {

        if (!isUserAgentSupported(userAgent) && !loadOnUnknownUA) {
            return new ArrayList<>();
        }

        List<Feature> requestedFeatures = new ArrayList<>(featureList);
        Map<String, Feature> featureSet = new HashMap<>();
        Set<String> excludeSet = new HashSet<>(excludeList);
        for (int i = 0; i < requestedFeatures.size(); i++) {
            Feature feature = requestedFeatures.get(i);

            List<Feature> featureGroup = resolveAlias(feature);
            if (featureGroup != null) {
                // feature is an alias group, append to list to handle later
                requestedFeatures.addAll(featureGroup);

            } else if(shouldIncludeFeature(feature, userAgent, excludeSet, loadOnUnknownUA)) {
                String featureName = feature.getName();
                if (featureSet.containsKey(featureName)) {
                    // feature set already has this feature, just add new flags
                    featureSet.get(featureName).copyFlags(feature);
                } else {
                    // add it to feature set
                    featureSet.put(featureName, feature);
                    feature.setPolyfill(this.polyfills.get(featureName));

                    // if feature has dependencies, append to list to handle later
                    List<Feature> dependencies = resolveDependencies(feature);
                    if (dependencies != null) {
                        requestedFeatures.addAll(dependencies);
                    }
                }
            }
        }

        return getSortedFeatureOptions(featureSet);
    }

    @Override
    public Polyfill getPolyfillByName(String name) {
        return this.polyfills.get(name);
    }

    /**
     * Sort feature options
     * @param featureSet set of features needed to sort
     * @return sorted list of feature options
     */
    private List<Feature> getSortedFeatureOptions(Map<String, Feature> featureSet) {
        return this.sortedPolyfills.stream()
                .filter(featureSet::containsKey)
                .map(featureSet::get)
                .collect(Collectors.toList());
    }

    /**
     * Return a list of polyfill names sorted by dependencies
     * @param polyfillMap map of polyfills with key=polyfill name, value=polyfill
     * @return a list of polyfill names sorted by dependencies
     */
    private List<String> getDependencySortedPolyfills(Map<String, Polyfill> polyfillMap) {
        return buildDependencyGraph(polyfillMap).sort();
    }

    /**
     * Return a topological graph of polyfills
     * @param polyfillMap map of polyfills with key=polyfill name, value=polyfill
     * @return a topological graph of polyfills
     */
    private TSort buildDependencyGraph(Map<String, Polyfill> polyfillMap) {
        TSort dependencyGraph = new TSort();
        for (Polyfill polyfill : polyfillMap.values()) {
            String polyfillName = polyfill.getName();
            dependencyGraph.addRelation(polyfillName, null);

            List<String> dependencies = polyfill.getDependencies();
            if (dependencies != null) {
                dependencies.stream()
                        .filter(polyfillMap::containsKey)
                        .forEach(dependency -> dependencyGraph.addRelation(dependency, polyfillName));
            }
        }
        return dependencyGraph;
    }

    /**
     * Check if {@code userAgent} meets the minimum browser versions we support
     * @param userAgent user agent to check
     * @return true if {@code userAgent} is supported
     */
    private boolean isUserAgentSupported(UserAgent userAgent) {
        Object baselineVersion = browserBaselines.get(userAgent.getFamily());
        String clientUAVersion = userAgent.getVersion();
        return baselineVersion instanceof String
                && versionChecker.isVersionInRange(clientUAVersion, (String)baselineVersion);
    }

    /**
     * Check if a polyfill should be shown for {@code userAgent}
     * @param feature feature options for the polyfill
     * @param userAgent user agent to check against
     * @param loadOnUnknownUA whether to load polyfill if user agent is unknown
     * @return true if we should load feature for the user agent
     */
    private boolean isFeatureNeededForUA(Feature feature, UserAgent userAgent, boolean loadOnUnknownUA) {
        Polyfill polyfill = this.polyfills.get(feature.getName());
        if (polyfill != null) {
            String requiredVersion = polyfill.getBrowserRequirement(userAgent.getFamily());
            String clientUAVersion = userAgent.getVersion();
            return feature.isAlways()
                    || (requiredVersion != null && versionChecker.isVersionInRange(clientUAVersion, requiredVersion))
                    || !isUserAgentSupported(userAgent) && loadOnUnknownUA;
        }
        return false;
    }

    /**
     * Determine whether we should include feature
     * @param feature feature for the feature to test
     * @param userAgent user agent object
     * @param excludeSet features to exclude
     * @return true if should include feature, else false
     */
    private boolean shouldIncludeFeature(Feature feature,
            UserAgent userAgent, Set<String> excludeSet, boolean loadOnUnknownUA) {
        return this.polyfills.containsKey(feature.getName()) // if we have this polyfill
                && !excludeSet.contains(feature.getName())   // if should not exclude
                && isFeatureNeededForUA(feature, userAgent, loadOnUnknownUA); // if needed for user agent
    }

    /**
     * Expand the alias feature
     * @param feature
     * @return return a list of features represented by the alias feature;
     *         return null if alias feature doesn't exist
     */
    private List<Feature> resolveAlias(Feature feature) {
        Object featureGroup = this.aliases.get(feature.getName());
        if (featureGroup instanceof List) {
            return ((List<String>) featureGroup).stream()
                    .map(featureName -> new Feature(featureName, feature))
                    .collect(Collectors.toList());
        }
        return null;
    }

    /**
     * Return a list of features depended by {@code feature}
     * @param feature
     * @return return a list of features depended by {@code feature}
     *         return null if there's no dependency
     */
    private List<Feature> resolveDependencies(Feature feature) {
        Polyfill polyfill = this.polyfills.get(feature.getName());
        if (polyfill != null) {
            List<String> featureGroup = polyfill.getDependencies();
            if (featureGroup != null) {
                return featureGroup.stream()
                        .map(featureName -> new Feature(featureName, feature))
                        .collect(Collectors.toList());
            }
        }
        return null;
    }
}
