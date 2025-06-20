package com.webdev.cheeper.repository;

import com.webdev.cheeper.model.Like;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LikeRepository extends BaseRepository {

    public void save(Like like) {
        String sql = "INSERT INTO likes (post_id, user_id, created_at) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, like.getPostId());
            stmt.setInt(2, like.getUserId());
            stmt.setTimestamp(3, like.getCreatedAt());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int postId, int userId) {
        String sql = "DELETE FROM likes WHERE post_id = ? AND user_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(int postId, int userId) {
        String sql = "SELECT 1 FROM likes WHERE post_id = ? AND user_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Like> findByPostId(int postId) {
        String sql = "SELECT * FROM likes WHERE post_id = ?";
        List<Like> likes = new ArrayList<>();
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Like like = new Like();
                like.setPostId(rs.getInt("post_id"));
                like.setUserId(rs.getInt("user_id"));
                like.setCreatedAt(rs.getTimestamp("created_at"));
                likes.add(like);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return likes;
    }
}