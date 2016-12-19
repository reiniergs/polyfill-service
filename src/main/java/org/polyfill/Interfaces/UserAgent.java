package org.polyfill.interfaces;

/**
 * Created by reinier.guerra on 10/12/16.
 */
public interface UserAgent {

    String getFamily();

    String getMajorVersion();

    String getMinorVersion();

    String getVersion();

    String toString();
}
