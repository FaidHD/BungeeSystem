package de.netdown.bungeesystem.commands;

import de.netdown.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class KickCommand extends Command {

    private BungeeSystem plugin;

    public KickCommand(BungeeSystem plugin) {
        super("kick");
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("bungee.kick")) {
            if (args.length == 2) {
                ProxiedPlayer player = plugin.getProxy().getPlayer(args[0]);
                if (player != null) {
                    player.disconnect(new TextComponent("§3Net§fDown §8● §7Netzwerk\n§cDu wurdest gekickt\n\n§8➥ §7Grund §8» §9" + args[1].replaceAll("&", "§") + "\n\n§7Teamspeak §8» §3Net§fDown.de"));
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Du hast den Spieler §b" + player.getName() + " für den Grund §b" + args[1].replaceAll("&", "§") + " §7gekickt."));
                } else
                    commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[0] + " §7ist nicht online."));
            } else
                commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Bitte benutze: /kick <Spieler> <Grund>"));
        } else
            commandSender.sendMessage(new TextComponent(plugin.getData().getNoPerm()));
    }
}
