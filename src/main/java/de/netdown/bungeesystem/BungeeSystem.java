package de.netdown.bungeesystem;

import de.netdown.bungeesystem.commands.*;
import de.netdown.bungeesystem.listeners.*;
import de.netdown.bungeesystem.utils.BanManager;
import de.netdown.bungeesystem.utils.MaintenanceManager;
import de.netdown.bungeesystem.utils.TabManager;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeSystem extends Plugin {

    private Data data;
    private BanManager banManager;
    private TabManager tabManager;
    private MaintenanceManager maintenanceManager;

    public void onEnable() {
        data = new Data(this);
        banManager = new BanManager(this);
        tabManager = new TabManager(this);
        maintenanceManager = new MaintenanceManager(this);
        initListeners();
        initCommands();
    }

    private void initCommands() {
        new BanCommand(this);
        new UnbanCommand(this);
        new UnmuteCommand(this);
        new CheckCommand(this);
        new BroadcastCommand(this);
        new TeamchatCommand(this);
        new MaintenanceCommand(this);
        new KickCommand(this);
        new TempbanCommand(this);
        new TempmuteCommand(this);
    }

    private void initListeners() {
        new LoginListener(this);
        new ChatListener(this);
        new QuitListener(this);
        new ServerSwitchListener(this);
        new ProxyPingListener(this);
    }

    public Data getData() {
        return data;
    }

    public BanManager getBanManager() {
        return banManager;
    }

    public TabManager getTabManager() {
        return tabManager;
    }

    public MaintenanceManager getMaintenanceManager() {
        return maintenanceManager;
    }
}
