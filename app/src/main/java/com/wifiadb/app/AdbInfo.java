package com.wifiadb.app;

/**
 * ADB信息数据类
 */
public class AdbInfo {
    private final String ipAddress;
    private final int port;
    private final boolean enabled;

    public AdbInfo(String ipAddress, int port, boolean enabled) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.enabled = enabled;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public boolean isEnabled() {
        return enabled;
    }
} 