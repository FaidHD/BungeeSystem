package de.netdown.bungeesystem;

import de.netdown.bungeesystem.utils.BanManager;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeSystem extends Plugin {

    private Data data;
    private BanManager banManager;

    public void onEnable() {
        data = new Data(this);
        banManager = new BanManager(this);
    }

    public Data getData() {
        return data;
    }

    public BanManager getBanManager() {
        return banManager;
    }
}
