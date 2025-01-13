package simpledb;

import lombok.Getter;
import lombok.Setter;
import utils.DbUtil;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

@Getter
public class SimpleDb{
    private final String username;
    private final String password;
    private final String dbName;
    private final String baseUrl;
    private final String url;
    private Connection connection;
    @Setter
    private boolean devMode = true;

    public SimpleDb(String host, String username, String password, String dbName) {
        this.username = username;
        this.password = password;
        this.dbName = dbName;
        this.baseUrl = "jdbc:mysql://%s:3306".formatted(host);
        this.url = "%s/%s".formatted(baseUrl, dbName);
        try {
            ensureDatabaseExists();
            this.connection = DriverManager.getConnection(this.url, this.username, this.password);
            System.out.println("Connected to " + this.url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ensureDatabaseExists() throws SQLException {
        try (Connection connection = DriverManager.getConnection(this.baseUrl, this.username, this.password)) {
            Connection dbConnection = DriverManager.getConnection(this.url, this.username, this.password);
        } catch (SQLSyntaxErrorException e) {
            if (e.getSQLState().equals("42000")) {
                System.out.println("Database does not exist. Creating database...");
                DbUtil.createDatabase(this.baseUrl, this.username, this.password, this.dbName);
            } else {
                throw e;
            }
        }
    }

    public void run(String expression, Object... args) {
        try(PreparedStatement statement = this.connection.prepareStatement(expression)){
            bindingParams(statement, List.of(args));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Sql genSql() {
        return new Sql(this);
    }

    public void close() {

    }

    public void startTransaction() {

    }

    public void rollback() {

    }

    public void commit() {

    }

    public PreparedStatement bindingParams(PreparedStatement statement, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            statement.setObject(i + 1, params.get(i));
        }
        return statement;
    }

    public long insert(String expression, List<Object> params) {
        System.out.println(expression);
        System.out.println(params);
        try (PreparedStatement statement = this.connection.prepareStatement(expression, Statement.RETURN_GENERATED_KEYS)) {
            bindingParams(statement, params);
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) return generatedKeys.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
