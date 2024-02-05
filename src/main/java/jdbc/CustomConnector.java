package jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class CustomConnector {
    public Connection getConnection(String url) {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection(String url, String name, String password) {
        try {
            return DriverManager.getConnection(url, name, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
