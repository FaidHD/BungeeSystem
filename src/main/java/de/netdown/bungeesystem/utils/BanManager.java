package de.netdown.bungeesystem.utils;

import de.netdown.bungeesystem.BungeeSystem;
import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
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

    private Reason toManyBanPoints;
    private Reason toManyMutePoints;

    public BanManager(BungeeSystem plugin) {
        this.plugin = plugin;
        this.reasons = new ArrayList<>();
        toManyBanPoints = new Reason("ZU VIELE BANS", 0, 0, 0, Reason.ReasonType.BAN, false);
        toManyMutePoints = new Reason("ZU VIELE MUTES", 0, 0, 0, Reason.ReasonType.MUTE, false);
        setupMySQL();
        initReasons();
    }

    private void initReasons() {
        reasons.add(new Reason("HACKING", 2592000, 1, 3, Reason.ReasonType.BAN, true));
        reasons.add(new Reason("TROLLING", 604800, 2, 1, Reason.ReasonType.BAN, true));
        reasons.add(new Reason("SKIN", 3600, 3, 1, Reason.ReasonType.BAN, true));
        reasons.add(new Reason("BUILDING", 259200, 4, 2, Reason.ReasonType.BAN, true));
        reasons.add(new Reason("NAME", 259200, 5, 2, Reason.ReasonType.BAN, true));
        reasons.add(new Reason("BUGUSING", 604800, 6, 3, Reason.ReasonType.BAN, true));
        reasons.add(new Reason("RECHTSEXTREMISMUS", 0, 7, 10, Reason.ReasonType.BAN, false));
        reasons.add(new Reason("VIRTUELLES HAUSVERBOT", 0, 8, 5, Reason.ReasonType.BAN, false));

        reasons.add(new Reason("BELEIDIGUNG", 3600, 9, 2, Reason.ReasonType.MUTE, true));
        reasons.add(new Reason("WERBUNG", 3600 * 24, 10, 3, Reason.ReasonType.MUTE, true));
    }

    private void setupMySQL() {
        file = new File(plugin.getDataFolder(), "mysql.yml");
        try {
            if (!plugin.getDataFolder().exists())
                plugin.getDataFolder().mkdir();
            if (!file.exists())
                file.createNewFile();
            cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            if (!cfg.contains("Hostname")) {
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

    public Reason getReasonTemplate(int id) {
        for (Reason reason : reasons) {
            if (reason.getId() == id)
                return reason;
        }
        return null;
    }

    public String getBanReasonFromDB(UUID uuid) {
        ResultSet rs = mySQL.query("SELECT * FROM bans WHERE UUID='" + uuid.toString() + "'");
        try {
            if (rs.next())
                return rs.getString("REASON");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int[] getBanTime(UUID uuid) {
        long end = getRemainingBanTime(uuid);
        if (end == 0)
            return null;
        long current = System.currentTimeMillis();
        long millis = end - current;
        int seconds = 0;
        while (millis > 1000L) {
            millis -= 1000L;
            seconds++;
        }
        return secondsToArray(seconds);
    }

    public int[] getMuteTime(UUID uuid) {
        long current = System.currentTimeMillis();
        long end = getRemainingMuteTime(uuid);
        if (end == 0)
            return null;
        long millis = end - current;
        int seconds = 0;
        while (millis > 1000L) {
            millis -= 1000L;
            seconds++;
        }
        return secondsToArray(seconds);
    }

    public String getTimeAsString(int[] time) {
        if (time == null)
            return "§bPERMANENT";
        return "§b" + (time[0] == 1 ? (time[0] + " §7Woche §b") : (time[0] + " §7Wochen §b")) + (time[1] == 1 ? (time[1] + " §7Tag §b") : (time[1] + " §7Tage §b")) + (time[2] == 1 ? (time[2] + " §7Stunde §b") : (time[2] + " §7Stunden §b")) + (time[3] == 1 ? (time[3] + " §7Minute §b") : (time[3] + " §7Minuten §b")) + (time[4] == 1 ? (time[4] + " §7Sekunde §b") : (time[4] + " §7Sekunden §b"));
    }

    public int getBanCount(UUID uuid) {
        ResultSet rs = mySQL.query("SELECT * FROM history WHERE UUID='" + uuid + "'");
        int bans = 0;
        while (true) {
            try {
                if (!rs.next()) break;
                if (rs.getString("TYPE").matches("BAN"))
                    bans++;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return bans;
    }

    public int getMuteCount(UUID uuid) {
        ResultSet rs = mySQL.query("SELECT * FROM history WHERE UUID='" + uuid + "'");
        int bans = 0;
        while (true) {
            try {
                if (!rs.next()) break;
                if (rs.getString("TYPE").matches("MUTE"))
                    bans++;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return bans;
    }

    public String[][] getBanHistoryFromPlayer(UUID uuid) {
        ResultSet rs = mySQL.query("SELECT * FROM history WHERE UUID='" + uuid.toString() + "'");
        int banCount = getBanCount(uuid);
        if (banCount == 0)
            return null;
        String[][] bans = new String[banCount][];
        int i = 0;
        while (true) {
            try {
                if (!rs.next()) break;
                if (rs.getString("TYPE").matches("BAN")) {
                    String[] ban = new String[3];
                    ban[0] = rs.getString("REASON");
                    ban[1] = rs.getString("POINTS");
                    ban[2] = rs.getString("JUDGE_NAME");
                    bans[i] = ban;
                    i++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return bans;
    }

    public String[][] getMuteHistoryFromPlayer(UUID uuid) {
        ResultSet rs = mySQL.query("SELECT * FROM history WHERE UUID='" + uuid.toString() + "'");
        int banCount = getMuteCount(uuid);
        if (banCount == 0)
            return null;
        String[][] bans = new String[banCount][];
        int i = 0;
        while (true) {
            try {
                if (!rs.next()) break;
                if (rs.getString("TYPE").matches("MUTE")) {
                    String[] ban = new String[3];
                    ban[0] = rs.getString("REASON");
                    ban[1] = rs.getString("POINTS");
                    ban[2] = rs.getString("JUDGE_NAME");
                    bans[i] = ban;
                    i++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return bans;
    }

    /**
     * @param seconds the seconds witch are translated to weeks, days, hours and minutes
     * @return first are weeks than days than hours etc.
     */
    public int[] secondsToArray(int seconds) {
        if (seconds == 0)
            return null;
        int[] time = new int[5];
        int sec = seconds;
        int minutes = 0;
        int hours = 0;
        int days = 0;
        int weeks = 0;

        while (sec >= 60) {
            sec -= 60;
            minutes++;
        }
        while (minutes >= 60) {
            minutes -= 60;
            hours++;
        }
        while (hours >= 24) {
            hours -= 24;
            days++;
        }
        while (days >= 7) {
            days -= 7;
            weeks++;
        }


        time[0] = weeks;
        time[1] = days;
        time[2] = hours;
        time[3] = minutes;
        time[4] = sec;
        return time;
    }

    public boolean isBanned(UUID uuid) {
        ResultSet rs = mySQL.query("SELECT * FROM bans WHERE UUID='" + uuid + "'");
        try {
            if (rs.next()) {
                long remainingTime = getRemainingBanTime(uuid);
                if (remainingTime <= System.currentTimeMillis()) {
                    if (remainingTime != 0) {
                        unBan(uuid);
                        for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers())
                            if (all.hasPermission("bungee.ban"))
                                all.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + rs.getString("PLAYERNAME") + " §7wurde automatisch wegen Ablauf des Banns entbannt."));
                        return false;
                    }
                }
                return true;
            }
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

    public boolean isMuted(UUID uuid) {
        ResultSet rs = mySQL.query("SELECT * FROM mutes WHERE UUID='" + uuid + "'");
        try {
            if (rs.next()) {
                long remainingTime = getRemainingMuteTime(uuid);
                if (remainingTime <= System.currentTimeMillis()) {
                    if (remainingTime != 0) {
                        unMute(uuid);
                        for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers())
                            if (all.hasPermission("bungee.ban"))
                                all.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Der Spieler §b" + rs.getString("PLAYERNAME") + " §7wurde automatisch wegen Ablauf des Mutes entmutet."));
                        return false;
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public long getRemainingBanTime(UUID uuid) {
        ResultSet rs = mySQL.query("SELECT * FROM bans WHERE UUID='" + uuid.toString() + "'");
        try {
            if (rs.next())
                return Long.parseLong(rs.getString("ENDING"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long getRemainingMuteTime(UUID uuid) {
        ResultSet rs = mySQL.query("SELECT * FROM mutes WHERE UUID='" + uuid.toString() + "'");
        try {
            if (rs.next())
                return Long.parseLong(rs.getString("ENDING"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getBanPoints(UUID uuid) {
        ResultSet rs = mySQL.query("SELECT * FROM history WHERE UUID='" + uuid.toString() + "'");
        int i = 0;
        while (true) {
            try {
                if (!rs.next()) break;
                if (rs.getString("TYPE").matches("BAN"))
                    i += Integer.parseInt(rs.getString("POINTS"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    public int getMutePoints(UUID uuid) {
        ResultSet rs = mySQL.query("SELECT * FROM history WHERE UUID='" + uuid.toString() + "'");
        int i = 0;
        while (true) {
            try {
                if (!rs.next()) break;
                if (rs.getString("TYPE").matches("MUTE"))
                    i += Integer.parseInt(rs.getString("POINTS"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    public String getBanReason(UUID uuid) {
        ResultSet rs = mySQL.query("SELECT * FROM bans WHERE UUID='" + uuid.toString() + "'");
        try {
            if (rs.next())
                return rs.getString("REASON");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMuteReason(UUID uuid) {
        ResultSet rs = mySQL.query("SELECT * FROM mutes WHERE UUID='" + uuid.toString() + "'");
        try {
            if (rs.next())
                return rs.getString("REASON");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void checkBanPoints(UUID uuid) {
        if (getBanPoints(uuid) < 10) return;
        if (getBanReason(uuid).matches("ZU VIELE BANS")) return;
        unBan(uuid);
        banOfflinePlayerByConsole(UUIDFetcher.getName(uuid), toManyBanPoints);
    }

    public void checkMutePoints(UUID uuid) {
        if (getMutePoints(uuid) < 10) return;
        if (getMuteReason(uuid).matches("ZU VIELE MUTES")) return;
        unMute(uuid);
        if (ProxyServer.getInstance().getPlayer(uuid) == null)
            muteOfflinePlayerByConsole(UUIDFetcher.getName(uuid), toManyMutePoints);
        else
            mutePlayerByConsole(ProxyServer.getInstance().getPlayer(uuid), toManyMutePoints);
    }

    private void banPlayer(String name, UUID uuid, String ip, int seconds, String reason, String judgeName, String judgeUUID, int banPoints) {
        long sec = seconds;
        long end = System.currentTimeMillis() + sec * 1000;
        if (seconds == 0)
            end = 0;
        mySQL.update("INSERT INTO bans(PLAYERNAME, UUID, IPADDRESS, ENDING, REASON, JUDGE_NAME, JUDGE_UUID) VALUES('" + name + "', '" + uuid + "', '" + ip + "', '" + end + "', '" + reason + "', '" + judgeName + "', '" + judgeUUID + "')");
        mySQL.update("INSERT INTO history(PLAYERNAME, UUID, IPADDRESS, TYPE, ENDING, REASON, POINTS, JUDGE_NAME, JUDGE_UUID) VALUES('" + name + "', '" + uuid + "', '" + ip + "', 'BAN', '" + end + "', '" + reason + "', '" + banPoints + "', '" + judgeName + "', '" + judgeUUID + "')");
        if (!reason.matches("RECHTSEXTREMISMUS") && !reason.matches("VIRTUELLES HAUSVERBOT"))
            checkBanPoints(uuid);
    }

    public void banPlayerByPlayer(ProxiedPlayer player, ProxiedPlayer judge, Reason reason) {
        banPlayer(player.getName(), player.getUniqueId(), getIPFromPlayer(player), reason.getSeconds(), reason.getReason(), judge.getName(), judge.getUniqueId().toString(), reason.getPoints());
        int[] time = secondsToArray(reason.getSeconds());
        if (time != null)
            player.disconnect(new TextComponent("§3Net§fDown §8● §cDu wurdest gebannt\n\n§8➥ §7Grund §8» §9" + reason.getReason() + "\n §8➥ §7Länge §8» §b" + (time[0] == 1 ? (time[0] + " §7Woche §b") : (time[0] + " §7Wochen §b")) + (time[1] == 1 ? (time[1] + " §7Tag §b") : (time[1] + " §7Tage §b")) + (time[2] == 1 ? (time[2] + " §7Stunde §b") : (time[2] + " §7Stunden §b")) + (time[3] == 1 ? (time[3] + " §7Minute §b") : (time[3] + " §7Minuten §b")) + (time[4] == 1 ? (time[4] + " §7Sekunde §b") : (time[4] + " §7Sekunden §b")) + "\n\n §7Zu unrecht gebannt? Auf unserem Teamspeak kannst du einen §bEntbannungsantrag stellen§7.\n\n§7Teamspeak §8» §3Net§fDown.de"));
        else
            player.disconnect(new TextComponent("§3Net§fDown §8● §cDu wurdest gebannt\n\n§8➥ §7Grund §8» §9" + reason.getReason() + "\n §8➥ §7Länge §8» §bPERMANENT\n\n §7Zu unrecht gebannt? Auf unserem Teamspeak kannst du einen §bEntbannungsantrag stellen§7.\n\n§7Teamspeak §8» §3Net§fDown.de"));
    }

    public void banOfflinePlayerByPlayer(String playerName, ProxiedPlayer judge, Reason reason) {
        UUID uuid = UUIDFetcher.getUUID(playerName);
        banPlayer(playerName, uuid, null, reason.getSeconds(), reason.getReason(), judge.getName(), judge.getUniqueId().toString(), reason.getPoints());
    }

    public void banPlayerByConsole(ProxiedPlayer player, Reason reason) {
        banPlayer(player.getName(), player.getUniqueId(), getIPFromPlayer(player), reason.getSeconds(), reason.getReason(), "CONSOLE", "CONSOLE", reason.getPoints());
        int[] time = secondsToArray(reason.getSeconds());
        if (time != null)
            player.disconnect(new TextComponent("§3Net§fDown §8● §cDu wurdest gebannt\n\n§8➥ §7Grund §8» §9" + reason.getReason() + "\n §8➥ §7Länge §8» §b" + (time[0] == 1 ? (time[0] + " §7Woche §b") : (time[0] + " §7Wochen §b")) + (time[1] == 1 ? (time[1] + " §7Tag §b") : (time[1] + " §7Tage §b")) + (time[2] == 1 ? (time[2] + " §7Stunde §b") : (time[2] + " §7Stunden §b")) + (time[3] == 1 ? (time[3] + " §7Minute §b") : (time[3] + " §7Minuten §b")) + (time[4] == 1 ? (time[4] + " §7Sekunde §b") : (time[4] + " §7Sekunden §b")) + "\n\n §7Zu unrecht gebannt? Auf unserem Teamspeak kannst du einen §bEntbannungsantrag stellen§7.\n\n§7Teamspeak §8» §3Net§fDown.de"));
        else
            player.disconnect(new TextComponent("§3Net§fDown §8● §cDu wurdest gebannt\n\n§8➥ §7Grund §8» §9" + reason.getReason() + "\n §8➥ §7Länge §8» §bPERMANENT\n\n §7Zu unrecht gebannt? Auf unserem Teamspeak kannst du einen §bEntbannungsantrag stellen§7.\n\n§7Teamspeak §8» §3Net§fDown.de"));
    }

    public void banOfflinePlayerByConsole(String playerName, Reason reason) {
        UUID uuid = UUIDFetcher.getUUID(playerName);
        banPlayer(playerName, uuid, null, reason.getSeconds(), reason.getReason(), "CONSOLE", "CONSOLE", reason.getPoints());
    }

    public void unBan(UUID uuid) {
        mySQL.update("DELETE FROM bans WHERE UUID='" + uuid.toString() + "'");
    }

    private void mutePlayer(String name, UUID uuid, String ip, int seconds, String reason, String judgeName, String judgeUUID, int mutePoints) {
        long sec = seconds;
        long end = System.currentTimeMillis() + sec * 1000;
        if (seconds == 0)
            end = 0;
        mySQL.update("INSERT INTO mutes(PLAYERNAME, UUID, IPADDRESS, ENDING, REASON, JUDGE_NAME, JUDGE_UUID) VALUES('" + name + "', '" + uuid + "', '" + ip + "', '" + end + "', '" + reason + "', '" + judgeName + "', '" + judgeUUID + "')");
        mySQL.update("INSERT INTO history(PLAYERNAME, UUID, IPADDRESS, TYPE, ENDING, REASON, POINTS, JUDGE_NAME, JUDGE_UUID) VALUES('" + name + "', '" + uuid + "', '" + ip + "', 'MUTE', '" + end + "', '" + reason + "', '" + mutePoints + "', '" + judgeName + "', '" + judgeUUID + "')");
        checkMutePoints(uuid);
    }

    public void mutePlayerByPlayer(ProxiedPlayer player, ProxiedPlayer judge, Reason reason) {
        player.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Du wurdest wegen §b" + reason.getReason() + " §7gemutet."));
        int[] time = secondsToArray(reason.getSeconds());
        if (time != null)
            player.sendMessage(new TextComponent(plugin.getData().getPrefix() + "§8➥ §7Länge §8» §b" + (time[0] == 1 ? (time[0] + " §7Woche §b") : (time[0] + " §7Wochen §b")) + (time[1] == 1 ? (time[1] + " §7Tag §b") : (time[1] + " §7Tage §b")) + (time[2] == 1 ? (time[2] + " §7Stunde §b") : (time[2] + " §7Stunden §b")) + (time[3] == 1 ? (time[3] + " §7Minute §b") : (time[3] + " §7Minuten §b")) + (time[4] == 1 ? (time[4] + " §7Sekunde §b") : (time[4] + " §7Sekunden §b"))));
        else
            player.sendMessage(new TextComponent(plugin.getData().getPrefix() + "§8➥ §7Länge §8» §bPERMANENT"));
        mutePlayer(player.getName(), player.getUniqueId(), getIPFromPlayer(player), reason.getSeconds(), reason.getReason(), judge.getName(), judge.getUniqueId().toString(), reason.getPoints());
    }

    public void muteOfflinePlayerByPlayer(String playerName, ProxiedPlayer judge, Reason reason) {
        UUID uuid = UUIDFetcher.getUUID(playerName);
        mutePlayer(playerName, uuid, null, reason.getSeconds(), reason.getReason(), judge.getName(), judge.getUniqueId().toString(), reason.getPoints());
    }

    public void mutePlayerByConsole(ProxiedPlayer player, Reason reason) {
        int[] time = secondsToArray(reason.getSeconds());
        player.sendMessage(new TextComponent(plugin.getData().getPrefix() + "Du wurdest wegen §b" + reason.getReason() + " §7gemutet."));
        if (time != null)
            player.sendMessage(new TextComponent(plugin.getData().getPrefix() + "§8➥ §7Länge §8» §b" + (time[0] == 1 ? (time[0] + " §7Woche §b") : (time[0] + " §7Wochen §b")) + (time[1] == 1 ? (time[1] + " §7Tag §b") : (time[1] + " §7Tage §b")) + (time[2] == 1 ? (time[2] + " §7Stunde §b") : (time[2] + " §7Stunden §b")) + (time[3] == 1 ? (time[3] + " §7Minute §b") : (time[3] + " §7Minuten §b")) + (time[4] == 1 ? (time[4] + " §7Sekunde §b") : (time[4] + " §7Sekunden §b"))));
        else
            player.sendMessage(new TextComponent(plugin.getData().getPrefix() + "§8➥ §7Länge §8» §bPERMANENT"));
        mutePlayer(player.getName(), player.getUniqueId(), getIPFromPlayer(player), reason.getSeconds(), reason.getReason(), "CONSOLE", "CONSOLE", reason.getPoints());
    }

    public void muteOfflinePlayerByConsole(String playerName, Reason reason) {
        UUID uuid = UUIDFetcher.getUUID(playerName);
        mutePlayer(playerName, uuid, null, reason.getSeconds(), reason.getReason(), "CONSOLE", "CONSOLE", reason.getPoints());
    }

    public void unMute(UUID uuid) {
        mySQL.update("DELETE FROM mutes WHERE UUID='" + uuid.toString() + "'");
    }

    public String getIPFromPlayer(ProxiedPlayer player) {
        return player.getSocketAddress().toString().split(":")[0].replaceAll("/", "");
    }

    public ArrayList<Reason> getReasons() {
        return reasons;
    }
}
