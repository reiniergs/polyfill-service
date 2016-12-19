package org.polyfill.components.userAgent;

import net.sf.uadetector.VersionNumber;
import org.polyfill.interfaces.UserAgent;

public class UserAgentImpl implements UserAgent {

    private String family;
    private VersionNumber version;

    public UserAgentImpl(String family, String versionString) {
        this(family, new VersionNumber(versionString));
    }

    public UserAgentImpl(String family, VersionNumber version) {
        this.family = family;
        this.version = version;
    }

    public String getFamily() {
        return this.family;
    }

    public String getVersion() {
        return version.toVersionString();
    }

    public String getMajorVersion() {
        return version.getMajor();
    }

    public String getMinorVersion() {
        return version.getMinor();
    }

    public String toString() {
        return getFamily() + "/" + getVersion();
    }
}
