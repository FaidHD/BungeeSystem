package de.netdown.bungeesystem.listeners;

import de.netdown.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class QuitListener implements Listener {

    private BungeeSystem plugin;

    public QuitListener(BungeeSystem plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        if (!plugin.getTabManager().isUseTab()) return;
        plugin.getTabManager().setTabAll();
    }

}

