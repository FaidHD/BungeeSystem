package de.netdown.bungeesystem.commands;

import de.netdown.bungeesystem.BungeeSystem;
import de.netdown.bungeesystem.utils.Reason;
import de.netdown.bungeesystem.utils.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class TempmuteCommand extends Command {

    private BungeeSystem plugin;

    public TempmuteCommand(BungeeSystem plugin) {
        super("tempmute");
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    //tempmute

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("bungee.ban") || commandSender.hasPermission("bungee.ban.all")) {
            if (args.length == 5) {
                if (args[2].equalsIgnoreCase("w") || args[2].equalsIgnoreCase("d") || args[2].equalsIgnoreCase("h") || args[2].equalsIgnoreCase("m") || args[2].equalsIgnoreCase("s")) {
                    try {
                        int time = getTimeAsSeconds(Integer.parseInt(args[1]), args[2]);
                        if (time > 2592000 && time != 0)
                            if (!commandSender.hasPermission("bungee.ban.all")) {
                                commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Du darfst nicht mehr als 30 Tage muten."));
                                return;
                            }
                        int points = Integer.parseInt(args[3]);
                        if (points > 3)
                            if (!commandSender.hasPermission("bungee.ban.all")) {
                                commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Du darfst nicht mehr als 3 Mutepunkte vergeben."));
                                return;
                            }
                        UUID uuid = UUIDFetcher.getUUID(args[0]);
                        if (uuid == null) {
                            commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[0] + " §7existiert nicht."));
                            return;
                        }
                        Reason reason = new Reason(args[4].toUpperCase(), time, 0, points, Reason.ReasonType.MUTE, false);
                        removeBan(reason, uuid);
                        ProxiedPlayer player = plugin.getProxy().getPlayer(uuid);
                        if (player == null) {
                            if (commandSender instanceof ProxiedPlayer)
                                plugin.getBanManager().muteOfflinePlayerByPlayer(args[0], (ProxiedPlayer) commandSender, reason);
                            else
                                plugin.getBanManager().muteOfflinePlayerByConsole(args[0], reason);
                        } else {
                            if (commandSender instanceof ProxiedPlayer)
                                plugin.getBanManager().mutePlayerByPlayer(player, (ProxiedPlayer) commandSender, reason);
                            else
                                plugin.getBanManager().mutePlayerByConsole(player, reason);
                        }
                        for (ProxiedPlayer all : plugin.getProxy().getPlayers())
                            if (all.hasPermission("bungee.ban") || all.hasPermission("bungee.ban.all")) {
                                all.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[0] + " §7wurde gemutet."));
                                all.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ Grund §b" + reason.getReason()));
                                all.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ Länge §b" + plugin.getBanManager().getTimeAsString(plugin.getBanManager().secondsToArray(reason.getSeconds()))));
                            }
                    } catch (NumberFormatException e) {
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Bitte benutze: /tempmute <Spieler> <Zeit> <s, m, h, d, w> <Punkte> <Grund>"));
                    }
                } else
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Bitte benutze: /tempmute <Spieler> <Zeit> <s, m, h, d, w> <Punkte> <Grund>"));
            } else
                commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Bitte benutze: /tempmute <Spieler> <Zeit> <s, m, h, d, w> <Punkte> <Grund>"));
        } else
            commandSender.sendMessage(new TextComponent(plugin.getData().getNoPerm()));
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

    private int getTimeAsSeconds(int time, String format) {
        switch (format) {
            case "w":
                return time * 7 * 24 * 60 * 60;
            case "d":
                return time * 24 * 60 * 60;
            case "h":
                return time * 60 * 60;
            case "m":
                return time * 60;
            case "s":
                return time;
        }
        return 0;
    }
}
