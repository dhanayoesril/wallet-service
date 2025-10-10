package com.wallet.db;

import com.wallet.config.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL Driver not found!", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DataSourceProvider.get().getConnection();
    }

    public static int ping() throws SQLException {
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT 1")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public static int executeUpdate(String sql) throws SQLException {
        try (Connection c = getConnection();
             Statement st = c.createStatement()) {
            return st.executeUpdate((sql));
        }
    }

    public static List<UserRow> listUsers() throws SQLException {
        String sql = "SELECT id, username, email FROM users ORDER BY id";
        List<UserRow> out = new ArrayList<>();
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                out.add(new UserRow(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("email")
                ));
            }
        }
        return out;
    }

    public static class UserRow {
        public long id;
        public String username;
        public String email;

        public UserRow(long id, String username, String email) {
            this.id = id;
            this.username = username;
            this.email = email;
        }
    }
}
