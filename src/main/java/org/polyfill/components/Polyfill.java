package org.polyfill.components;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class Polyfill {
    // supported fields for access
    private static final String BROWSER_REQUIREMENTS_KEY = "browsers";
    private static final String RAW_SOURCE_KEY = "rawSource";
    private static final String MIN_SOURCE_KEY = "minSource";
    private static final String DEPENDENCIES_KEY = "dependencies";
    private static final String ALIASES_KEY = "aliases";
    private static final String BASE_DIR_KEY = "baseDir";

    // polyfill map loaded from config file
    private Map<String, Object> polyfillMap;
    // name for the polyfill
    private String name;

    /**
     * Construct a polyfill
     * Name of polyfill is extracted from the 'baseDir' field
     * @param polyfillMap - map containing all the info about the polyfill
     */
    public Polyfill(Map<String, Object> polyfillMap) {
        this.polyfillMap = polyfillMap;

        String name = this.getStringFromMap(polyfillMap, BASE_DIR_KEY);
        if (name != null) {
            this.name = name.replace("/", ".");
        }
    }

    /**
     * Construct a polyfill
     * @param name - name of the polyfill, e.g. Array.of
     * @param polyfillMap - map containing all the info about the polyfill
     */
    public Polyfill(String name, Map<String, Object> polyfillMap) {
        this.polyfillMap = polyfillMap;
        this.name = name;
    }

    /*********************** Public Interface ************************/

    @Nullable
    public String getName() {
        return this.name;
    }

    @Nullable
    public List<String> getAliases() {
        return getListFromMap(this.polyfillMap, ALIASES_KEY);
    }

    @Nullable
    public Map<String, Object> getAllBrowserRequirements() {
        return getMapFromMap(this.polyfillMap, BROWSER_REQUIREMENTS_KEY);
    }

    @Nullable
    public String getBrowserRequirement(String browserName) {
        Map<String, Object> allBrowserRequirements = getAllBrowserRequirements();
        if (allBrowserRequirements != null) {
            return getStringFromMap(allBrowserRequirements, browserName);
        }
        return null;
    }

    @Nullable
    public List<String> getDependencies() {
        return getListFromMap(this.polyfillMap, DEPENDENCIES_KEY);
    }


    public String getRawSource() {
        return getStringFromMap(this.polyfillMap, RAW_SOURCE_KEY);
    }

    public String getMinSource() {
        return getStringFromMap(this.polyfillMap, MIN_SOURCE_KEY);
    }

    public String toString() {
        return this.polyfillMap.toString();
    }

    /**************************** Helpers **************************/

    @Nullable
    private String getStringFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return (value instanceof String) ? (String)value : null;
    }

    @Nullable
    private List<String> getListFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return (value instanceof List) ? (List<String>)value : null;
    }

    @Nullable
    private Map<String, Object> getMapFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return (value instanceof Map) ? (Map<String, Object>)value : null;
    }
}
