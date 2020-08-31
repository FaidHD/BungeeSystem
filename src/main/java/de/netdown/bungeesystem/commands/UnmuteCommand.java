package de.netdown.bungeesystem.commands;

import de.netdown.bungeesystem.BungeeSystem;
import de.netdown.bungeesystem.utils.BanManager;
import de.netdown.bungeesystem.utils.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class UnmuteCommand extends Command {

    private BungeeSystem plugin;

    public UnmuteCommand(BungeeSystem plugin) {
        super("unmute");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) commandSender;
            if (player.hasPermission("bungee.unban")) {
                if (args.length == 1) {
                    BanManager manager = plugin.getBanManager();
                    UUID uuid = UUIDFetcher.getUUID(args[0]);
                    if (uuid != null) {
                        if (manager.isMuted(uuid)) {
                            manager.unMute(uuid);
                            player.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Du hast den Spieler §b" + args[0] + " §7entmutet."));
                        } else
                            player.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[0] + " §7ist nicht gemuted."));
                    } else
                        player.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[0] + " §7existiert nicht."));
                } else
                    player.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Bitte benutze: /unmute <Spieler>"));
            } else
                player.sendMessage(new TextComponent(plugin.getData().getNoPerm()));
        } else {
            if (args.length == 1) {
                BanManager manager = plugin.getBanManager();
                UUID uuid = UUIDFetcher.getUUID(args[0]);
                if (uuid != null) {
                    if (manager.isBanned(uuid)) {
                        manager.unBan(uuid);
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Du hast den Spieler §b" + args[0] + " §7entmutet."));
                    } else
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[0] + " §7ist nicht gemutet."));
                } else
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[0] + " §7existiert nicht."));
            } else
                commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Bitte benutze: /unmute <Spieler>"));
        }
    }
}
