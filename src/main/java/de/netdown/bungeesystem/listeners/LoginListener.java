package de.netdown.bungeesystem.listeners;

import de.netdown.bungeesystem.BungeeSystem;
import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class LoginListener implements Listener {

    private BungeeSystem plugin;

    public LoginListener(BungeeSystem plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        UUID uuid = event.getConnection().getUniqueId();
        if (plugin.getBanManager().isBanned(uuid)) {
            int[] time = plugin.getBanManager().getBanTime(uuid);
            event.setCancelReason(new TextComponent("§3Net§fDown §8● §cDu bist gebannt\n\n§8➥ §7Grund §8» §9" + plugin.getBanManager().getBanReason(uuid) + "\n §8➥ §7Länge §8» §b" + plugin.getBanManager().getTimeAsString(time) + "\n\n §7Zu unrecht gebannt? Auf unserem Teamspeak kannst du einen §bEntbannungsantrag stellen§7.\n\n§7Teamspeak §8» §3Net§fDown.de"));
            event.setCancelled(true);
        }

        if (plugin.getMaintenanceManager().isMaintenance()) {
            if (!BungeePexBridge.get().hasPermission(uuid, "bungee.maintenance.join", true)) {
                if (!BungeePexBridge.get().hasPermission(uuid, "bungee.maintenance", true)) {
                    if (!plugin.getMaintenanceManager().getWhitelist().contains(uuid.toString())) {
                        event.setCancelReason(new TextComponent(plugin.getMaintenanceManager().getMaintenanceKick().replaceAll("&", "§")));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
