package de.netdown.bungeesystem.utils;

import de.netdown.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MaintenanceManager {

    private BungeeSystem plugin;

    private boolean maintenance;
    private boolean motdSupport;
    private boolean protocolSupport;
    private boolean playerListSupport;

    private String motdLine1;
    private String motdLine2;
    private String maintenanceLine1;
    private String maintenanceLine2;
    private String maintenanceKick;
    private String protocolLine;
    private ArrayList<String> playerList;

    private ArrayList<String> whitelist;

    private File file;
    private Configuration cfg;

    public MaintenanceManager(BungeeSystem plugin) {
        this.plugin = plugin;
        initConfig();
    }

    private void initConfig() {
        file = new File(plugin.getDataFolder(), "maintenance.yml");
        playerList = new ArrayList<>();
        whitelist = new ArrayList<>();
        try {
            if (!plugin.getDataFolder().exists())
                plugin.getDataFolder().mkdir();
            if (!file.exists())
                file.createNewFile();
            cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            if (!cfg.contains("Maintenance")) {
                cfg.set("Maintenance", false);
                cfg.set("Motd", true);
                cfg.set("MaintenanceProtocol", true);
                cfg.set("MaintenanceProtocolHover", true);
                cfg.set("MOTDLine.1", "&3&lNet&fDown.de &8● &7Dein &bCityBuild und Freebuild &7Netzwerk §f[§b1.13.2§f]");
                cfg.set("MOTDLine.2", " &8➥ &bTeammitglieder gesucht!");
                cfg.set("MaintenanceLine.1", "&3&lNet&fDown.de &8● &7Dein &bCityBuild und Freebuild &7Netzwerk §f[§b1.13.2§f]");
                cfg.set("MaintenanceLine.2", " &8➥ &cWartungsarbeiten &8» &7Zu &b72% &7Fertig");
                cfg.set("MaintenanceKick", "&3&lNet&fDown.de\n&7Entschuldigung, aber wir führen derzeit &cWartungsarbeiten &7durch. \n\nBei Problemen melde dich gerne auf unserem Teamspeak &8» &3&lNet&fDown.de");
                cfg.set("ProtocolLine", "&8☕ &cWartungsarbeiten");
                playerList.add(" ");
                playerList.add("&8➥ &7Teamspeak &8» &3&lNet&fDown.de");
                playerList.add("&8➥ &7Fortschritt &8» &b72%");
                playerList.add(" ");
                cfg.set("ProtocolPlayerList", playerList);
                whitelist.add(UUIDFetcher.getUUID("IchWurdeGeboren").toString());
                cfg.set("Whitelist", whitelist);
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
            }
            maintenance = cfg.getBoolean("Maintenance");
            motdSupport = cfg.getBoolean("Motd");
            protocolSupport = cfg.getBoolean("MaintenanceProtocol");
            playerListSupport = cfg.getBoolean("MaintenanceProtocolHover");
            motdLine1 = cfg.getString("MOTDLine.1");
            motdLine2 = cfg.getString("MOTDLine.2");
            maintenanceLine1 = cfg.getString("MaintenanceLine.1");
            maintenanceLine2 = cfg.getString("MaintenanceLine.2");
            maintenanceKick = cfg.getString("MaintenanceKick");
            protocolLine = cfg.getString("ProtocolLine");
            playerList = (ArrayList<String>) cfg.getStringList("ProtocolPlayerList");
            whitelist = (ArrayList<String>) cfg.getStringList("Whitelist");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ScheduledTask task;

    public void startMaintenanceCountdown() {
        task = plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
            int i = 60;

            @Override
            public void run() {
                switch (i) {
                    case 60:
                    case 30:
                    case 15:
                    case 10:
                    case 5:
                    case 4:
                    case 3:
                    case 2:
                        plugin.getProxy().broadcast(new TextComponent(plugin.getData().getPrefix() + "Die Wartungsarbeiten werden in §b" + i + " Sekunden §7aktiviert."));
                        break;
                    case 1:
                        plugin.getProxy().broadcast(new TextComponent(plugin.getData().getPrefix() + "Die Wartungsarbeiten werden in §beiner Sekunde §7aktiviert."));
                        break;
                    case 0:
                        setMaintenance(true);
                        ProxyServer.getInstance().getScheduler().cancel(task);
                        return;
                }
                i--;
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void addPlayerToWhitelist(UUID uuid) {
        if (!whitelist.contains(uuid.toString()))
            whitelist.add(uuid.toString());
        saveWhitelist();
    }

    public void removePlayerFromWhitelist(UUID uuid) {
        if (whitelist.contains(uuid.toString()))
            whitelist.remove(uuid.toString());
        saveWhitelist();
    }

    private void saveWhitelist() {
        cfg.set("Whitelist", whitelist);
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerPing.PlayerInfo[] getPlayerInfo() {
        ServerPing.PlayerInfo[] playerInfos = new ServerPing.PlayerInfo[playerList.size()];
        for (int i = 0; i < playerList.size(); i++)
            playerInfos[i] = new ServerPing.PlayerInfo(playerList.get(i).replaceAll("&", "§"), UUID.randomUUID());
        return playerInfos;
    }

    public void setMaintenance(boolean maintenance) {
        if (maintenance) {
            plugin.getProxy().broadcast(new TextComponent(plugin.getData().getPrefix() + "Die Wartungsarbeiten werden §bjetzt §7aktiviert."));
            for (ProxiedPlayer all : plugin.getProxy().getPlayers())
                if (!all.hasPermission("bungee.maintenance") && !all.hasPermission("bungee.maintenance.join"))
                    all.disconnect(new TextComponent(getMaintenanceKick().replaceAll("&", "§")));
        }
        cfg.set("Maintenance", maintenance);
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.maintenance = maintenance;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public boolean isMotdSupport() {
        return motdSupport;
    }

    public boolean isProtocolSupport() {
        return protocolSupport;
    }

    public boolean isPlayerListSupport() {
        return playerListSupport;
    }

    public String getMotdLine1() {
        return motdLine1;
    }

    public String getMotdLine2() {
        return motdLine2;
    }

    public String getMaintenanceLine1() {
        return maintenanceLine1;
    }

    public String getMaintenanceLine2() {
        return maintenanceLine2;
    }

    public String getMaintenanceKick() {
        return maintenanceKick;
    }

    public String getProtocolLine() {
        return protocolLine;
    }

    public ArrayList<String> getPlayerList() {
        return playerList;
    }

    public ArrayList<String> getWhitelist() {
        return whitelist;
    }
}
