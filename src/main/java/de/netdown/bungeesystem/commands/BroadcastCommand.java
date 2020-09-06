package de.netdown.bungeesystem.commands;

import de.netdown.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class BroadcastCommand extends Command {

    private BungeeSystem plugin;

    public BroadcastCommand(BungeeSystem plugin) {
        super("broadcast", "", "bc");
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("bungee.broadcast")) {
            if (args.length > 0) {
                String msg = "";
                for (int i = 0; i < args.length; i++) {

                    msg += " " + args[i];
                }
                plugin.getProxy().broadcast(new TextComponent(" "));
                plugin.getProxy().broadcast(new TextComponent("§8┃ §3§lBroad§fCast §8●§7" + msg.replaceAll("&", "§")));
                plugin.getProxy().broadcast(new TextComponent(" "));
            } else
                commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Bitte benutze: /bc <Nachricht>"));
        } else
            commandSender.sendMessage(new TextComponent(plugin.getData().getNoPerm()));
    }
}
