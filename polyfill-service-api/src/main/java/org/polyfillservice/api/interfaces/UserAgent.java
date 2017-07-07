package org.polyfillservice.api.interfaces;

/**
 * Created by reinier.guerra on 10/12/16.
 * Simple object for holding useful user agent properties.
 */
public interface UserAgent {

    /**
     * Return the browser family name of the user agent
     * @return the browser family name of the user agent
     *         e.g. Google Chrome's family name is "chrome"
     */
    String getFamily();

    /**
     * Return the major version number of the user agent
     * @return the major version number of the user agent
     *         e.g. if version is 1.2.3, then major is 1
     */
    String getMajorVersion();

    /**
     * Return the minor version number of the user agent
     * @return the minor version number of the user agent
     *         e.g. if version is 1.2.3, then minor is 2
     */
    String getMinorVersion();

    /**
     * Return the version number of the user agent
     * @return the version number of the user agent
     */
    String getVersion();

    /**
     * Return a string representation of UserAgent
     * @return a string representation of UserAgent
     */
    String toString();
}
