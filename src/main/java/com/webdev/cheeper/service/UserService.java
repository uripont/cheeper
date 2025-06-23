package com.webdev.cheeper.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
        return userRepository.usernameExists(username, excludeUserId);
    }

    public boolean usernameExists(String username) {
        return usernameExists(username, null);
    }
    public boolean emailExists(String email, Integer excludeUserId) {
        if (excludeUserId != null) {
            return userRepository.emailExists(email, excludeUserId);
        }
        return userRepository.emailExists(email);
    }
    
    public void savePicture(User user, Part filePart) throws IOException {
        System.out.println("Saving picture for user: " + user.getUsername());
        if(filePart == null || filePart.getSize() == 0){
            user.setPicture(getPictureNameById(user.getId()));           
        }else{
            String fileName = imageService.storeImage(filePart, user.getId().toString());
            user.setPicture(fileName);
        }        
    }

    public void updatePicture(User user, Part filePart) throws IOException {
        if (filePart != null && filePart.getSize() > 0) {
            System.out.println("Updating picture for user: " + user.getUsername());
            String fileName = imageService.storeImage(filePart, user.getUsername());
            user.setPicture(fileName);
        } else {
            System.out.println("No new picture provided for user: " + user.getUsername() + ". Retaining existing picture.");
            // Do not update the picture field if no new file is provided
            // The existing picture reference in the user object (which comes from db) will be retained
        }
    }
    
    public String getPicture(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        String fileName = userOpt.map(User::getPicture).orElse(null);
        return imageService.getImagePath(fileName);
    }

    public String getPictureNameById(Integer userId) {
        return userRepository.findUserPicture(userId);
    }


    public Map<String, String> validate(User user, String mode) {
        Map<String, String> errors = new HashMap<>();

        // Validate username
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            errors.put("username", "Username is required");
        } else if (user.getUsername().length() < 3 || user.getUsername().length() > 20) {
            errors.put("username", "Username must be 3-20 characters");
        } else if (usernameExists(user.getUsername(), "edit".equals(mode) ? user.getId() : null)) {
            errors.put("username", "Username already taken");
        }

        // Validate email
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.put("email", "Email is required");
        } else if (!user.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errors.put("email", "Invalid email format");
        } else if (emailExists(user.getEmail(), "edit".equals(mode) ? user.getId() : null)) {
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

    public boolean deleteUser(int userId) {
        return userRepository.delete(userId);
    }
}
