package org.polyfill.utils;

public interface UserAgent {

    String getFamily();

    String getMajorVersion();

    String getMinorVersion();

    String getVersion();

    String toString();

    boolean isUnknown();

    boolean meetsBaseline();

    boolean satisfies(String range);
}
