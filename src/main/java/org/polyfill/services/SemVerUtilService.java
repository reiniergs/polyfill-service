package org.polyfill.services;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by scottmo on 10/20/16
 */

@Service
public class SemVerUtilService {

    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)((\\.\\d+)+)?");
    private static final Pattern RANGE_PATTERN = Pattern.compile("(\\d+|\\*)\\s*-\\s*(\\d+|\\*)");
    private static final Pattern BOUNDARY_PATTERN = Pattern.compile("((<|>)(=)?)((\\d+)((\\.\\d+)+)?)");

    private static final int MAX_VERSION_GROUPS = 3; // major, minor, patch

    private static final String VERSION_DELIMITER_REGEX = "\\.";

    /**
     * check if version is within range
     * @param checkVersion - the version number that needs to be checked
     * @param range - range of user agent version (e.g. 4 - 6, >=5, >4, *, 10 - *).
     *                return false if range is invalid
     */
    public boolean isVersionInRange(String checkVersion, String range) {
        range = range.replace(" ", "");

        if (isAnyVersionAllowed(range)) {
            return true;
        }

        Matcher matcher;

        matcher = RANGE_PATTERN.matcher(range);
        if (matcher.find()) {
            String rangeMin = matcher.group(1);
            String rangeMax = matcher.group(2);
            return compareVersionRange(checkVersion, ">=", rangeMin)
                    && compareVersionRange(checkVersion, "<=", rangeMax);
        }

        matcher = BOUNDARY_PATTERN.matcher(range);
        if (matcher.find()) {
            String comparator = matcher.group(1);
            String upperOrLowerBound = matcher.group(4);
            return compareVersionRange(checkVersion, comparator, upperOrLowerBound);
        }

        matcher = VERSION_PATTERN.matcher(range);
        if (matcher.find()) {
            return compareVersionRange(checkVersion, "==", range);
        }

        return false;
    }

    /**
     * Check if (@param range) is *, allowing all versions
     */
    private boolean isAnyVersionAllowed(String range) {
        return range.equals("*");
    }

    /**
     * Check if version is within range
     * @param checkVersion - the version number to be checked
     * @param comparator - <, <=, >, >=, ==
     * @param range - either upper or lower bound depending on the comparator
     */
    private boolean compareVersionRange(String checkVersion, String comparator, String range) {
        List<String> checkGroups = parseVersionToList(checkVersion, "0");
        List<String> rangeGroups = parseVersionToList(range);

        if (rangeGroups.size() > checkGroups.size()) return false;

        char mainComparator = comparator.charAt(0);
        for (int i = 0; i < rangeGroups.size(); i++) {
            if (isAnyVersionAllowed(rangeGroups.get(i))) {
                return true;
            }
            int diff = Integer.parseInt(checkGroups.get(i)) - Integer.parseInt(rangeGroups.get(i));
            if (mainComparator == '<') {
                if (diff < 0) return true;
                if (diff > 0) return false;
            }
            else if (mainComparator == '>') {
                if (diff > 0) return true;
                if (diff < 0) return false;
            }
            else if (mainComparator == '=') {
                if (diff != 0) return false;
            }
        }

        // at this point, checkVersion == boundaryVersion, so
        // return true if comparator is either <= or >=, else false
        return (comparator.length() == 2);
    }

    /**
     * Extract major, minor, patch groups from (@param version) and store them in a list
     * If (@param filler) is not null, any missing groups is supplemented with the (@param filler)
     * Normally (@param filler) should be "0", so from 3.2 we would get 3.2.0
     * @param version - version string to extract the groups from
     * @param filler - backup we use for missing groups
     * @return a list of version groups extracted from (@param version)
     */
    private List<String> parseVersionToList(String version, String filler) {
         List<String> versionGroups = Arrays.asList(version.split(VERSION_DELIMITER_REGEX));
        if (filler != null && MAX_VERSION_GROUPS > versionGroups.size()) {
            // create a new ArrayList to make it modifiable because asList creates an unmodifiable list
            versionGroups = new ArrayList<String>(versionGroups);
            for (int missingGroups = MAX_VERSION_GROUPS - versionGroups.size(); missingGroups > 0; missingGroups--) {
                versionGroups.add(filler);
            }
        }
        return versionGroups;
    }

    private List<String> parseVersionToList(String version) {
        return parseVersionToList(version, null);
    }
}
