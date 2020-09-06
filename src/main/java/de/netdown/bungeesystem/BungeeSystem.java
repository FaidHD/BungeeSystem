package de.netdown.bungeesystem;

import de.netdown.bungeesystem.commands.*;
import de.netdown.bungeesystem.listeners.ChatListener;
import de.netdown.bungeesystem.listeners.LoginListener;
import de.netdown.bungeesystem.listeners.QuitListener;
import de.netdown.bungeesystem.listeners.ServerSwitchListener;
import de.netdown.bungeesystem.utils.BanManager;
import de.netdown.bungeesystem.utils.TabManager;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeSystem extends Plugin {

    private Data data;
    private BanManager banManager;
    private TabManager tabManager;

    public void onEnable() {
        data = new Data(this);
        banManager = new BanManager(this);
        tabManager = new TabManager(this);
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
    }

    private void initListeners() {
        new LoginListener(this);
        new ChatListener(this);
        new QuitListener(this);
        new ServerSwitchListener(this);
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
}
