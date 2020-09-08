package de.netdown.bungeesystem.commands;

import de.netdown.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MsgCommand extends Command {

    private BungeeSystem plugin;

    public MsgCommand(BungeeSystem plugin) {
        super("msg", "", "tell");
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Du musst ein Spieler sein, um diesen Befehl nutzen zu können."));
            return;
        }
        
    }
}
