package org.polyfill.components;

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

    private Set<String> flags = new HashSet<>();
    private List<String> requiredBys = new ArrayList<>();

    public Feature(String queryParam) {
        // e.g. Array.of|always|gated -> ["Array.of", "always", "gated"]
        // index 0 is name, index > 0 is flag
        String[] nameAndFlags = queryParam.split("\\|");
        this.name = nameAndFlags[0];
        for (int i = 1; i < nameAndFlags.length; i++) {
            String flag = nameAndFlags[i];
            this.flags.add(flag);
        }
    }

    public Feature(String name, Set<String> flags) {
        this.name = name;
        this.flags.addAll(flags);
    }

    public Feature(String name, Feature feature) {
        this.name = name;
        this.flags.addAll(feature.flags);
        this.requiredBys.addAll(feature.requiredBys);
        this.requiredBys.add(feature.name);
    }

    public String getName() {
        return this.name;
    }

    public boolean isGated() {
        return this.flags.contains(GATED);
    }

    public boolean isAlways() {
        return this.flags.contains(ALWAYS);
    }

    public void addFlags(Set<String> flags) {
        this.flags.addAll(flags);
    }

    public List<String> getRequiredBys() {
        return this.requiredBys;
    }

    public void copyRequiredBys(Feature feature) {
        this.requiredBys.addAll(feature.requiredBys);
    }

    public void setPolyfill(Polyfill polyfill) {
        this.polyfill = polyfill;
    }

    public String getSource(boolean minify) {
        return this.polyfill.getSource(minify, this.flags.contains(GATED));
    }

    public void copyFlags(Feature feature) {
        this.flags.addAll(feature.flags);
    }

    public String getLicense() {
        return this.polyfill.getLicense();
    }

    public List<String> getDependencies() {
        return this.polyfill.getDependencies();
    }
}
