package de.netdown.bungeesystem.listeners;

import de.netdown.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class ServerSwitchListener implements Listener {

    private BungeeSystem plugin;

    public ServerSwitchListener(BungeeSystem plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onServerConnect(ServerConnectedEvent event) {
        if(!plugin.getTabManager().isUseTab()) return;
        plugin.getTabManager().setTabAll();
    }

    @EventHandler
    public void onServerDisconnect(ServerDisconnectEvent event) {
        if(!plugin.getTabManager().isUseTab()) return;
        plugin.getTabManager().setTabAll();
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        if (!plugin.getTabManager().isUseTab()) return;
        plugin.getTabManager().setTab(event.getPlayer());
    }
}
