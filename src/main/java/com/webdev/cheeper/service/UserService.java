package com.webdev.cheeper.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.servlet.http.Part;

import com.webdev.cheeper.model.User;
import com.webdev.cheeper.repository.UserRepository;

public class UserService {
    protected final UserRepository userRepository;
    private static final String UPLOAD_DIRECTORY = "/usr/local/tomcat/webapps/uploads";
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public boolean usernameExists(String username) {
    	return userRepository.usernameExists(username);
    }
    public boolean emailExists(String email) {
    	return userRepository.emailExists(email);
    }
    
    public void savePicture(User user, Part filePart) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            user.setPicture("default.png");
            return;
        }
        
        String originalName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
		String extension = "";
		String repositoryName = "";
	    int dotIndex = originalName.lastIndexOf('.');
	    if (dotIndex > 0) {
	        extension = originalName.substring(dotIndex);
	        repositoryName = user.getUsername() + extension;
	    }     
	    
        // Ensure upload directory exists
        File uploadDir = new File(UPLOAD_DIRECTORY);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        // Save file
        File file = new File(uploadDir, repositoryName);
        try (InputStream fileContent = filePart.getInputStream()) {
            Files.copy(fileContent, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            user.setPicture(repositoryName);
        }
    }
    
    public String getPicture(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return userOpt.get().getPicture();
        }
        return "default.png";
    }


    public Map<String, String> validate(User user) {
        Map<String, String> errors = new HashMap<>();

        // Validate username
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            errors.put("username", "Username is required");
        } else if (user.getUsername().length() < 3 || user.getUsername().length() > 20) {
            errors.put("username", "Username must be 3-20 characters");
        } else if (userRepository.usernameExists(user.getUsername())) {
            errors.put("username", "Username already taken");
        }

        // Validate email
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.put("email", "Email is required");
        } else if (!user.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errors.put("email", "Invalid email format");
        } else if (userRepository.emailExists(user.getEmail())) {
            errors.put("email", "Email already registered");
        }

        // Validate biography
        if (user.getBiography() != null && user.getBiography().length() > 500) {
            errors.put("biography", "Biography cannot exceed 500 characters");
        }

        return errors;
    }

    public Map<String, String> register(User user, Part filePart) throws IOException{
        Map<String, String> errors = validate(user);
        if (errors.isEmpty()) {
        	savePicture(user, filePart);
            userRepository.save(user);
        }
        return errors;
    }
    
    public Integer getUserIdByEmail(String email) {
    	return userRepository.findUserIdByEmail(email);
    }
    
    public List<User> getRandomUsers(int limit, int excludeUserId){
    	return userRepository.findRandomUsers(limit, excludeUserId);
    }
   
 }
