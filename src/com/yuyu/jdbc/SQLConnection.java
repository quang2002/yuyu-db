package com.yuyu.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLConnection implements AutoCloseable {

    private Connection connection;

    public SQLConnection(String url, String username, String password) throws SQLException {
        this.connect(url, username, password);
    }

    public PreparedStatement prepareStatement(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = this.connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        return stmt;
    }

    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement stmt = this.prepareStatement(sql, params)) {
            return stmt.executeUpdate();
        }
    }

    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = this.prepareStatement(sql, params);
        return stmt.executeQuery();
    }


    public boolean execute(String sql, Object... params) throws SQLException {
        try (PreparedStatement stmt = this.prepareStatement(sql, params)) {
            return stmt.execute();
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void connect(String url, String username, String password) throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }
}
