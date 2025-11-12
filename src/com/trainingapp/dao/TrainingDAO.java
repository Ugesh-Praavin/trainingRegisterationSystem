package com.trainingapp.dao;

import com.trainingapp.DBConnection;
import com.trainingapp.models.Training;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainingDAO {

    public int createTraining(Training t) throws SQLException {
        String sql = "INSERT INTO trainings (title, description, seats, start_date, fee) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getTitle());
            ps.setString(2, t.getDescription());
            ps.setInt(3, t.getSeats());
            ps.setDate(4, t.getStartDate());
            ps.setDouble(5, t.getFee());
            int rows = ps.executeUpdate();
            if (rows == 0) return -1;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    t.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public List<Training> getAllTrainings() throws SQLException {
        List<Training> list = new ArrayList<>();
        String sql = "SELECT id, title, description, seats, start_date, fee FROM trainings ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Training t = new Training(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("seats"),
                        rs.getDate("start_date"),
                        rs.getDouble("fee")
                );
                list.add(t);
            }
        }
        return list;
    }

    public Training getTrainingById(int id) throws SQLException {
        String sql = "SELECT id, title, description, seats, start_date, fee FROM trainings WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Training(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getInt("seats"),
                            rs.getDate("start_date"),
                            rs.getDouble("fee")
                    );
                }
            }
        }
        return null;
    }

    public boolean decrementSeat(int trainingId) throws SQLException {
        String sql = "UPDATE trainings SET seats = seats - 1 WHERE id = ? AND seats > 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainingId);
            return ps.executeUpdate() > 0;
        }
    }
}
