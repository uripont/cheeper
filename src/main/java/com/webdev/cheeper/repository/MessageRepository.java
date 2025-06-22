package com.webdev.cheeper.repository;

import com.webdev.cheeper.model.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageRepository extends BaseRepository {

    public void save(Message message) {
        String sql = "INSERT INTO messages (room_id, sender_id, content, sent_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, message.getRoomId());
            stmt.setInt(2, message.getSenderId());
            stmt.setString(3, message.getContent());
            stmt.setTimestamp(4, new Timestamp(message.getCreatedAt().getTime()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    message.setId(rs.getInt(1));
                }
            } else {
                 System.err.println("MessageRepository.save: No rows affected, message might not have been saved.");
            }
        } catch (SQLException e) {
            System.err.println("MessageRepository.save: SQLException occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Optional<Message> findById(Integer id) {
        String sql = "SELECT * FROM messages WHERE message_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToMessage(rs));
            }
        } catch (SQLException e) {
            System.err.println("MessageRepository.findById: SQLException occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Message> findByRoomId(Integer roomId) {
        String sql = "SELECT * FROM messages WHERE room_id = ? ORDER BY sent_at ASC";
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }
        } catch (SQLException e) {
            System.err.println("MessageRepository.findByRoomId: SQLException occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return messages;
    }

    public List<Message> findLatestByRoomId(Integer roomId, int limit) {
        String sql = "SELECT * FROM messages WHERE room_id = ? ORDER BY sent_at DESC LIMIT ?";
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }
        } catch (SQLException e) {
            System.err.println("MessageRepository.findLatestByRoomId: SQLException occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return messages;
    }

    private Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("message_id"));
        message.setRoomId(rs.getInt("room_id"));
        message.setSenderId(rs.getInt("sender_id"));
        message.setContent(rs.getString("content"));
        message.setCreatedAt(rs.getTimestamp("sent_at"));
        return message;
    }
}
