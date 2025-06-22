package com.webdev.cheeper.repository;

import com.webdev.cheeper.model.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageRepository extends BaseRepository {

    public void save(Message message) {
        String sql = "INSERT INTO message (room_id, sender_id, content, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, message.getRoomId());
            stmt.setLong(2, message.getSenderId());
            stmt.setString(3, message.getContent());
            stmt.setTimestamp(4, new Timestamp(message.getCreatedAt().getTime()));
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                message.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Message> findById(Long id) {
        String sql = "SELECT * FROM message WHERE id = ?";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToMessage(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Message> findByRoomId(Long roomId) {
        String sql = "SELECT * FROM message WHERE room_id = ? ORDER BY created_at ASC";
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setLong(1, roomId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public List<Message> findLatestByRoomId(Long roomId, int limit) {
        String sql = "SELECT * FROM message WHERE room_id = ? ORDER BY created_at DESC LIMIT ?";
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setLong(1, roomId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    private Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getLong("id"));
        message.setRoomId(rs.getLong("room_id"));
        message.setSenderId(rs.getLong("sender_id"));
        message.setContent(rs.getString("content"));
        message.setCreatedAt(rs.getTimestamp("created_at"));
        return message;
    }
}
