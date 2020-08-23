package de.netdown.bungeesystem;

public class Data {

    private BungeeSystem plugin;

    private String prefix = "§8┃ §aBungee §8● §7";
    private String noPerm = prefix + "Dazu hast du keine Rechte.";


    public Data(BungeeSystem plugin) {
        this.plugin = plugin;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getNoPerm() {
        return noPerm;
    }
}
