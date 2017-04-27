package org.polyfill.api.components;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Author: smo
 * Immutable polyfill wrapper of all the metadata
 */
public class Polyfill {

    private String name;
    private String rawSource;
    private String minSource;
    private String detectSource;
    private String testsSource;
    private String license;
    private String repository;
    private boolean isTestable;
    private List<String> aliases;
    private List<String> dependencies;
    private Map<String, String> browserRequirements;

    private Polyfill(Builder builder) {
        this.name = builder.name;
        this.rawSource = builder.rawSource;
        this.minSource = builder.minSource;
        this.detectSource = builder.detectSource;
        this.testsSource = builder.testsSource;
        this.license = builder.license;
        this.repository = builder.repository;
        this.isTestable = builder.isTestable;
        this.aliases = builder.aliases;
        this.dependencies = builder.dependencies;
        this.browserRequirements = builder.browserRequirements;
    }

    /**
     * Gets the string representation of the polyfill
     * @return String representation of this polyfill
     */
    public String toString() {
        return "{ name:" + this.name
                + ", aliases: " + this.aliases.toString()
                + ", browsers: " + this.browserRequirements.toString()
                + ", dependencies: " + this.dependencies.toString()
                + ", license: " + this.license
                + ", repository: " + this.repository
                + ", rawSource: " + this.rawSource
                + ", minSource: " + this.minSource
                + ", detectSource: " + this.detectSource
                + ", testsSource: " + this.testsSource
                + ", isTestable: " + this.isTestable
                + "}";
    }

    /**
     * Gets the name of the polyfill: e.g. Array.of
     * @return the name of polyfill
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return feature group aliases that contain this feature
     */
    public List<String> getAliases() {
        return this.aliases;
    }

    /**
     * @return browser requirements; e.g. {chrome: '*', ios_saf: '&lt;10'}
     */
    public Map<String, String> getAllBrowserRequirements() {
        return this.browserRequirements;
    }

    /**
     * Gets the browser requirement based on browser name: e.g. '*' for browserName = "chrome"
     * @param browserName browser name
     * @return {String} - the specific browser requirement
     */
    @Nullable
    public String getBrowserRequirement(String browserName) {
        Map<String, String> allBrowserRequirements = getAllBrowserRequirements();
        return allBrowserRequirements.get(browserName);
    }

    /**
     * Gets a list of dependencies
     * @return {List} - The dependencies of this polyfill
     */
    public List<String> getDependencies() {
        return this.dependencies;
    }

    /**
     * Gets the source for detecting whether the polyfill is needed
     * @return {String} - The detect source of the polyfill
     */
    @Nullable
    public String getDetectSource() {
        return this.detectSource;
    }

    /**
     * Gets the minified source of the polyfill
     * @return {String} - The minified source of the polyfill
     */
    @Nullable
    public String getMinSource() {
        return this.minSource;
    }

    /**
     * Gets the raw source of the polyfill
     * @return {String} - The raw source of the polyfill
     */
    @Nullable
    public String getRawSource() {
        return this.rawSource;
    }

    /**
     * Gets the type of license of the polyfill implementation e.g. MIT | Apache
     * @return {String} - The type of license
     */
    @Nullable
    public String getLicense() {
        return this.license;
    }

    /**
     * Gets the URL to the polyfill repository
     * @return {String} - The URl to the repository
     */
    @Nullable
    public String getRepository() {
        return this.repository;
    }

    /**
     * Gets the source of mocha tests
     * @return {String} - The source of mocha tests
     */
    @Nullable
    public String getTestsSource() {
        return this.testsSource;
    }

    /**
     * Checks whether this polyfill has tests and is testable
     * @return {boolean} - whether polyfill is testable
     */
    public boolean isTestable() {
        return this.isTestable;
    }

    /**
     * Using builder pattern to make sure Polyfill is immutable once constructed
     */
    public static class Builder {

        private String name;
        private String rawSource;
        private String minSource;
        private String detectSource;
        private String testsSource;
        private String license;
        private String repository;
        private boolean isTestable;
        private List<String> aliases;
        private List<String> dependencies;
        private Map<String, String> browserRequirements;

        public Builder(String name) {
            this.name = name;
            this.isTestable = false;
            this.aliases = Collections.emptyList();
            this.dependencies = Collections.emptyList();
            this.browserRequirements = Collections.emptyMap();
        }

        public Polyfill build() {
            return new Polyfill(this);
        }

        public Builder rawSource(String rawSource) {
            this.rawSource = rawSource;
            return this;
        }

        public Builder minSource(String minSource) {
            this.minSource = minSource;
            return this;
        }

        public Builder detectSource(String detectSource) {
            this.detectSource = detectSource;
            return this;
        }

        public Builder testsSource(String testsSource) {
            this.testsSource = testsSource;
            return this;
        }

        public Builder license(String license) {
            this.license = license;
            return this;
        }

        public Builder repository(String repository) {
            this.repository = repository;
            return this;
        }

        public Builder isTestable(boolean testable) {
            isTestable = testable;
            return this;
        }

        public Builder aliases(List<String> aliases) {
            if (aliases != null && !aliases.isEmpty()) {
                this.aliases = Collections.unmodifiableList(aliases);
            }
            return this;
        }

        public Builder dependencies(List<String> dependencies) {
            if (dependencies != null && !dependencies.isEmpty()) {
                this.dependencies = Collections.unmodifiableList(dependencies);
            }
            return this;
        }

        public Builder browserRequirements(Map<String, String> browserRequirements) {
            if (browserRequirements != null && !browserRequirements.isEmpty()) {
                this.browserRequirements = Collections.unmodifiableMap(browserRequirements);
            }
            return this;
        }
    }
}
