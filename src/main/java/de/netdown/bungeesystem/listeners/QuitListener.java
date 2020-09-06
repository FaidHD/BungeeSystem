package de.netdown.bungeesystem.listeners;

import de.netdown.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class QuitListener implements Listener {

    private BungeeSystem plugin;

    public QuitListener(BungeeSystem plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        if (!plugin.getTabManager().isUseTab()) return;
        for (ProxiedPlayer all : plugin.getProxy().getPlayers()) {
            String header = plugin.getTabManager().getHeader().replaceAll("%online%", String.valueOf(plugin.getProxy().getPlayers().size())).replaceAll("%server%", all.getServer().getInfo().getName()).replaceAll("&", "§");
            String footer = plugin.getTabManager().getFooter().replaceAll("%online%", String.valueOf(plugin.getProxy().getPlayers().size())).replaceAll("%server%", all.getServer().getInfo().getName()).replaceAll("&", "§");
            all.setTabHeader(new TextComponent(header), new TextComponent(footer));
        }
    }

}

