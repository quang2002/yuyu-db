package com.yuyu.jdbc;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;

import com.yuyu.annotations.SQLColumn;
import com.yuyu.annotations.SQLTable;

public abstract class SQLModelBase<T> implements ISQLModel<T> {

    protected final Class<T> entityClass;
    private final String tableName;
    private SQLConnection connection;

    protected final ArrayList<Field> AUTO_INCREMENT_FIELDS = new ArrayList<>();
    protected final ArrayList<Field> PRIMARY_KEY_FIELDS = new ArrayList<>();
    protected final ArrayList<Field> NORMAL_FIELDS = new ArrayList<>();

    public void setConnection(SQLConnection connection) {
        this.connection = connection;
    }

    public SQLConnection getConnection() {
        return connection;
    }

    public String getTableName() {
        return tableName;
    }

    SQLModelBase(Class<T> entityClass) throws SQLException {
        this.entityClass = entityClass;

        if (this.entityClass.isAnnotationPresent(SQLTable.class)) {
            this.tableName = this.entityClass.getAnnotation(SQLTable.class).table();
        } else {
            throw new SQLException("The class " + this.entityClass.getName() + " is not annotated with @SQLTable");
        }

        for (Field field : entityClass.getFields()) {
            if (field.isAnnotationPresent(SQLColumn.class)) {
                SQLColumn fieldAnnotation = field.getAnnotation(SQLColumn.class);

                if (fieldAnnotation.isAutoIncrement()) {
                    AUTO_INCREMENT_FIELDS.add(field);
                } else if (fieldAnnotation.isPrimaryKey()) {
                    PRIMARY_KEY_FIELDS.add(field);
                } else {
                    NORMAL_FIELDS.add(field);
                }
            }
        }
    }

    SQLModelBase(SQLConnection connection, Class<T> entityClass) throws SQLException {
        this(entityClass);
        this.connection = connection;
    }

    SQLModelBase(String connectionString, String username, String password, Class<T> entityClass) throws SQLException {
        this(entityClass);
        this.connection = new SQLConnection(connectionString, username, password);
    }
}
