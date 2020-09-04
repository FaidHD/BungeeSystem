package de.netdown.bungeesystem;

public class Data {

    private BungeeSystem plugin;

    private String prefix = "§8┃ §3§lNet§fDown §8● §7";
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
