package org.polyfillservice.api.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by smo on 2/24/17.
 * Wrapper for carrying query information along with polyfill
 */
public class Feature {
    public static final String ALWAYS = "always";
    public static final String GATED = "gated";

    private String name;
    private Polyfill polyfill;
    private boolean isAlways;
    private boolean isGated;

    private Set<String> requiredBys = new HashSet<>();

    /** 
     * Construct a feature that is not gated nor always loaded 
     * @param name name of the feature 
     */
    public Feature(String name) {
        this(name, false, false);
    }

    /** 
     * Construct a feature 
     * @param name name of the feature 
     * @param isGated whether getSource() should wrap source code with detection code 
     * @param isAlways whether to tell service to always load this feature 
     */
    public Feature(String name, boolean isGated, boolean isAlways) {
        this.name = name;
        this.isGated = isGated;
        this.isAlways = isAlways;
    }

    /** 
     * Construct a feature and inherit another feature's attributes 
     * Used when the other feature requires this feature 
     * @param name name of the feature 
     * @param feature feature that requires this feature 
     */
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

    public Polyfill getPolyfill() {
        return this.polyfill;
    }

    public void copyFlags(Feature feature) {
        this.isGated = feature.isGated;
        this.isAlways = feature.isAlways;
    }
}
