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
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) commandSender;
            if (player.hasPermission("bungee.ban.all") || player.hasPermission("bungee.ban")) {
                if (args.length == 2) {
                    try {
                        BanManager manager = plugin.getBanManager();
                        Reason reason = manager.getReasonTemplate(Integer.parseInt(args[1]));
                        if (!reason.isCanSupBan() || player.hasPermission("bungee.ban.all")) {
                            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
                            if (target != null) {
                                if (reason.getReasonType() == Reason.ReasonType.BAN) {
                                    if (manager.isBanned(target.getUniqueId()))
                                        manager.unBan(target.getUniqueId());
                                    manager.banPlayerByPlayer(target, player, reason);
                                } else {
                                    if (manager.isMuted(target.getUniqueId()))
                                        manager.unMute(target.getUniqueId());
                                    manager.mutePlayerByPlayer(target, player, reason);
                                }
                                int[] time = manager.secondsToArray(reason.getSeconds());
                                for (ProxiedPlayer a : ProxyServer.getInstance().getPlayers()) {
                                    if (a.hasPermission("bungee.ban")) {
                                        if (reason.getReasonType() == Reason.ReasonType.BAN)
                                            a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + target.getName() + " §7wurde gebannt."));
                                        else
                                            a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + target.getName() + " §7wurde gemutet."));
                                        a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Grund §8» §b" + reason.getReason()));
                                        a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Länge §8» " + manager.getTimeAsString(time)));
                                        a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Von §8» §b" + player.getName()));
                                    }
                                }
                            } else {
                                UUID tUUID = UUIDFetcher.getUUID(args[0]);
                                if (tUUID == null) {
                                    player.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[0] + " §7existiert nicht."));
                                    return;
                                }
                                if (reason.getReasonType() == Reason.ReasonType.BAN) {
                                    if (manager.isBanned(tUUID))
                                        manager.unBan(tUUID);
                                    manager.banOfflinePlayerByPlayer(args[0], player, reason);
                                } else {
                                    if (manager.isMuted(tUUID))
                                        manager.unMute(tUUID);
                                    manager.muteOfflinePlayerByPlayer(args[0], player, reason);
                                }
                                int[] time = manager.secondsToArray(reason.getSeconds());
                                for (ProxiedPlayer a : ProxyServer.getInstance().getPlayers()) {
                                    if (a.hasPermission("bungee.ban")) {
                                        if (reason.getReasonType() == Reason.ReasonType.BAN)
                                            a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[0] + " §7wurde gebannt."));
                                        else
                                            a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[0] + " §7wurde gemutet."));
                                        a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Grund §8» §b" + reason.getReason()));
                                        a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Länge §8» " + manager.getTimeAsString(time)));
                                        a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Von §8» §b" + player.getName()));
                                    }
                                }
                            }
                        } else
                            player.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Du hast keine Rechte, um einen Spieler diesen Gründen zu bannen."));
                    } catch (NumberFormatException e) {
                        sendHelp(player);
                    }
                } else
                    sendHelp(player);
            } else
                player.sendMessage(new TextComponent(plugin.getData().getNoPerm()));
        } else {
            if (args.length == 2) {
                BanManager manager = plugin.getBanManager();
                try {
                    Reason reason = manager.getReasonTemplate(Integer.parseInt(args[1]));
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
                    if (target != null) {
                        if (reason.getReasonType() == Reason.ReasonType.BAN) {
                            if (manager.isBanned(target.getUniqueId()))
                                manager.unBan(target.getUniqueId());
                            manager.banPlayerByConsole(target, reason);
                        } else {
                            if (manager.isMuted(target.getUniqueId()))
                                manager.unMute(target.getUniqueId());
                            manager.mutePlayerByConsole(target, reason);
                        }
                        int[] time = manager.secondsToArray(reason.getSeconds());
                        for (ProxiedPlayer a : ProxyServer.getInstance().getPlayers()) {
                            if (a.hasPermission("bungee.ban")) {
                                if (reason.getReasonType() == Reason.ReasonType.BAN)
                                    a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + target.getName() + " §7wurde gebannt."));
                                else
                                    a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + target.getName() + " §7wurde gemutet."));
                                a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Grund §8» §b" + reason.getReason()));
                                a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Länge §8» " + manager.getTimeAsString(time)));
                                a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Von §8» §bCONSOLE"));
                            }
                        }
                    } else {
                        UUID tUUID = UUIDFetcher.getUUID(args[0]);
                        if (tUUID == null) {
                            commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[0] + " §7existiert nicht."));
                            return;
                        }
                        if (reason.getReasonType() == Reason.ReasonType.BAN) {
                            if (manager.isBanned(tUUID))
                                manager.unBan(tUUID);
                            manager.banOfflinePlayerByConsole(args[0], reason);
                        } else {
                            if (manager.isMuted(tUUID))
                                manager.unMute(tUUID);
                            manager.muteOfflinePlayerByConsole(args[0], reason);
                        }
                        int[] time = manager.secondsToArray(reason.getSeconds());
                        for (ProxiedPlayer a : ProxyServer.getInstance().getPlayers()) {
                            if (a.hasPermission("bungee.ban")) {
                                if (reason.getReasonType() == Reason.ReasonType.BAN)
                                    a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[0] + " §7wurde gebannt."));
                                else
                                    a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[0] + " §7wurde gemutet."));
                                a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Grund §8» §b" + reason.getReason()));
                                a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Länge §8» " + manager.getTimeAsString(time)));
                                a.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Von §8» §bCONSOLE"));
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    sendHelp(commandSender);
                }
            }else
                sendHelp(commandSender);
        }
    }

    private void sendHelp(CommandSender commandSender) {
        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Bitte benutze: /ban <Spieler> <ID>"));
        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Hier die Verfügbaren ID's"));
        for (Reason reason : plugin.getBanManager().getReasons()) {
            if (!reason.isCanSupBan() || commandSender.hasPermission("bungee.ban.all"))
                if (reason.getReasonType() == Reason.ReasonType.BAN)
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §b" + reason.getId() + " §8» §a" + reason.getReason()));
        }
        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Mutes"));
        for (Reason reason : plugin.getBanManager().getReasons()) {
            if (!reason.isCanSupBan() || commandSender.hasPermission("bungee.ban.all"))
                if (reason.getReasonType() == Reason.ReasonType.MUTE)
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §b" + reason.getId() + " §8» §a" + reason.getReason()));
        }
    }

}
