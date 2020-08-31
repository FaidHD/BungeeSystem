package de.netdown.bungeesystem.commands;

import de.netdown.bungeesystem.BungeeSystem;
import de.netdown.bungeesystem.utils.BanManager;
import de.netdown.bungeesystem.utils.Reason;
import de.netdown.bungeesystem.utils.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class EACommand extends Command {

    private BungeeSystem plugin;

    public EACommand(BungeeSystem plugin) {
        super("ea");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("bungee.ea")) {
            if (args.length == 1) {
                UUID uuid = UUIDFetcher.getUUID(args[0]);
                if (uuid != null) {
                    BanManager manager = plugin.getBanManager();
                    if (manager.isBanned(uuid)) {
                        Reason reason = manager.getReasonTemplate(manager.getBanReasonFromDB(uuid));
                        if (reason.getReasonType() == Reason.ReasonType.BAN) {
                            manager.unBan(uuid);
                            manager.banOfflinePlayerByConsole(args[0], new Reason("BANVERKÜRZUNG (" + reason.getReason() + ")", reason.getHalfTime(), 0, 0, Reason.ReasonType.BAN, false));
                        } else {
                            manager.unMute(uuid);
                            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uuid);
                            if (target == null)
                                manager.muteOfflinePlayerByConsole(args[0], new Reason("MUTEVERKÜRZUNG (" + reason.getReason() + ")", reason.getHalfTime(), 0, 0, Reason.ReasonType.MUTE, false));
                            else {
                                manager.mutePlayerByConsole(target, new Reason("MUTEVERKÜRZUNG (" + reason.getReason() + ")", reason.getHalfTime(), 0, 0, Reason.ReasonType.MUTE, false));
                                target.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Dein Mute wurde verkürzt. Restliche Dauer §8» " + manager.getTimeAsString(manager.secondsToArray(reason.getHalfTime()))));
                            }
                        }
                    }
                }
            }
        }
    }
}
