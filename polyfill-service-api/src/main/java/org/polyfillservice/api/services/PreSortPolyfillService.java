package org.polyfillservice.api.services;

import org.polyfillservice.api.components.Feature;
import org.polyfillservice.api.components.Polyfill;
import org.polyfillservice.api.components.Query;
import org.polyfillservice.api.components.ServiceConfig;
import org.polyfillservice.api.components.TSort;
import org.polyfillservice.api.interfaces.PolyfillService;
import org.polyfillservice.api.interfaces.UserAgent;
import org.polyfillservice.api.interfaces.UserAgentParserService;
import org.polyfillservice.api.interfaces.VersionUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by smo
 * Service to handle polyfill queries. This service presorts all the polyfills based on
 * dependencies to avoid doing topological sorting on every query.
 */
@Service("presort")
class PreSortPolyfillService implements PolyfillService {

    @Resource(name = "polyfills")
    private Map<String, Polyfill> polyfills;
    @Resource(name = "browserBaselines")
    private Map<String, String> browserBaselines;
    @Resource(name = "aliases")
    private Map<String, List<String>> aliases;

    @Autowired
    private ServiceConfig serviceConfig;
    @Autowired
    private VersionUtilService versionChecker;
    @Autowired
    private UserAgentParserService userAgentParserService;
    @Autowired
    private PolyfillsOutputService polyfillsOutputService;

    private List<String> sortedPolyfills;
    private Query defaultQuery;

    @PostConstruct
    public void init() {
        List<Feature> polyfillRequestList = serviceConfig.getPolyfills().stream()
            .map(Feature::new)
            .collect(Collectors.toList());

        this.defaultQuery = new Query.Builder()
            .includeFeatures(polyfillRequestList)
            .setMinify(serviceConfig.shouldMinify())
            .setGatedForAll(serviceConfig.shouldGate())
            .setAlwaysLoadForAll(false)
            .setLoadOnUnknownUA(serviceConfig.shouldLoadOnUnknownUA())
            .setIncludeDependencies(true)
            .setDebugMode(serviceConfig.isDebugMode())
            .build();

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
    public List<Polyfill> getPolyfills(String uaString) {
        return getPolyfills(uaString, this.defaultQuery);
    }

    @Override
    public List<Polyfill> getPolyfills(String uaString, Query userQuery) {
        UserAgent userAgent = uaString == null ? null : this.userAgentParserService.parse(uaString);
        Query query = mergeDefaultOptions(userQuery);
        return getFeatures(userAgent, query).stream()
                .map(Feature::getPolyfill)
                .collect(Collectors.toList());
    }

    @Override
    public String getPolyfillsSource(String uaString) {
        return getPolyfillsSource(uaString, this.defaultQuery);
    }

    @Override
    public String getPolyfillsSource(String uaString, Query userQuery) {
        UserAgent userAgent = uaString == null ? null : this.userAgentParserService.parse(uaString);
        Query query = mergeDefaultOptions(userQuery);
        List<Feature> featuresLoaded = getFeatures(userAgent, query);
        return polyfillsOutputService.getPolyfillsSource(userAgent.toString(), query, featuresLoaded);
    }

    private Query mergeDefaultOptions(Query query) {
        if (query == this.defaultQuery) {
            return query;
        }

        return new Query.Builder()
            .includeFeatures(query.getFeatures().isEmpty() ? this.defaultQuery.getFeatures() : query.getFeatures())
            .excludeFeatures(query.getExcludes().isEmpty() ? this.defaultQuery.getExcludes() : query.getExcludes())
            .setMinify(query.shouldMinify() == null ? this.defaultQuery.shouldMinify() : query.shouldMinify())
            .setGatedForAll(query.shouldGateForAll() == null ? this.defaultQuery.shouldGateForAll() : query.shouldGateForAll())
            .setAlwaysLoadForAll(query.shouldAlwaysLoadForAll() == null ? this.defaultQuery.shouldAlwaysLoadForAll() : query.shouldAlwaysLoadForAll())
            .setLoadOnUnknownUA(query.shouldLoadOnUnknownUA() == null ? this.defaultQuery.shouldLoadOnUnknownUA() : query.shouldLoadOnUnknownUA())
            .setIncludeDependencies(query.shouldIncludeDependencies() == null ? this.defaultQuery.shouldIncludeDependencies() : query.shouldIncludeDependencies())
            .setDebugMode(query.isDebugMode() == null ? this.defaultQuery.isDebugMode() : query.isDebugMode())
            .build();
    }

    /**
     * Main method for doing the query
     * Return a list of requested polyfills
     * @param userAgent user agent object used to filter polyfills
     * @param query config object with information about what polyfills to get
     * @return list of polyfills with options
     */
    private List<Feature> getFeatures(UserAgent userAgent, Query query) {
        if ((userAgent != null && !isUserAgentSupported(userAgent) && !query.shouldLoadOnUnknownUA())
                || query.getFeatures().isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Feature> featureSet = new HashMap<>();
        for (Feature feature : query.getFeatures()) {
            if (query.shouldAlwaysLoadForAll()) feature.setAlways(true);
            if (query.shouldGateForAll()) feature.setGated(true);
            featureSet.put(feature.getName(), feature);
        }

        resolveAlias(featureSet);
        filterForTargetingUA(featureSet, userAgent, query.shouldLoadOnUnknownUA());

        if (query.shouldIncludeDependencies()) {
            resolveDependencies(featureSet);
            filterForTargetingUA(featureSet, userAgent, query.shouldLoadOnUnknownUA());
        }

        filterExcludes(featureSet, query.getExcludes());

        attachPolyfills(featureSet);

        return sortByDependency(featureSet);
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
    private List<Feature> sortByDependency(Map<String, Feature> featureSet) {
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
        return dependencyGraph.sort();
    }

    /**
     * Check if {@code userAgent} meets the minimum browser versions we support
     * @param userAgent user agent to check
     * @return true if {@code userAgent} is supported
     */
    private boolean isUserAgentSupported(UserAgent userAgent) {
        if (userAgent == null) return false;

        String baselineVersion = browserBaselines.get(userAgent.getFamily());
        return baselineVersion != null
            && versionChecker.isVersionInRange(userAgent.getVersion(), baselineVersion);
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
     * Expand an alias polyfill
     * @param feature
     * @return return a list of polyfills represented by the alias polyfill;
     *         return empty list if alias doesn't exist
     */
    private List<Feature> getFromAlias(Feature feature) {
        if ("all".equals(feature.getName())) {
            return getAllFeatures(feature);
        } else {
            List<String> featureGroup = this.aliases.get(feature.getName());
            if (featureGroup != null) {
                return (featureGroup).stream()
                        .map(featureName -> new Feature(featureName, feature))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    /**
     * Get the dependencies of a polyfill
     * @param feature
     * @return return a polyfill's dependencies
     *         return empty list if there's no dependency
     */
    private List<Feature> getDependencies(Feature feature) {
        Polyfill polyfill = this.polyfills.get(feature.getName());
        if (polyfill != null) {
            return polyfill.getDependencies().stream()
                    .map(featureName -> new Feature(featureName, feature))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void resolveAlias(Map<String, Feature> featureSet) {
        resolveFeatures(featureSet, this::getFromAlias, true);
    }

    private void resolveDependencies(Map<String, Feature> featureSet) {
        resolveFeatures(featureSet, this::getDependencies, false);
    }

    /**
     * Expand/Resolve features through a resolve function
     * @param featureSet feature set to process with resolve function
     * @param resolveFn resolve function (e.g. expand alias, get dependencies)
     * @param removeResolvedFeature whether to remove the polyfill that need to be resolved
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
