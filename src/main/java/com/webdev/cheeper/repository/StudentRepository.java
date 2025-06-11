package com.webdev.cheeper.repository;

import java.sql.*;
import java.util.Optional;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import com.webdev.cheeper.model.RoleType;
import com.webdev.cheeper.model.Student;
import com.webdev.cheeper.model.User;

public class StudentRepository extends UserRepository {
    
    public void save(Student student) {
        super.save(student); // Save user data first
        saveStudentDetails(student);
    }

    private void saveStudentDetails(Student student) {
        String query = "INSERT INTO student (student_id, birthdate, social_links, degrees, enrolled_subjects) " +
                      "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, student.getId());
            stmt.setDate(2, student.getBirthdate());
            stmt.setString(3, mapToJson(student.getSocialLinks()));
            stmt.setString(4, mapToJson(student.getDegrees()));
            stmt.setString(5, mapToJson(student.getEnrolledSubjects()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Student student) {
        // Update user data
        String userQuery = "UPDATE users SET full_name = ?, email = ?, biography = ? WHERE id = ?";
        try (PreparedStatement stmt = db.prepareStatement(userQuery)) {
            stmt.setString(1, student.getFullName());
            stmt.setString(2, student.getEmail());
            stmt.setString(3, student.getBiography());
            stmt.setInt(4, student.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Update student data
        String studentQuery = "UPDATE student SET birthdate = ?, social_links = ?, " +
                            "degrees = ?, enrolled_subjects = ? WHERE student_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(studentQuery)) {
            stmt.setDate(1, student.getBirthdate());
            stmt.setString(2, mapToJson(student.getSocialLinks()));
            stmt.setString(3, mapToJson(student.getDegrees()));
            stmt.setString(4, mapToJson(student.getEnrolledSubjects()));
            stmt.setInt(5, student.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public Optional<Student> findStudentByUsername(String username) {
        Optional<User> userOptional = super.findByUsername(username);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();
        String query = "SELECT * FROM student WHERE student_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Student student = new Student();
                // Copy all user properties to student
                student.setId(user.getId());
                student.setFullName(user.getFullName());
                student.setEmail(user.getEmail());
                student.setUsername(user.getUsername());
                student.setBiography(user.getBiography());
                
                // Set student-specific properties
                student.setBirthdate(rs.getDate("birthdate"));
                student.setSocialLinks(jsonToMap(rs.getString("social_links")));
                student.setDegrees(jsonToMap(rs.getString("degrees")));
                student.setEnrolledSubjects(jsonToMap(rs.getString("enrolled_subjects")));
                
                return Optional.of(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private String mapToJson(Map<String, String> map) {
        return map != null ? new JSONObject(map).toString() : "{}";
    }

    private Map<String, String> jsonToMap(String json) {
        Map<String, String> map = new HashMap<>();
        if (json != null && !json.isEmpty()) {
            JSONObject jsonObject = new JSONObject(json);
            for (String key : jsonObject.keySet()) {
                map.put(key, jsonObject.getString(key));
            }
        }
        return map;
    }
    
    public Optional<Student> getProfile(int userId) {
        String query = "SELECT s.*, u.full_name, u.username, u.email, u.biography, u.picture, u.role_type " +
                       "FROM student s JOIN users u ON s.student_id = u.id WHERE s.student_id = ?";

        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Student student = new Student();
                
                // Set student-specific fields
                student.setId(userId);
                student.setBirthdate(rs.getDate("birthdate"));
                student.setSocialLinks(jsonToMap(rs.getString("social_links")));
                student.setDegrees(jsonToMap(rs.getString("degrees")));
                student.setEnrolledSubjects(jsonToMap(rs.getString("enrolled_subjects")));

                // Set common user fields 
                student.setFullName(rs.getString("full_name"));
                student.setUsername(rs.getString("username"));
                student.setEmail(rs.getString("email"));
                student.setBiography(rs.getString("biography"));
                student.setPicture(rs.getString("picture"));
                student.setRoleType(RoleType.valueOf(rs.getString("role_type")));  

                return Optional.of(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}