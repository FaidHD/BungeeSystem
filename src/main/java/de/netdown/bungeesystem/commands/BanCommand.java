package de.netdown.bungeesystem.commands;

import de.netdown.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

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
                if(args.length == 2) {

                }
            }
        }
    }
}
