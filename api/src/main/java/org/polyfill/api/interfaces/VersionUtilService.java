package org.polyfill.api.interfaces;

/**
 * Created by smo on 2/26/17.
 * Service to check version number
 */
public interface VersionUtilService {

    /**
     * Check if {@code checkVersion} satisfies {@code range}
     * @param checkVersion the version number that needs to be checked
     * @param range range of user agent version (e.g. 4 - 6, &gt;=5, &gt;4, *, 10 - *).
     * @return return true if version is in range and return false if range is invalid
     */
    boolean isVersionInRange(String checkVersion, String range);
}
