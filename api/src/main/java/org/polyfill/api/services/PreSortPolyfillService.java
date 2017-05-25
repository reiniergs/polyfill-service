package org.polyfill.api.services;

import org.polyfill.api.components.Feature;
import org.polyfill.api.components.Query;
import org.polyfill.api.components.Polyfill;
import org.polyfill.api.components.TSort;
import org.polyfill.api.interfaces.*;
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
class PreSortPolyfillService implements PolyfillService {

    @Resource(name = "polyfills")
    private Map<String, Polyfill> polyfills;

    @Resource(name = "browserBaselines")
    private Map<String, Object> browserBaselines;

    @Resource(name = "aliases")
    private Map<String, Object> aliases;

    @Autowired
    private VersionUtilService versionChecker;

    @Autowired
    private UserAgentParserService userAgentParserService;

    @Autowired
    private PolyfillsOutputService polyfillsOutputService;

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
    public List<Polyfill> getPolyfills(Query query, String uaString) {
        UserAgent userAgent = uaString == null ? null : userAgentParserService.parse(uaString);
        return getFeatures(userAgent, query).stream()
                .map(feature -> feature.getPolyfill())
                .collect(Collectors.toList());
    }

    @Override
    public String getPolyfillsSource(Query query, String userAgentString) {
        return getPolyfillsSource(query, userAgentString, false);
    }

    @Override
    public String getPolyfillsSource(Query query, String uaString, boolean isDebugMode) {
        UserAgent userAgent = uaString == null ? null : userAgentParserService.parse(uaString);
        List<Feature> featuresLoaded = getFeatures(userAgent, query);
        return polyfillsOutputService.getPolyfillsSource(userAgent.toString(), query, featuresLoaded, isDebugMode);
    }

    private List<Feature> getFeatures(UserAgent userAgent, Query query) {
        if ((userAgent != null && !isUserAgentSupported(userAgent) && !query.shouldLoadOnUnknownUA())
                || query.getFeatures().isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Feature> featureSet = new HashMap<>();
        for (Feature feature : query.getFeatures()) {
            if (query.isAlwaysForAll()) feature.setAlways(true);
            if (query.isGatedForAll()) feature.setGated(true);
            featureSet.put(feature.getName(), feature);
        }

        resolveFeatures(featureSet, this::resolveAlias, true);
        filterForTargetingUA(featureSet, userAgent, query.shouldLoadOnUnknownUA());

        if (query.shouldIncludeDependencies()) {
            resolveFeatures(featureSet, this::resolveDependencies, false);
            filterForTargetingUA(featureSet, userAgent, query.shouldLoadOnUnknownUA());
        }

        filterExcludes(featureSet, query.getExcludes());

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
     * @param doLoadOnUnknownUA whether to load polyfill if user agent is unknown
     */
    private void filterForTargetingUA(Map<String, Feature> featureSet,
                UserAgent userAgent, boolean doLoadOnUnknownUA) {

        if (userAgent == null) return;

        String clientBrowser = userAgent.getFamily();
        String clientUAVersion = userAgent.getVersion();
        boolean unknownUALoadPolyfill = !isUserAgentSupported(userAgent) && doLoadOnUnknownUA;

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
