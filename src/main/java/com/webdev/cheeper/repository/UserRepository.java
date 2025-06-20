package com.webdev.cheeper.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;  // Add this import for Statement.RETURN_GENERATED_KEYS
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.webdev.cheeper.model.RoleType;
import com.webdev.cheeper.model.User;


public class UserRepository extends BaseRepository {
    
    public boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void save(User user) {
        String query = "INSERT INTO users (full_name, email, username, biography, picture, role_type) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, user.getBiography());
            stmt.setString(5, user.getPicture());
            stmt.setString(6, user.getRoleType().name());
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<User> findByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public Optional<User> findByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<User> findById(int id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }



    protected User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setUsername(rs.getString("username"));
        user.setBiography(rs.getString("biography"));
        user.setPicture(rs.getString("picture"));
        user.setRoleType(RoleType.valueOf(rs.getString("role_type")));  
        return user;
    }
    
    // Retrieve all users from the database
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, full_name, email, role_type FROM users";
        
        try (PreparedStatement statement = db.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    
    public List<User> findRandomUsers(int limit, int excludeUserId) {
        List<User> users = new ArrayList<>();
        
        String query = "SELECT * FROM users " +
            "WHERE id != ? " +
            "AND id NOT IN (" +
                "SELECT following_id FROM followers " +
                "WHERE follower_id = ? AND status = 'ACCEPTED'" +
            ") " +
            "ORDER BY RAND() " +
            "LIMIT ?";

        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, excludeUserId); // Exclude self
            stmt.setInt(2, excludeUserId); // Exclude followed users
            stmt.setInt(3, limit);         // Limit
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRoleType(RoleType.valueOf(rs.getString("role_type")));
                user.setPicture(rs.getString("picture"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }
    
    public Integer findUserIdByEmail(String email) {
        return findByEmail(email)
                .map(User::getId)
                .orElse(null);
    }

    public boolean isUsernameOfUser(int userId, String username) {
        String query = "SELECT COUNT(*) FROM users WHERE id = ? AND username = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<User> searchUsers(String query, int limit, Integer excludeUserId) {
        List<User> users = new ArrayList<>();
        
        // Build dynamic query based on whether we're excluding a user
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM users WHERE ");
        sqlBuilder.append("(full_name LIKE ? OR username LIKE ? OR email LIKE ?) ");
        
        if (excludeUserId != null) {
            sqlBuilder.append("AND id != ? ");
        }
        
        sqlBuilder.append("ORDER BY ");
        sqlBuilder.append("CASE ");
        sqlBuilder.append("  WHEN username LIKE ? THEN 1 ");  // Exact username match gets priority
        sqlBuilder.append("  WHEN full_name LIKE ? THEN 2 ");  // Full name match second
        sqlBuilder.append("  ELSE 3 ");
        sqlBuilder.append("END, ");
        sqlBuilder.append("full_name ASC ");
        sqlBuilder.append("LIMIT ?");
        
        try (PreparedStatement stmt = db.prepareStatement(sqlBuilder.toString())) {
            String searchPattern = "%" + query.toLowerCase() + "%";
            String exactPattern = query.toLowerCase() + "%";
            
            int paramIndex = 1;
            
            // Search parameters
            stmt.setString(paramIndex++, searchPattern); // full_name LIKE
            stmt.setString(paramIndex++, searchPattern); // username LIKE  
            stmt.setString(paramIndex++, searchPattern); // email LIKE
            
            // Exclude user parameter
            if (excludeUserId != null) {
                stmt.setInt(paramIndex++, excludeUserId);
            }
            
            // Ordering parameters
            stmt.setString(paramIndex++, exactPattern); // username priority
            stmt.setString(paramIndex++, exactPattern); // full_name priority
            
            // Limit parameter
            stmt.setInt(paramIndex, limit);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    public List<User> searchUsersByUsername(String username, int limit) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE username LIKE ? ORDER BY username ASC LIMIT ?";
        
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setString(1, username + "%"); // Starts with pattern
            stmt.setInt(2, limit);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
}