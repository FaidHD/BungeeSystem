package de.netdown.bungeesystem.listeners;

import de.netdown.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatListener implements Listener {

    private BungeeSystem plugin;

    public ChatListener(BungeeSystem plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (!(event.getSender().isConnected())) return;
        if (!(event.getSender() instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String msg = event.getMessage();
        if (msg.charAt(0) != '/' || msg.startsWith("/msg") || msg.startsWith("/tell") || msg.startsWith("/me") || msg.startsWith("/say"))
            if (plugin.getBanManager().isMuted(player.getUniqueId())) {
                event.setCancelled(true);
                player.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Du bist wegen §b" + plugin.getBanManager().getMuteReason(player.getUniqueId()) + " §7gemutet."));
                player.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ Länge §8» " + plugin.getBanManager().getTimeAsString(plugin.getBanManager().getMuteTime(player.getUniqueId()))));
            }
    }
}
