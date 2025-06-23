package com.webdev.cheeper.repository;

import com.webdev.cheeper.model.Association;
import com.webdev.cheeper.model.RoleType;
import com.webdev.cheeper.model.User;
import com.webdev.cheeper.model.VerificationStatus;

import java.sql.*;
import java.util.Optional;

public class AssociationRepository extends UserRepository {

    public void save(Association association) {
        super.save(association);
        saveAssociationDetails(association);
    }

    private void saveAssociationDetails(Association association) {
        String query = "INSERT INTO association (association_id, verification_status, verification_date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, association.getId());
            stmt.setString(2, association.getVerificationStatus().name());
            stmt.setTimestamp(3, association.getVerificationDate());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Association association){
        super.update(association);
        updateAssociationDetails(association);
    }
    public void updateAssociationDetails(Association association) {
        String assocQuery = "UPDATE association SET verification_status = ?, verification_date = ? WHERE association_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(assocQuery)) {
            stmt.setString(1, association.getVerificationStatus().name());
            stmt.setTimestamp(2, association.getVerificationDate());
            stmt.setInt(3, association.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Association> findAssociationByUsername(String username) {
        Optional<User> userOpt = super.findByUsername(username);
        if (userOpt.isEmpty()) return Optional.empty();

        User user = userOpt.get();
        String query = "SELECT * FROM association WHERE association_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Association association = new Association();
                association.setId(user.getId());
                association.setFullName(user.getFullName());
                association.setEmail(user.getEmail());
                association.setUsername(user.getUsername());
                association.setBiography(user.getBiography());
                association.setVerificationStatus(VerificationStatus.valueOf(rs.getString("verification_status")));
                association.setVerificationDate(rs.getTimestamp("verification_date"));
                return Optional.of(association);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public Optional<Association> getProfile(int userId) {
        String query = "SELECT a.*, u.full_name, u.username, u.email, u.biography, u.picture, u.role_type " +
                       "FROM association a " +
                       "JOIN users u ON a.association_id = u.id " +
                       "WHERE a.association_id = ?";

        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Association association = new Association();

                // Set association-specific fields
                association.setId(userId);
                association.setVerificationStatus(VerificationStatus.valueOf(rs.getString("verification_status")));
                association.setVerificationDate(rs.getTimestamp("verification_date"));

                // Set common user fields if needed
                association.setFullName(rs.getString("full_name"));
                association.setUsername(rs.getString("username"));
                association.setEmail(rs.getString("email"));
                association.setBiography(rs.getString("biography"));
                association.setPicture(rs.getString("picture"));
                association.setRoleType(RoleType.valueOf(rs.getString("role_type"))); 

                return Optional.of(association);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}