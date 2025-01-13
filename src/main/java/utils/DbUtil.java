package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtil {
    public static void createDatabase(String baseUrl, String username, String password, String dbName) throws SQLException {
        try (Connection connection = DriverManager.getConnection(baseUrl, username, password);
                Statement statement = connection.createStatement()) {
            String createDbSql = "CREATE DATABASE IF NOT EXISTS %s".formatted(dbName);
            statement.executeUpdate(createDbSql);
            System.out.printf("Database created successfully: %s%n", dbName);
        }
    }
}
