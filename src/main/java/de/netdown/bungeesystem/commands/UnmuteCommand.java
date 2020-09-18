package de.netdown.bungeesystem.commands;

import de.netdown.bungeesystem.BungeeSystem;
import de.netdown.bungeesystem.utils.BanManager;
import de.netdown.bungeesystem.utils.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
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
        if (commandSender.hasPermission("bungee.unmute")) {
            if (args.length == 2) {
                BanManager manager = plugin.getBanManager();
                UUID uuid = UUIDFetcher.getUUID(args[0]);
                boolean lastHistory;
                try {
                    lastHistory = Boolean.valueOf(args[1]);
                } catch (Exception e) {
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Bitte benutze: /unmute <Spieler> <true, false>"));
                    return;
                }
                if (uuid != null) {
                    if (manager.isMuted(uuid)) {
                        manager.unMute(uuid, lastHistory);
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Du hast den Spieler §b" + args[0] + " §7entmutet."));
                    } else
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[0] + " §7ist nicht gemutet."));
                } else
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[0] + " §7existiert nicht."));
            } else
                commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Bitte benutze: /unmute <Spieler> <true, false>"));
        }else
            commandSender.sendMessage(new TextComponent(plugin.getData().getNoPerm()));
    }
}
