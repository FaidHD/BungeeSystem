package de.netdown.bungeesystem.utils;

import de.netdown.bungeesystem.BungeeSystem;

public class MaintenanceManager {

    private BungeeSystem plugin;

    private boolean maintenance;
    private boolean motdSupport;

    private String motdLine1;
    private String motdLine2;

    public MaintenanceManager(BungeeSystem plugin) {
        this.plugin = plugin;
    }
}
