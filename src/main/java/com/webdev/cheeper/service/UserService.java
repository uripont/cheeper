package com.webdev.cheeper.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.servlet.http.Part;

import com.webdev.cheeper.model.User;
import com.webdev.cheeper.repository.UserRepository;

public class UserService {
    protected final UserRepository userRepository;
    private final ImageService imageService;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.imageService = new ImageService();
    }
    
    public boolean usernameExists(String username, Integer excludeUserId) {
        if (excludeUserId != null) {
            // For existing users (edit mode), exclude their own ID from the check
            return userRepository.findByUsername(username)
                .map(u -> !u.getId().equals(excludeUserId))
                .orElse(false);
        }
        // For new users, check if username exists at all
        return userRepository.usernameExists(username);
    }

    public boolean usernameExists(String username) {
        return usernameExists(username, null);
    }
    public boolean emailExists(String email) {
    	return userRepository.emailExists(email);
    }
    
    public void savePicture(User user, Part filePart) throws IOException {
        String fileName = imageService.storeImage(filePart, user.getUsername());
        user.setPicture(fileName);
    }
    
    public String getPicture(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        String fileName = userOpt.map(User::getPicture).orElse(null);
        return imageService.getImagePath(fileName);
    }


    public Map<String, String> validate(User user, String mode) {
        Map<String, String> errors = new HashMap<>();

        // Validate username
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            errors.put("username", "Username is required");
        } else if (user.getUsername().length() < 3 || user.getUsername().length() > 20) {
            errors.put("username", "Username must be 3-20 characters");
        } else if (userRepository.usernameExists(user.getUsername()) && !"edit".equals(mode)) {
            errors.put("username", "Username already taken");
        }

        // Validate email
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.put("email", "Email is required");
        } else if (!user.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errors.put("email", "Invalid email format");
        } else if (userRepository.emailExists(user.getEmail()) && !"edit".equals(mode)) {
            errors.put("email", "Email already registered");
        }

        // Validate biography
        if (user.getBiography() != null && user.getBiography().length() > 500) {
            errors.put("biography", "Biography cannot exceed 500 characters");
        }

        return errors;
    }

    public Map<String, String> register(User user, Part filePart) throws IOException{
        Map<String, String> errors = validate(user, "register");
        if (errors.isEmpty()) {
            try {
                savePicture(user, filePart);
                userRepository.save(user);
            } catch (Exception e) {
                errors.put("system", "Registration failed: " + e.getMessage());
                e.printStackTrace(); 
            }
        }
        return errors;
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Integer getUserIdByEmail(String email) {
    	return userRepository.findUserIdByEmail(email);
    }
    
    public List<User> getRandomUsers(int limit, int excludeUserId){
    	return userRepository.findRandomUsers(limit, excludeUserId);
    }

    public boolean isUsernameOfUser(int userId, String username) {
        return userRepository.isUsernameOfUser(userId, username);
    }
   
    public List<User> searchUsers(String query, int limit, Integer excludeUserId) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Clean and validate query
        String cleanQuery = query.trim();
        if (cleanQuery.length() < 2) {
            return new ArrayList<>(); // Require at least 2 characters
        }
        
        return userRepository.searchUsers(cleanQuery, limit, excludeUserId);
    }

    public List<User> searchUsersByUsername(String username, int limit) {
        if (username == null || username.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return userRepository.searchUsersByUsername(username.trim(), limit);
    }

    public List<User> getRecommendedUsers(int limit, int excludeUserId) {
        return userRepository.findRandomUsers(limit, excludeUserId);
    }
    
}