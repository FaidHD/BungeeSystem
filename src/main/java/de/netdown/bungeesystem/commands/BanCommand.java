package de.netdown.bungeesystem.commands;

import de.netdown.bungeesystem.BungeeSystem;
import de.netdown.bungeesystem.utils.BanManager;
import de.netdown.bungeesystem.utils.Reason;
import de.netdown.bungeesystem.utils.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import javax.swing.*;
import java.util.UUID;

public class BanCommand extends Command {

    private BungeeSystem plugin;

    public BanCommand(BungeeSystem plugin) {
        super("ban");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("bungee.ban") || commandSender.hasPermission("bungee.ban.all")) {
            if (args.length == 2) {
                try {
                    Reason reason = plugin.getBanManager().getReasonTemplate(Integer.parseInt(args[1]));
                    if (reason == null) {
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Diese ID ist nicht bekannt."));
                        return;
                    }
                    UUID uuid = UUIDFetcher.getUUID(args[0]);
                    if (uuid == null) {
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Dieser Spieler existiert nicht."));
                        return;
                    }
                    if (!reason.isCanSupBan()) {
                        if (commandSender.hasPermission("bungee.ban.all")) {
                            removeBan(reason, uuid);
                            ProxiedPlayer player = plugin.getProxy().getPlayer(uuid);
                            if (player == null) {
                                if (commandSender instanceof ProxiedPlayer)
                                    plugin.getBanManager().banOfflinePlayerByPlayer(args[0], (ProxiedPlayer) commandSender, reason);
                                else
                                    plugin.getBanManager().banOfflinePlayerByConsole(args[0], reason);
                            } else {
                                if (commandSender instanceof ProxiedPlayer)
                                    plugin.getBanManager().banPlayerByPlayer(player, (ProxiedPlayer) commandSender, reason);
                                else
                                    plugin.getBanManager().banPlayerByConsole(player, reason);
                            }
                            for(ProxiedPlayer all : plugin.getProxy().getPlayers())
                                if(all.hasPermission("bungee.ban") || all.hasPermission("bungee.ban.all")) {

                                }
                        } else
                            commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Du darfst für diese ID nicht bannen."));
                        return;
                    }
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Bitte gebe als ID nur ganze Zahlen an."));
                }
            } else
                sendHelp(commandSender);
        } else
            commandSender.sendMessage(new TextComponent(plugin.getData().getNoPerm()));
    }

    private void sendHelp(CommandSender commandSender) {
        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Bitte benutze: /ban <Spieler> <ID>"));
        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Hier die Verfügbaren ID's"));
        for (Reason reason : plugin.getBanManager().getReasons()) {
            if (!reason.isCanSupBan() && commandSender.hasPermission("bungee.ban.all"))
                if (reason.getReasonType() == Reason.ReasonType.BAN)
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §b" + reason.getId() + " §8» §a" + reason.getReason()));
        }
        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Mutes"));
        for (Reason reason : plugin.getBanManager().getReasons()) {
            if (!reason.isCanSupBan() && commandSender.hasPermission("bungee.ban.all"))
                if (reason.getReasonType() == Reason.ReasonType.MUTE)
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §b" + reason.getId() + " §8» §a" + reason.getReason()));
        }
    }

    private void removeBan(Reason reason, UUID uuid) {
        if (reason.getReasonType() == Reason.ReasonType.BAN) {
            if (plugin.getBanManager().isBanned(uuid))
                plugin.getBanManager().unBan(uuid);
        } else {
            if (plugin.getBanManager().isMuted(uuid))
                plugin.getBanManager().unMute(uuid);
        }
    }

}
