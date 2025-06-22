package com.webdev.cheeper.repository;

import com.webdev.cheeper.model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomRepository extends BaseRepository {

    public void save(Room room) {
        String sql = "INSERT INTO room (name, description, created_by, created_at, expires_at, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, room.getName());
            stmt.setString(2, room.getDescription());
            stmt.setInt(3, room.getCreatedBy());
            stmt.setTimestamp(4, new Timestamp(room.getCreatedAt().getTime()));
            stmt.setTimestamp(5, room.getExpiresAt() != null ? new Timestamp(room.getExpiresAt().getTime()) : null);
            stmt.setBoolean(6, room.isActive());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                room.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Room> findById(Integer id) {
        String sql = "SELECT * FROM room WHERE room_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Room> findByUserId(Integer userId) {
        String sql = "SELECT r.* FROM room r " +
                    "JOIN room_participants rp ON r.room_id = rp.room_id " +
                    "WHERE rp.user_id = ? " +
                    "ORDER BY r.created_at DESC";
        List<Room> rooms = new ArrayList<>();
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public Optional<Room> findPrivateRoomBetweenUsers(Integer user1Id, Integer user2Id) {
        String sql = "SELECT r.* FROM room r " +
                    "JOIN room_participants rp1 ON r.room_id = rp1.room_id " +
                    "JOIN room_participants rp2 ON r.room_id = rp2.room_id " +
                    "WHERE rp1.user_id = ? " +
                    "AND rp2.user_id = ? " +
                    "AND r.is_active = true " +
                    "LIMIT 1";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, user1Id);
            stmt.setInt(2, user2Id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void addParticipant(Integer roomId, Integer userId) {
        String sql = "INSERT INTO room_participants (room_id, user_id) VALUES (?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("room_id"));
        room.setName(rs.getString("name"));
        room.setDescription(rs.getString("description"));
        room.setCreatedBy(rs.getInt("created_by"));
        room.setCreatedAt(rs.getTimestamp("created_at"));
        Timestamp expiresAt = rs.getTimestamp("expires_at");
        if (expiresAt != null) {
            room.setExpiresAt(expiresAt);
        }
        room.setActive(rs.getBoolean("is_active"));
        return room;
    }

    public boolean isUserParticipant(Integer roomId, Integer userId) {
        String sql = "SELECT COUNT(*) FROM room_participants WHERE room_id = ? AND user_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
