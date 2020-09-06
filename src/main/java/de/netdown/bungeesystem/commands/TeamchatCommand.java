package de.netdown.bungeesystem.commands;

import de.netdown.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TeamchatCommand extends Command {

    private BungeeSystem plugin;

    public TeamchatCommand(BungeeSystem plugin) {
        super("teamchat", "", "tc");
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Du musst ein Spieler sein, um diese Funktion benutzen zu können."));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (player.hasPermission("bungee.tc")) {
            if (args.length > 0) {
                String msg = "";
                for (int i = 0; i < args.length; i++) {
                    msg += " " + args[i];
                }
                for (ProxiedPlayer all : plugin.getProxy().getPlayers())
                    if (all.hasPermission("bungee.tc"))
                        all.sendMessage(new TextComponent("§8┃ §3§lTeam§fChat §8● §e" + player.getName() + " §8»§7" + msg.replaceAll("&", "§")));
            } else
                player.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Bitte benutze: /tc <Nachricht>"));
        } else
            player.sendMessage(new TextComponent(plugin.getData().getNoPerm()));
    }
}
