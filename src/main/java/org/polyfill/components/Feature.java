package org.polyfill.components;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by smo on 2/24/17.
 */
public class Feature {
    public static final String ALWAYS = "always";
    public static final String GATED = "gated";

    private String name;
    private Set<String> flags;
    private Polyfill polyfill;

    public Feature(String queryParam) {
        // e.g. Array.of|always|gated -> ["Array.of", "always", "gated"]
        // index 0 is name, index > 0 is flag
        String[] nameAndFlags = queryParam.split("\\|");

        this.name = nameAndFlags[0];
        this.flags = new HashSet<>();
        for (int i = 1; i < nameAndFlags.length; i++) {
            String flag = nameAndFlags[i];
            flags.add(flag);
        }
    }

    public Feature(String name, Set<String> flags) {
        this.name = name;
        this.flags = flags;
    }

    public Feature(String name, Feature feature) {
        this.name = name;
        this.flags = new HashSet<>(feature.flags);
    }

    public String getName() {
        return this.name;
    }

    public String getSource(boolean minify) {
        return this.polyfill.getSource(minify, this.flags.contains(GATED));
    }

    public boolean isGated() {
        return this.flags.contains(GATED);
    }

    public boolean isAlways() {
        return this.flags.contains(ALWAYS);
    }

    public void setPolyfill(Polyfill polyfill) {
        this.polyfill = polyfill;
    }

    public void addFlags(Set<String> flags) {
        if (this.flags == null) {
            this.flags = new HashSet<>(flags);
        } else {
            this.flags.addAll(flags);
        }
    }

    public void copyFlags(Feature feature) {
        addFlags(feature.flags);
    }
}
