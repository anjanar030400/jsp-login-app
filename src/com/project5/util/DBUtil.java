package com.project5.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Loads DB connection settings from db.properties (on the classpath, i.e.
 * WEB-INF/classes/db.properties) and hands out JDBC connections.
 *
 * db.properties is excluded from version control via .gitignore -- see
 * db.properties.example for the expected keys.
 */
public final class DBUtil {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException(
                    "db.properties not found on classpath (expected at WEB-INF/classes/db.properties)");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }

        // Explicitly load the driver so it registers with DriverManager under
        // the webapp's own classloader. Relying solely on the JDBC 4
        // auto-registration (META-INF/services) can silently fail inside an
        // application server, since ServiceLoader discovery at JVM start can
        // run under a different classloader than the one that loaded this
        // webapp's copy of the driver jar (WEB-INF/lib).
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found on classpath", e);
        }
    }

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        String url = PROPS.getProperty("db.url");
        String user = PROPS.getProperty("db.user");
        String password = PROPS.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }
}
