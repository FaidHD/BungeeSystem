package de.netdown.bungeesystem.utils;

import de.netdown.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class BanManager {

    private BungeeSystem plugin;

    private File file;
    private Configuration cfg;

    private MySQL mySQL;

    private ArrayList<Reason> reasons;

    public BanManager(BungeeSystem plugin) {
        this.plugin = plugin;
        this.reasons = new ArrayList<>();
        initReasons();
        initMySQL();
    }

    private void initReasons() {
        reasons.add(new Reason("HACKING", 2592000, 1, 3, Reason.ReasonType.BAN, true));
        reasons.add(new Reason("TROLLING", 604800, 2, 1, Reason.ReasonType.BAN, true));
        reasons.add(new Reason("SKIN", 3600, 3, 1, Reason.ReasonType.BAN, true));
        reasons.add(new Reason("BUILDING", 259200, 1, 4, Reason.ReasonType.BAN, true));
        reasons.add(new Reason("NAME", 259200, 1, 5, Reason.ReasonType.BAN, true));
        reasons.add(new Reason("BUGUSING", 604800, 6, 2, Reason.ReasonType.BAN, true));
        reasons.add(new Reason("RECHTSEXTREMISMUS", 0, 7, 10, Reason.ReasonType.BAN, false));
        reasons.add(new Reason("VIRTUELLES HAUSVERBOT", 0, 8, 5, Reason.ReasonType.BAN, false));

        reasons.add(new Reason("BELEIDIGUNG", 3600, 9, 1, Reason.ReasonType.MUTE, true));
        reasons.add(new Reason("WERBUNG", 3600 * 24, 9, 3, Reason.ReasonType.MUTE, true));
    }

    private void initMySQL() {
        file = new File(plugin.getDataFolder(), "mysql.yml");
        try {
            if (!plugin.getDataFolder().exists())
                plugin.getDataFolder().mkdir();
            if (!file.exists()) {
                file.createNewFile();
                cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
                cfg.set("Hostname", "localhost");
                cfg.set("Port", "3306");
                cfg.set("Database", "database");
                cfg.set("Username", "username");
                cfg.set("Password", "password");
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mySQL = new MySQL(cfg.getString("Hostname"), cfg.getString("Port"), cfg.getString("Database"), cfg.getString("Username"), cfg.getString("Password"), plugin);
        mySQL.update("CREATE TABLE IF NOT EXISTS bans(PLAYERNAME VARCHAR(16), UUID VARCHAR(64), IPADDRESS VARCHAR(32), ENDING VARCHAR(64), REASON VARCHAR(128), JUDGE_NAME VARCHAR(16), JUDGE_UUID VARCHAR(64))");
        mySQL.update("CREATE TABLE IF NOT EXISTS mutes(PLAYERNAME VARCHAR(16), UUID VARCHAR(64), IPADDRESS VARCHAR(32), ENDING VARCHAR(64), REASON VARCHAR(128), JUDGE_NAME VARCHAR(16), JUDGE_UUID VARCHAR(64))");
        mySQL.update("CREATE TABLE IF NOT EXISTS history(PLAYERNAME VARCHAR(16), UUID VARCHAR(64), IPADDRESS VARCHAR(32), TYPE VARCHAR(8), ENDING VARCHAR(64), REASON VARCHAR(128), POINTS INT, JUDGE_NAME VARCHAR(16), JUDGE_UUID VARCHAR(64))");
    }

    public Reason getReason(String reasonName) {
        for (Reason reason : reasons) {
            if (reason.getReason() == reasonName.toUpperCase())
                return reason;
        }
        return null;
    }

    public Reason getReason(int id) {
        for (Reason reason : reasons) {
            if (reason.getId() == id)
                return reason;
        }
        return null;
    }

    public boolean isBanned(ProxiedPlayer player) {
        String uuid = player.getUniqueId().toString();
        ResultSet rs = mySQL.query("SELECT * FROM bans WHERE UUID='" + uuid + "'");
        try {
            if (rs.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isBanned(String playerName) {
        ResultSet rs = mySQL.query("SELECT * FROM bans WHERE PLAYERNAME='" + playerName + "'");
        try {
            if (rs.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isBanned(UUID uuid) {
        ResultSet rs = mySQL.query("SELECT * FROM bans WHERE UUID='" + uuid + "'");
        try {
            if (rs.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isBannedIP(String ipAddress) {
        ResultSet rs = mySQL.query("SELECT * FROM bans WHERE IPADDRESS='" + ipAddress + "'");
        try {
            if (rs.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isMuted(ProxiedPlayer player) {
        String uuid = player.getUniqueId().toString();
        ResultSet rs = mySQL.query("SELECT * FROM mutes WHERE UUID='" + uuid + "'");
        try {
            if (rs.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isMuted(String playerName) {
        ResultSet rs = mySQL.query("SELECT * FROM mutes WHERE PLAYERNAME='" + playerName + "'");
        try {
            if (rs.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isMuted(UUID uuid) {
        ResultSet rs = mySQL.query("SELECT * FROM mutes WHERE UUID='" + uuid + "'");
        try {
            if (rs.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void banPlayer(String name, UUID uuid, String ip, int seconds, String reason, String judgeName, String judgeUUID, int banPoints) {
        long end = System.currentTimeMillis() + seconds * 1000;
        mySQL.update("INSERT INTO bans(PLAYERNAME, UUID, IPADDRESS, ENDING, REASON, JUDGE_NAME, JUDGE_UUID) VALUES('" + name + "', '" + uuid + "', '" + ip + "', '" + end + "', '" + reason + "', '" + judgeName + "', '" + judgeUUID + "')");
        mySQL.update("INSERT INTO history(PLAYERNAME, UUID, IPADDRESS, TYPE, ENDING, REASON, POINTS, JUDGE_NAME, JUDGE_UUID) VALUES('" + name + "', '" + uuid + "', '" + ip + "', 'BAN', '" + end + "', '" + reason + "', '" + banPoints + "', '" + judgeName + "', '" + judgeUUID + "')");
    }

    public void banPlayerByPlayer(ProxiedPlayer player, ProxiedPlayer judge, Reason reason) {
        banPlayer(player.getName(), player.getUniqueId(), getIPFromPlayer(player), reason.getSeconds(), reason.getReason(), judge.getName(), judge.getUniqueId().toString(), reason.getPoints());
    }

    public void banOfflinePlayerByPlayer(String playerName, ProxiedPlayer judge, Reason reason) {
        UUID uuid = UUIDFetcher.getUUID(playerName);
        banPlayer(playerName, uuid, null, reason.getSeconds(), reason.getReason(), judge.getName(), judge.getUniqueId().toString(), reason.getPoints());
    }

    public void banPlayerByConsole(ProxiedPlayer player, Reason reason) {
        banPlayer(player.getName(), player.getUniqueId(), getIPFromPlayer(player), reason.getSeconds(), reason.getReason(), "CONSOLE", "CONSOLE", reason.getPoints());
    }

    public void banOfflinePlayerByConsole(String playerName, Reason reason) {
        UUID uuid = UUIDFetcher.getUUID(playerName);
        banPlayer(playerName, uuid, null, reason.getSeconds(), reason.getReason(), "CONSOLE", "CONSOLE", reason.getPoints());
    }

    public void unBan(String uuid) {
        mySQL.update("DELETE FROM mutes WHERE UUID='" + uuid + "'");
    }

    private void mutePlayer(String name, UUID uuid, String ip, int seconds, String reason, String judgeName, UUID judgeUUID, int mutePoints) {
        long end = System.currentTimeMillis() + seconds * 1000;
        mySQL.update("INSERT INTO mutes(PLAYERNAME, UUID, IPADDRESS, ENDING, REASON, JUDGE_NAME, JUDGE_UUID) VALUES('" + name + "', '" + uuid + "', '" + ip + "', '" + end + "', '" + reason + "', '" + judgeName + "', '" + judgeUUID.toString() + "')");
        mySQL.update("INSERT INTO history(PLAYERNAME, UUID, IPADDRESS, TYPE, ENDING, REASON, POINTS, JUDGE_NAME, JUDGE_UUID) VALUES('" + name + "', '" + uuid + "', '" + ip + "', 'BAN', '" + end + "', '" + reason + "', '" + mutePoints + "', '" + judgeName + "', '" + judgeUUID.toString() + "')");
    }

    public void mutePlayerByPlayer(ProxiedPlayer player, int seconds, String reason, ProxiedPlayer judge, int points) {
        mutePlayer(player.getName(), player.getUniqueId(), getIPFromPlayer(player), seconds, reason, judge.getName(), judge.getUniqueId(), points);
    }

    public void muteOfflinePlayerByPlayer(String playerName, int seconds, String reason, ProxiedPlayer judge, int points) {
        UUID uuid = UUIDFetcher.getUUID(playerName);
        mutePlayer(playerName, uuid, null, seconds, reason, judge.getName(), judge.getUniqueId(), points);
    }

    public void mutePlayerByConsole(ProxiedPlayer player, int seconds, String reason, ProxiedPlayer judge, int points) {
        mutePlayer(player.getName(), player.getUniqueId(), getIPFromPlayer(player), seconds, reason, judge.getName(), judge.getUniqueId(), points);
    }

    public void muteOfflinePlayerByConsole(String playerName, int seconds, String reason, ProxiedPlayer judge, int points) {
        UUID uuid = UUIDFetcher.getUUID(playerName);
        mutePlayer(playerName, uuid, null, seconds, reason, judge.getName(), judge.getUniqueId(), points);
    }

    public void unMute(String uuid) {
        mySQL.update("DELETE FROM mutes WHERE UUID='" + uuid + "'");
    }

    public String getIPFromPlayer(ProxiedPlayer player) {
        return player.getSocketAddress().toString().split(":")[0].replaceAll("/", "");
    }

}
