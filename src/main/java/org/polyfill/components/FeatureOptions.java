package org.polyfill.components;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by smo on 2/24/17.
 */
public class FeatureOptions {
    public static final String ALWAYS = "always";
    public static final String GATED = "gated";

    private String name;
    private Set<String> flags;

    public FeatureOptions(String featureParam) {
        // e.g. Array.of|always|gated -> ["Array.of", "always", "gated"]
        // index 0 is name, index > 0 is flag
        String[] nameAndFlags = featureParam.split("\\|");

        this.name = nameAndFlags[0];

        this.flags = new HashSet<>();
        for (int i = 1; i < nameAndFlags.length; i++) {
            String flag = nameAndFlags[i];
            this.flags.add(flag);
        }

    }

    public FeatureOptions(String name, List<String> flags) {
        this.name = name;
        this.flags = new HashSet<>(flags);
    }

    public FeatureOptions(String name, FeatureOptions featureOptions) {
        this.name = name;
        this.flags = new HashSet<>(featureOptions.flags);
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

    public void setAlways(boolean isAlways) {
        setFlag(ALWAYS, isAlways);
    }

    public void setGated(boolean isGated) {
        setFlag(GATED, isGated);
    }

    public void copyOptions(FeatureOptions featureOptions) {
        if (this.flags == null) {
            this.flags = new HashSet<>(featureOptions.flags);
        } else {
            this.flags.addAll(featureOptions.flags);
        }
    }

    private void setFlag(String flag, boolean addFlag) {
        if (addFlag) {
            this.flags.add(flag);
        } else {
            this.flags.remove(flag);
        }
    }
}
