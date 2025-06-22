package com.webdev.cheeper.repository;

import com.webdev.cheeper.model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomRepository extends BaseRepository {

    public void save(Room room) {
        String sql = "INSERT INTO room (name, is_private, created_at) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, room.getName());
            stmt.setBoolean(2, room.isPrivate());
            stmt.setTimestamp(3, new Timestamp(room.getCreatedAt().getTime()));
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                room.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Room> findById(Long id) {
        String sql = "SELECT * FROM room WHERE id = ?";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Room> findByUserId(Long userId) {
        String sql = "SELECT r.* FROM room r " +
                    "JOIN room_participants rp ON r.id = rp.room_id " +
                    "WHERE rp.user_id = ? " +
                    "ORDER BY r.created_at DESC";
        List<Room> rooms = new ArrayList<>();
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public Optional<Room> findPrivateRoomBetweenUsers(Long user1Id, Long user2Id) {
        String sql = "SELECT r.* FROM room r " +
                    "JOIN room_participants rp1 ON r.id = rp1.room_id " +
                    "JOIN room_participants rp2 ON r.id = rp2.room_id " +
                    "WHERE r.is_private = true " +
                    "AND rp1.user_id = ? " +
                    "AND rp2.user_id = ? " +
                    "LIMIT 1";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setLong(1, user1Id);
            stmt.setLong(2, user2Id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void addParticipant(Long roomId, Long userId) {
        String sql = "INSERT INTO room_participants (room_id, user_id) VALUES (?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setLong(1, roomId);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getLong("id"));
        room.setName(rs.getString("name"));
        room.setPrivate(rs.getBoolean("is_private"));
        room.setCreatedAt(rs.getTimestamp("created_at"));
        return room;
    }
}
