package de.netdown.bungeesystem.commands;

import de.netdown.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class MsgCommand extends Command {

    private BungeeSystem plugin;

    public MsgCommand(BungeeSystem plugin) {
        super("msg");
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        //TODO Add msg command
    }
}
