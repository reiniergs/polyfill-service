package org.polyfill.components;

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

    @Override
    public String getFamily() {
        return this.family;
    }

    @Override
    public String getVersion() {
        return version.toVersionString();
    }

    @Override
    public String getMajorVersion() {
        return version.getMajor();
    }

    @Override
    public String getMinorVersion() {
        return version.getMinor();
    }

    @Override
    public String toString() {
        return getFamily() + "/" + getVersion();
    }
}
