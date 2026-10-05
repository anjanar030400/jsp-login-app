package com.project5.dao;

import com.project5.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * All database access for the `users` table. Every query uses a
 * PreparedStatement with bound parameters -- never string-concatenated
 * SQL -- to prevent SQL injection.
 */
public class UserDAO {

    public boolean isUserIdTaken(String userId) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isEmailTaken(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void insertUser(User u) throws SQLException {
        String sql = "INSERT INTO users " +
            "(user_id, password_hash, password_salt, first_name, last_name, email, " +
            " date_of_birth, photo_path, security_question, security_answer_hash, security_answer_salt) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.userId);
            ps.setString(2, u.passwordHash);
            ps.setString(3, u.passwordSalt);
            ps.setString(4, u.firstName);
            ps.setString(5, u.lastName);
            ps.setString(6, u.email);
            ps.setString(7, u.dateOfBirth);
            ps.setString(8, u.photoPath);
            ps.setString(9, u.securityQuestion);
            ps.setString(10, u.securityAnswerHash);
            ps.setString(11, u.securityAnswerSalt);
            ps.executeUpdate();
        }
    }

    public User findByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapRow(rs);
            }
        }
    }

    public boolean updatePassword(String userId, String newHash, String newSalt) throws SQLException {
        String sql = "UPDATE users SET password_hash = ?, password_salt = ? WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setString(2, newSalt);
            ps.setString(3, userId);
            return ps.executeUpdate() == 1;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.id = rs.getInt("id");
        u.userId = rs.getString("user_id");
        u.passwordHash = rs.getString("password_hash");
        u.passwordSalt = rs.getString("password_salt");
        u.firstName = rs.getString("first_name");
        u.lastName = rs.getString("last_name");
        u.email = rs.getString("email");
        u.dateOfBirth = rs.getString("date_of_birth");
        u.photoPath = rs.getString("photo_path");
        u.securityQuestion = rs.getString("security_question");
        u.securityAnswerHash = rs.getString("security_answer_hash");
        u.securityAnswerSalt = rs.getString("security_answer_salt");
        return u;
    }
}
