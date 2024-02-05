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

    public Connection getConnection() {
        try {
            Properties properties = new Properties();
            properties.load(CustomConnector.class.getClassLoader().getResourceAsStream("app.properties"));
            String url = properties.getProperty("postgres.url");
            String user = properties.getProperty("postgres.name");
            String password = properties.getProperty("postgres.password");
            return DriverManager.getConnection(url, user, password);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
