package org.polyfill.api.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by smo on 2/24/17.
 */
public class Feature {
    public static final String ALWAYS = "always";
    public static final String GATED = "gated";

    private String name;
    private Polyfill polyfill;
    private boolean isAlways;
    private boolean isGated;

    private Set<String> requiredBys = new HashSet<>();

    public Feature(String name) {
        this(name, false, false);
    }

    public Feature(String name, boolean isGated, boolean isAlways) {
        this.name = name;
        this.isGated = isGated;
        this.isAlways = isAlways;
    }

    public Feature(String name, Feature feature) {
        this.name = name;
        this.isGated = feature.isGated;
        this.isAlways = feature.isAlways;
        this.requiredBys.addAll(feature.requiredBys);
        this.requiredBys.add(feature.name);
    }

    public String getName() {
        return this.name;
    }

    public boolean isAlways() {
        return this.isAlways;
    }

    public void setAlways(boolean isAlways) {
        this.isAlways = isAlways;
    }

    public boolean isGated() {
        return this.isGated;
    }

    public void setGated(boolean isGated) {
        this.isGated = isGated;
    }

    public List<String> getRequiredBys() {
        return new ArrayList<>(this.requiredBys);
    }

    public void copyRequiredBys(Feature feature) {
        this.requiredBys.addAll(feature.requiredBys);
    }

    public void setPolyfill(Polyfill polyfill) {
        this.polyfill = polyfill;
    }

    public String getSource(boolean minify) {
        String detectSource = this.polyfill.getDetectSource();
        String source = minify ? this.polyfill.getMinSource() : this.polyfill.getRawSource();
        source = (source == null) ? "" : source;

        boolean wrapInDetect = this.isGated && detectSource != null;

        if (wrapInDetect && !"".equals(detectSource)) {
            String lf = minify ? "" : "\n";
            return "if(!(" + detectSource + ")){" + lf + source + lf + "}" + lf + lf;
        }

        return source;
    }

    public void copyFlags(Feature feature) {
        this.isGated = feature.isGated;
        this.isAlways = feature.isAlways;
    }

    public String getLicense() {
        return this.polyfill.getLicense();
    }

    public List<String> getDependencies() {
        return this.polyfill.getDependencies();
    }
}
