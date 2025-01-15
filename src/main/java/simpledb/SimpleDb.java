package simpledb;

import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Getter
public class SimpleDb{
    private final String username;
    private final String password;
    private final String dbName;
    private final String baseUrl;
    private final String url;
    private final Connection connection;
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
            if (devMode) {
                System.out.println("Connected to database successfully.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database.", e);
        }
    }

    private void ensureDatabaseExists() throws SQLException {
        String createDbSql = String.format("CREATE DATABASE IF NOT EXISTS %s", dbName);

        try (Connection connection = DriverManager.getConnection(this.baseUrl, this.username, this.password)){
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(createDbSql);
                System.out.printf("Database ensured: %s%n", dbName);
            }
        } catch (SQLSyntaxErrorException e) {
            if ("42000".equals(e.getSQLState())) {
                System.err.printf("Failed to create database: %s. Error: %s%n", dbName, e.getMessage());
            }
            throw e;
        }
    }

    public Sql genSql() {
        return new Sql(this);
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                if (devMode) {
                    System.out.println("Close database successfully.");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to close database connection. "+ e.getMessage());
            }
        }
    }

    public void startTransaction() {
        try {
            if (this.connection != null) {
                this.connection.setAutoCommit(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rollback() {

    }

    public void commit() {

    }

    public long insert(String sql, List<Object> params) {
        return _run(sql, Long.class, params);
    }

    public int update(String sql, List<Object> params) {
        return _run(sql, Integer.class, params);
    }

    public int delete(String sql, List<Object> params) {
        return _run(sql, Integer.class, params);
    }

    public String selectString(String sql, List<Object> params) {
        return _run(sql, String.class, params);
    }

    public Long selectLong(String sql, List<Object> params) {
        return _run(sql, Long.class, params);
    }

    public List<Long> selectLongs(String sql, List<Object> params) {
        List<Map<String, Object>> selectedRows = selectRows(sql, params);
        return selectedRows.stream()
                .map(row -> (Long) row.values().iterator().next())
                .toList();
    }

    public boolean selectBoolean(String sql, List<Object> params) {
        return _run(sql, Boolean.class, params);
    }

    public LocalDateTime selectDateTime(String sql, List<Object> params) {
        return _run(sql, LocalDateTime.class, params);
    }

    public Map<String, Object> selectRow(String sql, List<Object> params) {
        return _run(sql, Map.class, params);
    }

    public <T> T selectRow(String sql, List<Object> params, Class<T> cls) {
        return Util.mapToObject(selectRow(sql, params), cls);
    }

    public List<Map<String, Object>> selectRows(String sql, List<Object> params) {
        return _run(sql, List.class, params);
    }

    public <T> List<T> selectRows(String sql, List<Object> params, Class<T> cls) {
        return selectRows(sql, params).stream().map(m -> Util.mapToObject(m, cls)).toList();
    }

    public void run(String sql, Object... params) {
        _run(sql, Integer.class, Arrays.stream(params).toList());
    }

    private <T> T _run(String sql, Class<T> cls, List<Object> params) {
        System.out.println("sql : " + sql);
        try(PreparedStatement stmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(stmt, params);
            if (sql.startsWith("SELECT")) {
                ResultSet rs = stmt.executeQuery();
                return parseResultSet(rs, cls);
            }
            if (sql.startsWith("INSERT") & cls == Long.class) {
                stmt.executeUpdate();
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return cls.cast(generatedKeys.getLong(1));
                }
            }

            return cls.cast(stmt.executeUpdate());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute query: %s%n%s".formatted(sql, e.getMessage()));
        }
    }

    private PreparedStatement setParams(PreparedStatement stmt, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }
        return stmt;
    }

    private <T> T parseResultSet(ResultSet rs, Class<T> cls) throws SQLException {
        if(cls == Boolean.class) {
            rs.next();
            return cls.cast((rs.getBoolean(1)));
        } else if(cls == String.class){
            rs.next();
            return cls.cast(rs.getString(1));
        } else if(cls == Long.class){
            rs.next();
            return cls.cast(rs.getLong(1));
        } else if(cls == LocalDateTime.class){
            rs.next();
            return cls.cast(rs.getTimestamp(1).toLocalDateTime());
        } else if(cls == Map.class) {
            rs.next();
            return cls.cast(resultSetRowToMap(rs));
        } else if(cls == List.class) {
            List<Map<String, Object>> rows = new ArrayList<>();
            while(rs.next()) {
                rows.add(resultSetRowToMap(rs));
            }
            return cls.cast(rows);
        }
        throw new RuntimeException();
    }

    private Map<String, Object> resultSetRowToMap(ResultSet rs) throws SQLException {
        Map<String, Object> row = new HashMap<>();

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String cname = metaData.getColumnName(i);
            row.put(cname, rs.getObject(i));
        }
        return row;
    }

}