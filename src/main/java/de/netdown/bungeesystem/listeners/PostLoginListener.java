package de.netdown.bungeesystem.listeners;

import de.netdown.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PostLoginListener implements Listener {

    private BungeeSystem plugin;

    public PostLoginListener(BungeeSystem plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (plugin.getBanManager().isBanned(player)) {
            int[] time = plugin.getBanManager().getBanTime(player.getUniqueId());
            player.disconnect(new TextComponent("§3Net§fDown §8● §cDu wurdest gebannt\n\n§8➥ §7Grund §8» §9" + plugin.getBanManager().getBanReason(player.getUniqueId()) + "\n §8➥ §7Länge §8» §b" + plugin.getBanManager().getTimeAsString(time) + "\n\n §7Zu unrecht gebannt? Auf unserem Teamspeak kannst du einen §bEntbannungsantrag stellen§7.\n\n§7Teamspeak §8» §3Net§fDown.de"));
        } else if (plugin.getBanManager().isBannedIP(plugin.getBanManager().getIPFromPlayer(player))) {
            int[] time = plugin.getBanManager().getBanTime(player.getUniqueId());
            if (time != null)
                player.disconnect(new TextComponent("§3Net§fDown §8● §cDeine IP-Adresse ist gebannt\n\n§8➥ §7Grund §8» §9" + plugin.getBanManager().getBanReason(player.getUniqueId()) + "\n §8➥ §7Länge §8» " + plugin.getBanManager().getTimeAsString(time) + "\n\n §7Zu unrecht gebannt? Auf unserem Teamspeak kannst du einen §bEntbannungsantrag stellen§7.\n\n§7Teamspeak §8» §3Net§fDown.de"));
            else
                player.disconnect(new TextComponent("§3Net§fDown §8● §cDeine IP-Adresse ist gebannt\n\n§8➥ §7Grund §8» §9" + plugin.getBanManager().getBanReason(player.getUniqueId()) + "\n §8➥ §7Länge §8» §bPERMANENT\n\n §7Zu unrecht gebannt? Auf unserem Teamspeak kannst du einen §bEntbannungsantrag stellen§7.\n\n§7Teamspeak §8» §3Net§fDown.de"));
        }
    }
}
