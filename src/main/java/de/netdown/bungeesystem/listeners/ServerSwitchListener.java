package de.netdown.bungeesystem.listeners;

import de.netdown.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerSwitchListener implements Listener {

    private BungeeSystem plugin;

    public ServerSwitchListener(BungeeSystem plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if(!plugin.getTabManager().isUseTab()) return;
        String header = plugin.getTabManager().getHeader().replaceAll("%online%", String.valueOf(plugin.getProxy().getPlayers().size())).replaceAll("%server%", player.getServer().getInfo().getName()).replaceAll("&", "§");
        String footer = plugin.getTabManager().getFooter().replaceAll("%online%", String.valueOf(plugin.getProxy().getPlayers().size())).replaceAll("%server%", player.getServer().getInfo().getName()).replaceAll("&", "§");
        player.setTabHeader(new TextComponent(header), new TextComponent(footer));
    }
}
