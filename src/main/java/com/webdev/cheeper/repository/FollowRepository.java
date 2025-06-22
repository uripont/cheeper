package com.webdev.cheeper.repository;

import com.webdev.cheeper.model.User;
import com.webdev.cheeper.model.RoleType;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FollowRepository extends BaseRepository {
	
	public boolean isFollowing(int followerId, int followingId) {
        String query = "SELECT COUNT(*) FROM followers WHERE follower_id = ? AND following_id = ? AND status = 'ACCEPTED'";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, followerId);
            stmt.setInt(2, followingId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean followUser(int followerId, int followingId) {
        if (followerId == followingId) {
            return false; // Can't follow yourself
        }
        
        String query = "INSERT INTO followers (follower_id, following_id, status) VALUES (?, ?, 'ACCEPTED')";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, followerId);
            stmt.setInt(2, followingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean unfollowUser(int followerId, int followingId) {
        if (followerId == followingId) {
            return false; // Can't unfollow yourself
        }

        String query = "DELETE FROM followers WHERE follower_id = ? AND following_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, followerId);
            stmt.setInt(2, followingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public int countFollowers(int userId) {
        String query = "SELECT COUNT(*) FROM followers WHERE following_id = ? AND status = 'ACCEPTED'";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int countFollowing(int userId) {
        String query = "SELECT COUNT(*) FROM followers WHERE follower_id = ? AND status = 'ACCEPTED'";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<User> getFollowers(int userId) {
        String query = "SELECT u.* FROM users u\n" +
                       "JOIN followers f ON u.id = f.follower_id\n" +
                       "WHERE f.following_id = ? AND f.status = 'ACCEPTED'";
        return fetchUsers(query, userId);
    }

    public List<User> getFollowing(int userId) {
        String query = "SELECT u.* FROM users u\n" +
                       "JOIN followers f ON u.id = f.following_id\n" +
                       "WHERE f.follower_id = ? AND f.status = 'ACCEPTED'";
        return fetchUsers(query, userId);
    }

    public void unfollow(int followerId, int followingId) {
        String query = "DELETE FROM followers WHERE follower_id = ? AND following_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, followerId);
            stmt.setInt(2, followingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<User> fetchUsers(String query, int id) {
        List<User> users = new ArrayList<>();
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPicture(rs.getString("picture"));
                user.setRoleType(RoleType.valueOf(rs.getString("role_type")));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public Set<Integer> getFollowingIds(int userId) {
        Set<Integer> ids = new HashSet<>();
        String sql = "SELECT following_id FROM followers WHERE follower_id = ?";

        try(PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ids.add(rs.getInt("following_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ids;
    }

    public List<User> getMutualFollowers(int userId) {
        // Get users who you follow and who follow you back (mutual connections)
        String query = "SELECT DISTINCT u.* FROM users u " +
                      "JOIN followers f1 ON u.id = f1.following_id " +
                      "JOIN followers f2 ON u.id = f2.follower_id " +
                      "WHERE f1.follower_id = ? " +  // You follow them
                      "AND f2.following_id = ? " +   // They follow you
                      "AND f1.status = 'ACCEPTED' " +
                      "AND f2.status = 'ACCEPTED'";
        return fetchUsers(query, userId, userId);
    }

    private List<User> fetchUsers(String query, int... params) {
        List<User> users = new ArrayList<>();
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setInt(i + 1, params[i]);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPicture(rs.getString("picture"));
                user.setRoleType(RoleType.valueOf(rs.getString("role_type")));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
