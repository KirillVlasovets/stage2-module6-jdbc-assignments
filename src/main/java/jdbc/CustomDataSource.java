package jdbc;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomDataSource implements DataSource {
    private static volatile CustomDataSource instance;
    private final String driver;
    private final String url;
    private final String name;
    private final String password;
    private CustomConnector connector = new CustomConnector();

    private CustomDataSource(String driver, String url, String password, String name) {
        this.driver = driver;
        this.url = url;
        this.password = password;
        this.name = name;
    }

    public static CustomDataSource getInstance() {
        try {
            Properties properties = new Properties();
            properties.load(CustomDataSource.class.getClassLoader().getResourceAsStream("app.properties"));
            String driverProp = properties.getProperty("postgres.driver");
            String urlProp = properties.getProperty("postgres.url");
            String userProp = properties.getProperty("postgres.name");
            String passwordProp = properties.getProperty("postgres.password");
            if (instance == null) {
                instance = new CustomDataSource(driverProp, urlProp, passwordProp, userProp);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connector.getConnection(this.url, this.name, this.password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.connector.getConnection(this.url, username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
