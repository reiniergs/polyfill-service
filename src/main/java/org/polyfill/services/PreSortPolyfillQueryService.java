package org.polyfill.services;

import org.polyfill.components.Feature;
import org.polyfill.components.Filters;
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
import java.util.function.Function;
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
    public Polyfill getPolyfill(String name) {
        return this.polyfills.get(name);
    }

    @Override
    public List<Polyfill> getPolyfills(List<String> polyfillNames, UserAgent userAgent) {
        if (userAgent != null && !isUserAgentSupported(userAgent)) {
            return Collections.emptyList();
        }

        Map<String, Feature> featureSet = new HashMap<>();
        for (String polyfillName : polyfillNames) {
            featureSet.put(polyfillName, new Feature(polyfillName));
        }

        if (polyfillNames.contains("all")) {
            resolveFeatures(featureSet, this::resolveAlias, true);
        }

        filterForTargetingUA(featureSet, userAgent, false);

        return featureSet.keySet().stream()
                .map(name -> this.polyfills.get(name))
                .collect(Collectors.toList());
    }

    @Override
    public List<Feature> getFeatures(List<Feature> featureList, Filters filters) {
        UserAgent userAgent = filters.getUserAgent();
        boolean loadOnUnknownUA = filters.getLoadOnUnknownUA();
        Set<String> excludes = filters.getExcludes();

        if (userAgent != null && !isUserAgentSupported(userAgent) && !loadOnUnknownUA) {
            return Collections.emptyList();
        }

        Map<String, Feature> featureSet = new HashMap<>();
        for (Feature feature : featureList) {
            featureSet.put(feature.getName(), feature);
        }

        resolveFeatures(featureSet, this::resolveAlias, true);
        filterForTargetingUA(featureSet, userAgent, loadOnUnknownUA);

        resolveFeatures(featureSet, this::resolveDependencies, false);
        filterForTargetingUA(featureSet, userAgent, loadOnUnknownUA);

        filterExcludes(featureSet, excludes);

        attachPolyfills(featureSet);

        return sort(featureSet);
    }

    /**
     * Attach polyfill source to each feature
     * @param featureSet set of features
     */
    private void attachPolyfills(Map<String, Feature> featureSet) {
        for (Feature feature : featureSet.values()) {
            feature.setPolyfill(this.polyfills.get(feature.getName()));
        }
    }

    /**
     * Sort features by dependencies
     * @param featureSet set of features needed to sort
     * @return sorted list of feature options
     */
    private List<Feature> sort(Map<String, Feature> featureSet) {
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

            polyfill.getDependencies().stream()
                    .filter(polyfillMap::containsKey)
                    .forEach(dependency -> {
                        dependencyGraph.addRelation(dependency, polyfillName);
                    });
        }
        return dependencyGraph;
    }

    /**
     * Check if {@code userAgent} meets the minimum browser versions we support
     * @param userAgent user agent to check
     * @return true if {@code userAgent} is supported
     */
    private boolean isUserAgentSupported(UserAgent userAgent) {
        if (userAgent == null) return false;

        Object baselineVersion = browserBaselines.get(userAgent.getFamily());
        String clientUAVersion = userAgent.getVersion();
        return baselineVersion instanceof String
                && versionChecker.isVersionInRange(clientUAVersion, (String)baselineVersion);
    }

    /**
     * Filter out all features that do not support given user agent unless
     * loadOnUnknownUA is true
     * NOP if user agent is null
     *
     * @param featureSet feature options for the polyfill
     * @param userAgent user agent to check against
     * @param loadOnUnknownUA whether to load polyfill if user agent is unknown
     */
    private void filterForTargetingUA(Map<String, Feature> featureSet,
                UserAgent userAgent, boolean loadOnUnknownUA) {

        if (userAgent == null) return;

        String clientBrowser = userAgent.getFamily();
        String clientUAVersion = userAgent.getVersion();
        boolean unknownUALoadPolyfill = !isUserAgentSupported(userAgent) && loadOnUnknownUA;

        featureSet.values().removeIf(feature -> {
            boolean isFeatureNeededForUA = false;
            Polyfill polyfill = this.polyfills.get(feature.getName());
            if (polyfill != null) {
                String requiredVersion = polyfill.getBrowserRequirement(clientBrowser);
                boolean versionIsInRange = requiredVersion != null
                        && versionChecker.isVersionInRange(clientUAVersion, requiredVersion);
                isFeatureNeededForUA = feature.isAlways() || versionIsInRange || unknownUALoadPolyfill;
            }
            return !isFeatureNeededForUA;
        });
    }

    /**
     * Remove feature from features based on feature names in excludes
     * @param featureSet map of features to go through excludes
     * @param excludes features to excludes
     */
    private void filterExcludes(Map<String, Feature> featureSet, Set<String> excludes) {
        featureSet.keySet().removeIf(excludes::contains);
    }

    /**
     * Expand features=all to all features
     * @param featureAll feature all to inherit the flags
     * @return list of all features
     */
    private List<Feature> getAllFeatures(Feature featureAll) {
        return this.polyfills.keySet().stream()
                .map(featureName -> new Feature(featureName, featureAll))
                .collect(Collectors.toList());
    }

    /**
     * Expand the alias feature
     * @param feature
     * @return return a list of features represented by the alias feature;
     *         return null if alias feature doesn't exist
     */
    private List<Feature> resolveAlias(Feature feature) {
        if ("all".equals(feature.getName())) {
            return getAllFeatures(feature);
        } else {
            Object featureGroup = this.aliases.get(feature.getName());
            if (featureGroup instanceof List) {
                return ((List<String>) featureGroup).stream()
                        .map(featureName -> new Feature(featureName, feature))
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
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
            return polyfill.getDependencies().stream()
                    .map(featureName -> new Feature(featureName, feature))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * Expand/Resolve features through a resolve function
     * @param featureSet feature set to process with resolve function
     * @param resolveFn resolve function (e.g. expand alias, get dependencies)
     */
    private void resolveFeatures(Map<String, Feature> featureSet,
                Function<Feature, List<Feature>> resolveFn, boolean removeResolvedFeature) {

        Queue<String> featuresQueue = new LinkedList<>(featureSet.keySet());
        while (!featuresQueue.isEmpty()) {
            String name = featuresQueue.remove();

            List<Feature> resolvedFeatures = resolveFn.apply(featureSet.get(name));
            for (Feature newFeature : resolvedFeatures) {
                String newName = newFeature.getName();

                if (featureSet.containsKey(newName)) {
                    // if exists, copy new properties
                    Feature existingFeature = featureSet.get(newName);
                    existingFeature.copyFlags(newFeature);
                    existingFeature.copyRequiredBys(newFeature);

                } else {
                    // new feature, add it and queue it up to process later
                    featureSet.put(newName, newFeature);
                    featuresQueue.add(newName);
                }
            }

            // feature is resolved
            if (!resolvedFeatures.isEmpty() && removeResolvedFeature) {
                featureSet.remove(name);
            }
        }
    }
}
