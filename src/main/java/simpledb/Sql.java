package simpledb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Sql {
    private final StringBuilder sqlBuilder;
    private final List<Object> params;
    private final SimpleDb simpleDb;

    public Sql(SimpleDb simpleDb){
        this.sqlBuilder =  new StringBuilder();
        this.params = new ArrayList<>();
        this.simpleDb = simpleDb;
    }

    public Sql append(String expression) {
        this.sqlBuilder.append(expression).append(" ");
        return this;
    }

    public Sql append(String expression, Object... args) {
        this.sqlBuilder.append(expression).append(" ");
        params.addAll(Arrays.stream(args).toList());
        return this;
    }

    public long insert() {
        return this.simpleDb.insert(sqlBuilder.toString(), params);
    }

    public int update() {
        return this.simpleDb.update(sqlBuilder.toString(), params);
    }

    public int delete() {
        return simpleDb.delete(sqlBuilder.toString(), params);
    }

    public List<Map<String, Object>> selectRows() {
        return simpleDb.selectRows(sqlBuilder.toString(), params);
    }

    public <T> List<T> selectRows(Class<T> cls) {
        return simpleDb.selectRows(sqlBuilder.toString(), params, cls);
    }

    public Map<String, Object> selectRow() {
        return simpleDb.selectRow(sqlBuilder.toString(), params);
    }

    public LocalDateTime selectDatetime() {
        return simpleDb.selectDateTime(sqlBuilder.toString(), params);
    }

    public Long selectLong() {
        return simpleDb.selectLong(sqlBuilder.toString(), params);
    }

    public String selectString() {
        return simpleDb.selectString(sqlBuilder.toString(), params);
    }

    public Boolean selectBoolean() {
        return simpleDb.selectBoolean(sqlBuilder.toString(), params);
    }

    public Sql appendIn(String sql, Object... args) {
        String inClause = Arrays.stream(args)
                .map(arg -> "?")
                .collect(Collectors.joining(","));
        sql = sql.replaceAll("\\?", inClause);
        return this.append(sql, args);
    }

    public List<Long> selectLongs() {
        return simpleDb.selectLongs(sqlBuilder.toString(), params);
    }
}
