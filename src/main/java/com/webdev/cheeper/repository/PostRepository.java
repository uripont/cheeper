package com.webdev.cheeper.repository;
import com.webdev.cheeper.model.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostRepository extends BaseRepository {

    // Guarda un nuevo post en la base de datos
    public void save(Post post) {
        String sql = "INSERT INTO post (source_id, user_id, content, image, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {

            if (post.getSourceId() != null) {
                stmt.setInt(1, post.getSourceId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER); 
            }

            stmt.setInt(2, post.getUserId());
            stmt.setString(3, post.getContent());
            stmt.setString(4, post.getImage());
            stmt.setTimestamp(5, post.getCreatedAt());
            stmt.setTimestamp(6, post.getUpdatedAt());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Actualiza un post existente
    /*public void update(Post post) {
        String sql = "UPDATE post SET source_id = ?, user_id = ?, content = ?, image = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, post.getSourceId());
            stmt.setInt(2, post.getUserId());
            stmt.setString(3, post.getContent());
            stmt.setString(4, post.getImage());
            stmt.setTimestamp(5, post.getUpdatedAt());
            stmt.setInt(6, post.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    // Busca un post por su ID
    public Optional<Post> findById(int id) {
        String sql = "SELECT * FROM post WHERE post_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToPost(rs)); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    

    // Returns all posts in the database, ordered by creation date (excluding comments)
    public List<Post> findAll() {
        String sql = "SELECT * FROM post WHERE source_id IS NULL ORDER BY created_at DESC";
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public List<Post> findAllButYou(int userId) {
        String sql = "SELECT * FROM post WHERE user_id != ? AND source_id IS NULL ORDER BY created_at DESC";
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    // Lista todos los posts de un usuario (excluding comments)
    public List<Post> findByUserId(int userId) {
        String sql = "SELECT * FROM post WHERE user_id = ? AND source_id IS NULL ORDER BY created_at DESC";
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    // Given a userId(followerId), returns all posts from users that the user is following (excluding comments)
    public List<Post> findByFollowedUsers(int followerId) {
        String sql = "SELECT p.* FROM post p JOIN followers f ON p.user_id = f.following_id WHERE f.follower_id = ? AND f.status = 'ACCEPTED' AND p.source_id IS NULL ORDER BY p.created_at DESC";
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, followerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    // Returns all posts that are replies to a source post
    public List<Post> findBySourceId(int sourcePostId) {
        String sql = "SELECT * FROM post WHERE source_id = ? ORDER BY created_at DESC";
        List<Post> posts = new ArrayList<>();
    
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, sourcePostId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return posts;
    }
    
    
    

    // Mapea un ResultSet a un objeto Post
    private Post mapResultSetToPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getInt("post_id"));
        post.setSourceId(rs.getInt("source_id"));
        post.setUserId(rs.getInt("user_id"));
        post.setContent(rs.getString("content"));
        post.setImage(rs.getString("image"));
        post.setCreatedAt(rs.getTimestamp("created_at"));
        post.setUpdatedAt(rs.getTimestamp("updated_at"));
        return post;
    }

    public void deleteById(int postId) {
        String sql = "DELETE FROM post WHERE post_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting post", e);
        }
    }

    public void update(Post post) {
        String sql = "UPDATE post SET content = ?, image = ?, updated_at = ? WHERE post_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setString(1, post.getContent());
            stmt.setString(2, post.getImage());
            stmt.setTimestamp(3, post.getUpdatedAt());
            stmt.setInt(4, post.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No post found with ID: " + post.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating post", e);
        }
    }
}
