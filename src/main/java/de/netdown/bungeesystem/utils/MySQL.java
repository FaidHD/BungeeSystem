package de.netdown.bungeesystem.utils;

import de.netdown.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.ProxyServer;

import java.sql.*;

public class MySQL {

    private BungeeSystem plugin;

    private String HOST;
    private String PORT;
    private String DATABASE;
    private String USER;
    private String PASSWORD;

    private Connection con;

    public MySQL(String host, String port, String database, String user, String password, BungeeSystem plugin) {
        this.HOST = host;
        this.PORT = port;
        this.DATABASE = database;
        this.USER = user;
        this.PASSWORD = password;

        this.plugin = plugin;

        connect();
    }

    private void connect() {
        try {
            this.con = DriverManager.getConnection("jdbc:mysql://" + this.HOST + ":" + this.PORT + "/" + this.DATABASE + "?autoReconnect=true", this.USER, this.PASSWORD);
            ProxyServer.getInstance().getLogger().info("Die Verbindung zur MySQL wurde hergestellt!");
        } catch (SQLException e) {
            ProxyServer.getInstance().getLogger().info("Die Verbindung zur MySQL ist fehlgeschlagen! Fehler: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (con != null) {
                con.close();
                ProxyServer.getInstance().getLogger().info(plugin.getData().getPrefix() + "Die Verbindung zur MySQL wurde Erfolgreich beendet!");
            }
        } catch (SQLException e) {
            ProxyServer.getInstance().getLogger().info(plugin.getData().getPrefix() + "Fehler beim beenden der Verbindung zur MySQL! Fehler: " + e.getMessage());
        }
    }

    public void update(String qry) {
        try {
            Statement st = con.createStatement();
            st.executeUpdate(qry);
            st.close();
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    public ResultSet query(String qry) {
        ResultSet rs = null;
        try {
            Statement st = con.createStatement();
            rs = st.executeQuery(qry);
        } catch (SQLException e) {
            System.err.println(e);
        }
        return rs;
    }

    public Connection getCon() {
        return con;
    }
}