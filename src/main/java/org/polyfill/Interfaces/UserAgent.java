package org.polyfill.Interfaces;

/**
 * Created by reinier.guerra on 10/12/16.
 */
public interface UserAgent {
    public String getFamily();

    public String getMajorVersion();

    public String getMinorVersion();

    public String getVersion();

    String toString();

    boolean isUnknown();

    boolean meetsBaseline();

    boolean satisfies(String range);
}
