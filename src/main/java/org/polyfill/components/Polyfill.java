package org.polyfill.components;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapper for polyfill metadata
 */
public class Polyfill {

    public static final String BROWSER_REQUIREMENTS_KEY = "browsers";
    public static final String RAW_SOURCE_KEY = "rawSource";
    public static final String MIN_SOURCE_KEY = "minSource";
    public static final String DETECT_SOURCE_KEY = "detectSource";
    public static final String DEPENDENCIES_KEY = "dependencies";
    public static final String ALIASES_KEY = "aliases";
    public static final String LICENSE_KEY = "license";
    public static final String REPO_KEY = "repo";
    public static final String TEST_KEY = "test";
    public static final String TESTS_SOURCE_KEY = "testsSource";

    private Map<String, Object> polyfillMap;
    private String name;

    public Polyfill(String name, Map<String, Object> polyfillMap) {
        this.name = name;
        this.polyfillMap = polyfillMap;
    }

    /*********************** Public Interface ************************/

    /**
     * @return name of polyfill; e.g. Array.of
     */
    @Nullable
    public String getName() {
        return this.name;
    }

    /**
     * @return feature group aliases that contain this feature
     */
    public List<String> getAliases() {
        return getListFromMap(this.polyfillMap, ALIASES_KEY);
    }

    /**
     * @return browser requirements; e.g. {chrome: '*', ios_saf: '&lt;10'}
     */
    public Map<String, Object> getAllBrowserRequirements() {
        return getMapFromMap(this.polyfillMap, BROWSER_REQUIREMENTS_KEY);
    }

    /**
     * @param browserName browser name
     * @return get browser requirement based on browser name: e.g. '*' for browserName = "chrome"
     */
    @Nullable
    public String getBrowserRequirement(String browserName) {
        Map<String, Object> allBrowserRequirements = getAllBrowserRequirements();
        return getStringFromMap(allBrowserRequirements, browserName);
    }

    /**
     * @return a list of dependencies of this polyfill
     */
    public List<String> getDependencies() {
        return getListFromMap(this.polyfillMap, DEPENDENCIES_KEY);
    }

    public String getSource(boolean minify, boolean gated) {
        String sourceKey = minify ? MIN_SOURCE_KEY : RAW_SOURCE_KEY;
        String source = getStringFromMap(this.polyfillMap, sourceKey);
        source = (source == null) ? "" : source;

        String detectSource = getStringFromMap(this.polyfillMap, DETECT_SOURCE_KEY);
        boolean wrapInDetect = gated && detectSource != null;

        if (wrapInDetect && !"".equals(detectSource)) {
            String lf = minify ? "" : "\n";
            return "if(!(" + detectSource + ")){" + lf + source + lf + "}" + lf + lf;
        }

        return source;
    }

    /**
     * @return String representation of this polyfill
     */
    public String toString() {
        return this.polyfillMap.toString();
    }

    /**
     * Overriding equals to define that when all fields of both polyfills
     * are equal, then they are equal
     * @param obj the other polyfill object
     * @return true if all fields of both polyfills are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(Polyfill.class.isAssignableFrom(obj.getClass()))) {
            return false;
        }
        final Polyfill other = (Polyfill)obj;
        if ((this.name == null && other.name != null)
                || (!this.name.equals(other.name))) {
            return false;
        }
        if ((this.polyfillMap == null && other.polyfillMap != null)
                || (!this.polyfillMap.equals(other.polyfillMap))) {
            return false;
        }
        return true;
    }

    /**
     * Compute hashCode using the hashCodes of polyfill's fields to
     * make sure they are valid for the equals method
     * @return hashCode of the polyfill
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + (this.polyfillMap != null ? this.polyfillMap.hashCode() : 0);
        return hash;
    }

    /**
     * Gets the type of license of the polyfill implementation e.g. MIT | Apache
     * @return {String} - The type of license
     */
    public String getLicense() {
        return getStringFromMap(this.polyfillMap, LICENSE_KEY);
    }

    /**
     * Gets the URL to the polyfill repository
     * @return {String} - The URl to the repository
     */
    public String getRepository() {
        return getStringFromMap(this.polyfillMap, REPO_KEY);
    }

    public boolean isTestable() {
        Map<String, Object> testMap = getMapFromMap(this.polyfillMap, TEST_KEY);
        return !(testMap.containsKey("ci") && (Boolean)testMap.get("ci") == false);
    }

    public String getTestsSource() {
        return getStringFromMap(this.polyfillMap, TESTS_SOURCE_KEY);
    }

    public String getDetectSource() {
        return getStringFromMap(this.polyfillMap, DETECT_SOURCE_KEY);
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
        return (value instanceof List) ? (List<String>)value : new ArrayList<>();
    }

    @Nullable
    private Map<String, Object> getMapFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return (value instanceof Map) ? (Map<String, Object>)value : new HashMap<>();
    }
}
