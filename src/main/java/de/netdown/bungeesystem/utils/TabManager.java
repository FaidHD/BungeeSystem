package de.netdown.bungeesystem.utils;

import de.netdown.bungeesystem.BungeeSystem;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class TabManager {

    private BungeeSystem plugin;

    private File file;
    private Configuration cfg;

    private String header;
    private String footer;

    private boolean useTab;

    public TabManager(BungeeSystem plugin) {
        this.plugin = plugin;
        initConfig();
    }

    private void initConfig() {
        this.file = new File(plugin.getDataFolder(), "tablist.yml");
        try {
            if (!plugin.getDataFolder().exists())
                plugin.getDataFolder().mkdir();
            if (!file.exists())
                file.createNewFile();
            cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            if(!cfg.contains("Header")) {
                useTab = true;
                header = "\n&3&lNet§fDown.de &8● &7Netzwerk\n&7Dein &bCityBuild und &bFreebuild &7Server\n&7Aktueller Server &8➥ §b%server%";
                footer = "\nAktuell Online &8➥ &b%online%\n&7Teamspeak &8➥ &3Net&fDown.de\n";
                cfg.set("UseTab", true);
                cfg.set("Header", header);
                cfg.set("Footer", footer);
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
            }
            useTab = cfg.getBoolean("UseTab");
            header = cfg.getString("Header");
            footer = cfg.getString("Footer");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getHeader() {
        return header;
    }

    public String getFooter() {
        return footer;
    }

    public boolean isUseTab() {
        return useTab;
    }
}
