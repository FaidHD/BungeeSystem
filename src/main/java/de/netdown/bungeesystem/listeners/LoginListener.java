package de.netdown.bungeesystem.listeners;

import de.netdown.bungeesystem.BungeeSystem;
import net.alpenblock.bungeeperms.BungeePerms;
import net.alpenblock.bungeeperms.BungeePermsAPI;
import net.alpenblock.bungeeperms.User;
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
            long remainingTime = plugin.getBanManager().getRemainingBanTime(uuid);
            if (remainingTime <= System.currentTimeMillis()) {
                if (remainingTime != 0) {
                    plugin.getBanManager().unBan(uuid);
                    for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers())
                        if (all.hasPermission("bungee.ban")) {
                            all.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + event.getConnection().getName() + " §7wurde automatisch wegen Ablauf des Banns entbannt."));
                        }
                    return;
                }
            }
            int[] time = plugin.getBanManager().getBanTime(uuid);
            event.setCancelReason(new TextComponent("§3Net§fDown §8● §cDu wurdest gebannt\n\n§8➥ §7Grund §8» §9" + plugin.getBanManager().getBanReason(uuid) + "\n §8➥ §7Länge §8» §b" + plugin.getBanManager().getTimeAsString(time) + "\n\n §7Zu unrecht gebannt? Auf unserem Teamspeak kannst du einen §bEntbannungsantrag stellen§7.\n\n§7Teamspeak §8» §3Net§fDown.de"));
            event.setCancelled(true);
        }
    }
}
