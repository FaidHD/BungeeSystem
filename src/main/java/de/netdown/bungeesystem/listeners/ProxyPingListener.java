package de.netdown.bungeesystem.listeners;

import de.netdown.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProxyPingListener implements Listener {

    private BungeeSystem plugin;

    public ProxyPingListener(BungeeSystem plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        ServerPing ping = event.getResponse();
        ServerPing.Protocol protocol = ping.getVersion();
        if (plugin.getMaintenanceManager().isMaintenance()) {
            ping.getPlayers().setOnline(plugin.getMaintenanceManager().getPlayerList().size());
            ping.getPlayers().setSample(plugin.getMaintenanceManager().getPlayerInfo());
            protocol.setProtocol(486);
            protocol.setName(plugin.getMaintenanceManager().getProtocolLine().replaceAll("&", "§"));
            ping.setDescriptionComponent(new TextComponent(plugin.getMaintenanceManager().getMaintenanceLine1().replaceAll("&", "§") + "\n" + plugin.getMaintenanceManager().getMaintenanceLine2().replaceAll("&", "§")));
        } else
            ping.setDescriptionComponent(new TextComponent(plugin.getMaintenanceManager().getMotdLine1().replaceAll("&", "§") + "\n" + plugin.getMaintenanceManager().getMotdLine2().replaceAll("&", "§")));
        event.setResponse(ping);
    }

}
