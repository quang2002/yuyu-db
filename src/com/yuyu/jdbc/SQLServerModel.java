package com.yuyu.jdbc;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.yuyu.annotations.SQLColumn;

public class SQLServerModel<T> extends SQLModelBase<T> {

    public SQLServerModel(Class<T> entityClass) throws SQLException {
        super(entityClass);
    }

    public SQLServerModel(SQLConnection connection, Class<T> entityClass) throws Exception {
        super(connection, entityClass);
    }

    public SQLServerModel(String connectionString, String username, String password, Class<T> entityClass)
            throws Exception {
        super(connectionString, username, password, entityClass);
    }

    @Override
    public T get(Object... primaryKeys) throws Exception {
        if (primaryKeys == null || primaryKeys.length == 0) {
            throw new SQLException("There is no primary key");
        }

        List<Field> keyFields = new ArrayList<>();
        keyFields.addAll(AUTO_INCREMENT_FIELDS);
        keyFields.addAll(PRIMARY_KEY_FIELDS);

        List<Field> allFields = new ArrayList<>(keyFields);
        allFields.addAll(NORMAL_FIELDS);

        String sql = "SELECT * FROM [" + getTableName() + "] WHERE ";

        sql += String.join(" AND ",
                keyFields.stream().map((t) -> "[" + t.getAnnotation(SQLColumn.class).column() + "] = ?")
                        .collect(Collectors.toList()));

        try (ResultSet rs = getConnection().executeQuery(sql, primaryKeys)) {
            while (rs.next()) {
                T instance = entityClass.getConstructor().newInstance();

                for (Field field : allFields) {
                    field.setAccessible(true);
                    field.set(instance, rs.getObject(field.getAnnotation(SQLColumn.class).column()));
                }

                return instance;
            }
        }

        return null;
    }

    @Override
    public List<T> getall() throws Exception {
        String sql = "SELECT * FROM [" + getTableName() + "]";

        List<T> result = new ArrayList<>();

        List<Field> allFields = new ArrayList<>();

        allFields.addAll(AUTO_INCREMENT_FIELDS);
        allFields.addAll(PRIMARY_KEY_FIELDS);
        allFields.addAll(NORMAL_FIELDS);

        try (ResultSet rs = getConnection().executeQuery(sql)) {
            while (rs.next()) {
                T instance = entityClass.getConstructor().newInstance();

                for (Field field : allFields) {
                    field.setAccessible(true);
                    field.set(instance, rs.getObject(field.getAnnotation(SQLColumn.class).column()));
                }

                result.add(instance);
            }
        }

        return result;
    }

    @Override
    public int insert(T data) throws Exception {
        String sql = "INSERT INTO [" + getTableName() + "]";

        List<Field> insertableColumns = new ArrayList<>();

        insertableColumns.addAll(PRIMARY_KEY_FIELDS);
        insertableColumns.addAll(NORMAL_FIELDS);

        sql += "(" + String.join(",",
                insertableColumns.stream().map(t -> "[" + t.getAnnotation(SQLColumn.class).column() + "]")
                        .collect(Collectors.toList()))
                + ")";

        sql += " VALUES (" + String.join(",", insertableColumns.stream().map((t) -> "?").collect(Collectors.toList()))
                + ")";

        List<Object> values = new ArrayList<>();

        for (Field field : insertableColumns) {
            field.setAccessible(true);
            values.add(field.get(data));
        }

        return getConnection().executeUpdate(sql, values.toArray());
    }

    @Override
    public int update(T data) throws Exception {
        List<Field> keyFields = new ArrayList<>();
        keyFields.addAll(PRIMARY_KEY_FIELDS);
        keyFields.addAll(AUTO_INCREMENT_FIELDS);

        String sql = "UPDATE [" + getTableName() + "] SET ";
        sql += String.join(",",
                NORMAL_FIELDS.stream().map((t) -> "[" + t.getAnnotation(SQLColumn.class).column() + "] = ?")
                        .collect(Collectors.toList()));
        sql += " WHERE " + String.join(" AND ", keyFields.stream()
                .map((t) -> "[" + t.getAnnotation(SQLColumn.class).column() + "] = ?").collect(Collectors.toList()));

        List<Object> values = new ArrayList<>();

        for (Field field : NORMAL_FIELDS) {
            field.setAccessible(true);
            values.add(field.get(data));
        }

        for (Field field : keyFields) {
            field.setAccessible(true);
            values.add(field.get(data));
        }

        return getConnection().executeUpdate(sql, values.toArray());
    }

    @Override
    public int delete(T data) throws Exception {
        List<Field> keyFields = new ArrayList<>();
        keyFields.addAll(AUTO_INCREMENT_FIELDS);
        keyFields.addAll(PRIMARY_KEY_FIELDS);

        String sql = "DELETE FROM [" + getTableName() + "] WHERE ";
        sql += String.join(" AND ",
                keyFields.stream().map((t) -> "[" + t.getAnnotation(SQLColumn.class).column() + "] = ?")
                        .collect(Collectors.toList()));

        List<Object> values = new ArrayList<>();

        for (Field field : keyFields) {
            field.setAccessible(true);
            values.add(field.get(data));
        }

        return getConnection().executeUpdate(sql, values.toArray());
    }
}
