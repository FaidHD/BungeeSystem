package de.netdown.bungeesystem.utils;

import de.netdown.bungeesystem.BungeeSystem;

public class MaintenanceManager {

    private BungeeSystem plugin;

    private boolean maintenance;
    private boolean motdSupport;
    private boolean protocolSupport;

    private String motdLine1;
    private String motdLine2;
    private String protocolLine;

    public MaintenanceManager(BungeeSystem plugin) {
        this.plugin = plugin;
    }

    public boolean isProtocolSupport() {
        return protocolSupport;
    }

    public String getProtocolLine() {
        return protocolLine;
    }

    public boolean isMotdSupport() {
        return motdSupport;
    }

    public String getMotdLine1() {
        return motdLine1;
    }

    public String getMotdLine2() {
        return motdLine2;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }
}
