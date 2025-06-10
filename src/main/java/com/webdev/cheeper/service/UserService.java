package com.webdev.cheeper.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webdev.cheeper.model.User;
import com.webdev.cheeper.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean emailExists(String email) {
        return userRepository.emailExists(email);
    }

    public Map<String, String> validateBaseUser(User user) {
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

        // Validate birthdate
        if (user.getBirthdate() == null) {
            errors.put("birthdate", "Birthdate is required");
        } else if (user.getBirthdate().toLocalDate().isAfter(LocalDate.now())) {
            errors.put("birthdate", "Birthdate cannot be in the future");
        }

        // Validate biography (optional)
        if (user.getBiography() != null && user.getBiography().length() > 500) {
            errors.put("biography", "Biography cannot exceed 500 characters");
        }

        return errors;
    }

    public Map<String, String> register(User user) {
        Map<String, String> errors = validateBaseUser(user);
        if (errors.isEmpty()) {
            userRepository.save(user);
        }
        return errors;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
