package org.polyfill.components;

import org.polyfill.interfaces.ConfigLoaderService;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Wrapper for polyfill metadata
 */
@Configurable
public class Polyfill {

    private static final String BROWSER_REQUIREMENTS_KEY = "browsers";
    private static final String RAW_SOURCE_KEY = "rawSource";
    private static final String MIN_SOURCE_KEY = "minSource";
    private static final String DETECT_SOURCE_KEY = "detectSource";
    private static final String DEPENDENCIES_KEY = "dependencies";
    private static final String ALIASES_KEY = "aliases";
    private static final String BASE_DIR_KEY = "baseDir";

    private Map<String, Object> polyfillMap;
    private String name;

    /**
     * Construct a polyfill
     * Name of polyfill is extracted from the 'baseDir' field
     * @param polyfillMap map containing all the info about the polyfill
     */
    public Polyfill(Map<String, Object> polyfillMap) {
        this.polyfillMap = polyfillMap;

        String name = this.getStringFromMap(polyfillMap, BASE_DIR_KEY);
        if (name != null) {
            this.name = name.replace("/", ".");
        }
    }

    /**
     * Construct a polyfill given a polyfill directory
     * Name of polyfill is extracted from the directory name
     * @param polyfillDir directory containing all the info about the polyfill, meta.json, min.js and raw.js file
     * @param configLoaderService TODO: remove the configLoaderService, I tried to injected it but it doesn't work
     */
    public Polyfill(File polyfillDir, ConfigLoaderService configLoaderService) throws IOException {
        Map<String, Object> meta = configLoaderService.getConfig(new File(polyfillDir, "meta.json").getPath());
        File min = new File(polyfillDir.getPath(), "min.js");
        File raw = new File(polyfillDir.getPath(), "raw.js");
        meta.put(RAW_SOURCE_KEY, getFileSource(raw));
        meta.put(MIN_SOURCE_KEY, getFileSource(min));
        this.name = polyfillDir.getName();
        this.polyfillMap = meta;
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
    @Nullable
    public List<String> getAliases() {
        return getListFromMap(this.polyfillMap, ALIASES_KEY);
    }

    /**
     * @return browser requirements; e.g. {chrome: '*', ios_saf: '&lt;10'}
     */
    @Nullable
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
        if (allBrowserRequirements != null) {
            return getStringFromMap(allBrowserRequirements, browserName);
        }
        return null;
    }

    /**
     * @return a list of dependencies of this polyfill
     */
    @Nullable
    public List<String> getDependencies() {
        return getListFromMap(this.polyfillMap, DEPENDENCIES_KEY);
    }

    /**
     * @return the raw source of this polyfill
     */
    public String getRawSource() {
        return getSource(false, false);
    }

    /**
     * @param gated if true, wrap source with feature detection code
     * @return the raw source of this polyfill
     */
    public String getRawSource(boolean gated) {
        return getSource(false, gated);
    }

    /**
     * @return the min source of this polyfill
     */
    public String getMinSource() {
        return getSource(true, false);
    }

    /**
     * @param gated if true, wrap source with feature detection code
     * @return the min source of this polyfill
     */
    public String getMinSource(boolean gated) {
        return getSource(true, gated);
    }

    /**
     * @return String representation of this polyfill
     */
    public String toString() {
        return this.polyfillMap.toString();
    }

    /**************************** Helpers **************************/

    private String getFileSource(File file) {
        StringBuilder source = new StringBuilder("");
        try (FileInputStream fis = new FileInputStream(file)) {
            int content;
            while ((content = fis.read()) != -1) {
                source.append((char) content);
            }
        } catch (IOException e) {
            return "";
        }
        return source.toString();
    }

    private String getSource(boolean minify, boolean gated) {
        StringBuilder outputSource = new StringBuilder();

        String sourceKey = minify ? MIN_SOURCE_KEY : RAW_SOURCE_KEY;
        String source = getStringFromMap(this.polyfillMap, sourceKey);
        if (source == null) {
            source = "";
        }

        String detectSource = getStringFromMap(this.polyfillMap, DETECT_SOURCE_KEY);
        boolean wrapInDetect = gated && detectSource != null;

        if (wrapInDetect) {
            String lf = minify ? "" : "\n";
            outputSource.append("if(!(").append(detectSource).append(")){").append(lf);
            outputSource.append(source).append(lf);
            outputSource.append("}").append(lf).append(lf);
        } else {
            outputSource.append(source);
        }

        return outputSource.toString();
    }

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
