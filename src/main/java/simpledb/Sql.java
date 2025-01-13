package simpledb;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Sql {
    private StringBuilder sqlBuilder;
    private List<Object> params;
    private SimpleDb simpleDb;

    public Sql(SimpleDb simpleDb){
        this.sqlBuilder =  new StringBuilder();
        this.params = new ArrayList<>();
        this.simpleDb = simpleDb;
    }

    public Sql append(String expression) {
        this.sqlBuilder.append(" ").append(expression);
        return this;
    }

    public Sql append(String expression, Object... args) {
        this.sqlBuilder.append(" ").append(expression);
        params.addAll(List.of(args));
        return this;
    }

    public long insert() {
        return simpleDb.insert(sqlBuilder.toString(), params);
    }

    public int update() {
        return simpleDb.update(sqlBuilder.toString(), params);
    }

    public int delete() {
        return simpleDb.delete(sqlBuilder.toString(), params);
    }

    public List<Map<String, Object>> selectRows() {
        return simpleDb.selectRows(sqlBuilder.toString());
    }

    public <T> List<T> selectRows(Class<T> classname) {
        return List.of();
    }

    public Map<String, Object> selectRow() {
        return Map.of();
    }

    public <T> T selectRow(Class<T> classname) {
        return null;
    }

    public LocalDateTime selectDatetime() {
        return null;
    }

    public Long selectLong() {
        return 0L;
    }

    public String selectString() {
        return "";
    }

    public Boolean selectBoolean() {
        return null;
    }

    public Sql appendIn(String s, Object... args) {
        return null;
    }

    public List<Long> selectLongs() {
        return List.of();
    }
}
