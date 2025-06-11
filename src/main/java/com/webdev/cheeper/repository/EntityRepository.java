package com.webdev.cheeper.repository;

import java.sql.*;
import java.util.Optional;

import com.webdev.cheeper.model.Entity;
import com.webdev.cheeper.model.RoleType;
import com.webdev.cheeper.model.User;


public class EntityRepository extends UserRepository {
    
    public void save(Entity entity) {
        super.save(entity); // Save user data first
        saveEntityDetails(entity);
    }

    private void saveEntityDetails(Entity entity) {
        String query = "INSERT INTO entity (entity_id, department) VALUES (?, ?)";
        
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, entity.getId());
            stmt.setString(2, entity.getDepartment());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Entity entity) {
        // Update user data
        String userQuery = "UPDATE users SET full_name = ?, email = ?, biography = ? WHERE id = ?";
        try (PreparedStatement stmt = db.prepareStatement(userQuery)) {
            stmt.setString(1, entity.getFullName());
            stmt.setString(2, entity.getEmail());
            stmt.setString(3, entity.getBiography());
            stmt.setInt(4, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Update entity data
        String entityQuery = "UPDATE entity SET department = ? WHERE entity_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(entityQuery)) {
            stmt.setString(1, entity.getDepartment());
            stmt.setInt(2, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Optional<Entity> findEntityByUsername(String username) {
        Optional<User> userOptional = super.findByUsername(username);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();
        String query = "SELECT * FROM entity WHERE entity_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Entity entity = new Entity();
                // Copy all user properties to entity
                entity.setId(user.getId());
                entity.setFullName(user.getFullName());
                entity.setEmail(user.getEmail());
                entity.setUsername(user.getUsername());
                entity.setBiography(user.getBiography());
                
                // Set entity-specific property
                entity.setDepartment(rs.getString("department"));
                
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Additional methods if needed
    public boolean existsByDepartment(String department) {
        String query = "SELECT COUNT(*) FROM entity WHERE department = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setString(1, department);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    public Optional<Entity> getProfile(int userId) {
        String query = "SELECT e.*, u.full_name, u.username, u.email, u.biography, u.picture, u.role_type " +
                       "FROM entity e " +
                       "JOIN users u ON e.entity_id = u.id " +
                       "WHERE e.entity_id = ?";

        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Entity entity = new Entity();

                // Set entity-specific fields
                entity.setId(userId);
                entity.setDepartment(rs.getString("department"));

                // Set common user fields 
                entity.setFullName(rs.getString("full_name"));
                entity.setUsername(rs.getString("username"));
                entity.setEmail(rs.getString("email"));
                entity.setBiography(rs.getString("biography"));
                entity.setPicture(rs.getString("picture"));
                entity.setRoleType(RoleType.valueOf(rs.getString("role_type"))); 

                return Optional.of(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
}