package org.polyfill.interfaces;

/**
 * Created by reinier.guerra on 10/12/16.
 */
public interface UserAgent {
    public String getFamily();

    public String getMajorVersion();

    public String getMinorVersion();

    public String getVersion();

    public String getOperatingSystemName();

    public String getOperatingSystemVersion();

    String toString();

    boolean isUnknown();

    boolean meetsBaseline();

    boolean satisfies(String range);
}
