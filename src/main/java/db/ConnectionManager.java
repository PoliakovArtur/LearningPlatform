package db;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConnectionManager {

    private static String url;
    private static String user;
    private static String password;

    private ConnectionManager() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    static void readProperties() {
        String propertiesPath = "src/main/resources/db.properties";
        try(FileInputStream inputStream = new FileInputStream(propertiesPath)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            url = properties.getProperty("url");
            if(url == null) throw new DBConnectionException("need to set url");
            user = properties.getProperty("user");
            if(user == null) throw new DBConnectionException("need to set user");
            password = properties.getProperty("password");
            if(password == null) throw new DBConnectionException("need to set password");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        ConnectionManager.url = url;
    }

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        ConnectionManager.user = user;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        ConnectionManager.password = password;
    }

    public static Connection getConnection(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}


