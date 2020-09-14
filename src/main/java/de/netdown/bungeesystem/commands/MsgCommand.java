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
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Du musst ein Spieler sein, um diesen Befehl nutzen zu können."));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (args.length >= 2) {
            ProxiedPlayer target = plugin.getProxy().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + args[0] + " §7ist nicht online."));
                return;
            }
            String msg = "";
            for (int i = 1; i < args.length; i++)
                msg += " " + args[i];
            target.sendMessage(new TextComponent("§3§lM§fSG §8● §b" + player.getName() + " §8➟ §7Mir §8»" + (player.hasPermission("chat.color") ? msg.replaceAll("&", "§") : msg)));
            target.sendMessage(new TextComponent("§3§lM§fSG §8● §7Du §8➟ §b" + target.getName() + " §8»" + (player.hasPermission("chat.color") ? msg.replaceAll("&", "§") : msg)));
        } else
            player.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Bitte benutze: /msg <Spieler> <Nachricht>"));
    }
}
