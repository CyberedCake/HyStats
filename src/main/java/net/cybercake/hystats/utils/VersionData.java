package net.cybercake.hystats.utils;

public class VersionData {

    private String version;

    public VersionData version(String version) {
        this.version = version;
        return this;
    }

    public String version() {
        return this.version;
    }

}
