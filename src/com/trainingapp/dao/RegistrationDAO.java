package com.trainingapp.dao;

import com.trainingapp.DBConnection;
import com.trainingapp.models.Registration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDAO {

    public int createRegistration(Registration r) throws SQLException {
        String sql = "INSERT INTO registrations (user_id, training_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getUserId());
            ps.setInt(2, r.getTrainingId());
            int rows = ps.executeUpdate();
            if (rows == 0) return -1;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    r.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public List<String> getAllRegistrationsSummary() throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT r.id, u.name as user_name, t.title as training_title, r.registration_date, r.status " +
                "FROM registrations r " +
                "JOIN users u ON r.user_id = u.id " +
                "JOIN trainings t ON r.training_id = t.id " +
                "ORDER BY r.registration_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String line = String.format("%d | %s -> %s | %s | %s",
                        rs.getInt("id"),
                        rs.getString("user_name"),
                        rs.getString("training_title"),
                        rs.getTimestamp("registration_date"),
                        rs.getString("status"));
                list.add(line);
            }
        }
        return list;
    }
}
