package de.netdown.bungeesystem.commands;

import de.netdown.bungeesystem.BungeeSystem;
import de.netdown.bungeesystem.utils.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class MaintenanceCommand extends Command {

    private BungeeSystem plugin;

    public MaintenanceCommand(BungeeSystem plugin) {
        super("maintenance", "", "wartungen", "wartung");
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("bungee.maintenance")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("toggle")) {
                    commandSender.sendMessage(new TextComponent(plugin.getMaintenanceManager().isMaintenance() + ""));
                    if (plugin.getMaintenanceManager().isMaintenance()) {
                        plugin.getMaintenanceManager().setMaintenance(false);
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Die Wartungen sind nun deaktiviert."));
                    } else
                        plugin.getMaintenanceManager().startMaintenanceCountdown();
                } else if (args[0].equalsIgnoreCase("toggledirect")) {
                    if (plugin.getMaintenanceManager().isMaintenance()) {
                        plugin.getMaintenanceManager().setMaintenance(false);
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Die Wartungen sind nun deaktiviert."));
                    } else {
                        plugin.getMaintenanceManager().setMaintenance(true);
                    }
                } else if (args[0].equalsIgnoreCase("status")) {
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Aktuell sind die Wartungen §b" + (plugin.getMaintenanceManager().isMaintenance() ? "aktiviert" : "deaktiviert")));
                } else
                    sendHelp(commandSender);
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("add")) {
                    UUID uuid = UUIDFetcher.getUUID(args[1]);
                    if (uuid != null) {
                        plugin.getMaintenanceManager().addPlayerToWhitelist(uuid);
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Du hast den Spieler §b" + args[1] + " §7zur Whitelist hinzugefügt."));
                    } else
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[1] + " §7existiert nicht."));
                } else if (args[0].equalsIgnoreCase("remove")) {
                    UUID uuid = UUIDFetcher.getUUID(args[1]);
                    if (uuid != null) {
                        ProxiedPlayer player = plugin.getProxy().getPlayer(uuid);
                        if(player != null)
                            player.disconnect(new TextComponent(plugin.getMaintenanceManager().getMaintenanceKick().replaceAll("&", "§")));
                        plugin.getMaintenanceManager().removePlayerFromWhitelist(uuid);
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Du hast den Spieler §b" + args[1] + " §7von der Whitelist entfernt."));
                    } else
                        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[1] + " §7existiert nicht."));
                } else
                    sendHelp(commandSender);
            } else
                sendHelp(commandSender);
        } else
            commandSender.sendMessage(new TextComponent(plugin.getData().getNoPerm()));
    }

    private void sendHelp(CommandSender commandSender) {
        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Bitte benutze"));
        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ /maintenance toggle §8» De/aktiviere die Wartungen"));
        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ /maintenance toggleDirect §8» De/aktiviere die Wartungen direkt"));
        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ /maintenance status §8» Zeigt, ob Wartungen an/aus sind"));
        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ /maintenance add <Player> §8» Fügt einen Spieler zur whitelist hinzu"));
        commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "➥ /maintenance remove <Player> §8» Entfernt einen Spieler aus der Whitelist"));
    }
}
