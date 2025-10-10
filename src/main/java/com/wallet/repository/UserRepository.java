package com.wallet.repository;

import com.wallet.db.Database;
import com.wallet.dto.user.UserResponse;
import com.wallet.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<User>();
        try (Connection c = Database.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, username, email FROM users ORDER BY id")) {
            while (rs.next()) {
                list.add(new User(rs.getLong(1), rs.getString(2), rs.getString(3)));
            }
        }
        return list;
    }

    public User insert(String username, String email, String password) throws SQLException {
        final String sql =
            "INSERT INTO users (username, email, password) " +
                "VALUES (?, ?, ?) " +
                "RETURNING id, username, email";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("email")
                    );
                }
            }
        }
        throw new SQLException(("insert_failed"));
    }
}
