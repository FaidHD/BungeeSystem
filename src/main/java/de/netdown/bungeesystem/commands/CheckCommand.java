package de.netdown.bungeesystem.commands;

import de.netdown.bungeesystem.BungeeSystem;
import de.netdown.bungeesystem.utils.BanManager;
import de.netdown.bungeesystem.utils.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class CheckCommand extends Command {

    private BungeeSystem plugin;

    public CheckCommand(BungeeSystem plugin) {
        super("check");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("bungee.check")) {
            if (args.length == 1) {
                UUID uuid = UUIDFetcher.getUUID(args[0]);
                if (uuid != null) {
                    BanManager manager = plugin.getBanManager();
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Hier die Informationen über §b" + args[0]));
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Banpunkte §8» §b" + manager.getBanPoints(uuid)));
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Mutepunkte §8» §b" + manager.getMutePoints(uuid)));
                    if (manager.isBanned(uuid))
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Aktuell gebannt §8» §b" + manager.getBanReason(uuid) + " §8➟ " + manager.getTimeAsString(manager.getBanTime(uuid))));
                    else
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Aktuell gebannt §8» §bNein"));
                    if (manager.isMuted(uuid))
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Aktuell gemutet §8» §b" + manager.getMuteReason(uuid) + " §8➟ " + manager.getTimeAsString(manager.getMuteTime(uuid))));
                    else
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §7Aktuell gemutet §8» §bNein"));
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + ""));
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Bans von §b" + args[0]));
                    if (manager.getBanHistoryFromPlayer(uuid) == null)
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §cKeine Bans in der Datenbank gefunden."));
                    else
                        for (String[] bans : manager.getBanHistoryFromPlayer(uuid))
                            commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §b" + bans[0] + " §8┃ §7Gebannt von §b" + bans[2] + " §8┃ §b" + bans[1] + " §7Banpunkte"));
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + ""));
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Mutes von §b" + args[0]));
                    if (manager.getMuteHistoryFromPlayer(uuid) == null)
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §cKeine Mutes in der Datenbank gefunden."));
                    else
                        for (String[] bans : manager.getMuteHistoryFromPlayer(uuid))
                            commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ §b" + bans[0] + " §8┃ §7Gemutet von §b" + bans[2] + " §8┃ §b" + bans[1] + " §7Mutepunkte"));
                } else
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler existiert nicht."));
            } else
                commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Bitte benutze: /check <Spieler>"));
        } else
            commandSender.sendMessage(plugin.getData().getNoPerm());
    }
}
