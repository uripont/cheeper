package com.webdev.cheeper.service;

import jakarta.servlet.http.Part;

import com.webdev.cheeper.model.Entity;
import com.webdev.cheeper.model.User;
import com.webdev.cheeper.repository.EntityRepository;
import com.webdev.cheeper.repository.UserRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityService {
    private final UserRepository userRepository;
    private final EntityRepository entityRepository;
    private final ImageService imageService;

    public EntityService(UserRepository userRepository, EntityRepository entityRepository) {
        this.userRepository = userRepository;
        this.entityRepository = entityRepository;
        this.imageService = new ImageService();
    }

    public Optional<Entity> getProfile(int id) {
        return entityRepository.getProfile(id);
    }

    public Map<String, String> register(Entity entity, Part picture) {
        Map<String, String> errors = new HashMap<>();
        
        try {
            // Validate email uniqueness for new users
            if (userRepository.findByEmail(entity.getEmail()).isPresent()) {
                errors.put("email", "Email already exists");
            }
            
            // Validate username uniqueness for new users
            if (userRepository.findByUsername(entity.getUsername()).isPresent()) {
                errors.put("username", "Username already taken");
            }
            
            if (!errors.isEmpty()) {
                return errors;
            }
            
            // Handle profile picture if provided
            if (picture != null && picture.getSize() > 0) {
                String picturePath = imageService.storeImage(picture, entity.getUsername());
                entity.setPicture(picturePath);
            }
            
            // Save entity
            entityRepository.save(entity);
        } catch (IOException e) {
            errors.put("picture", "Failed to save profile picture");
            e.printStackTrace();
        }
        
        return errors;
    }
    
    public Map<String, String> update(Entity entity, Part picture) {
        Map<String, String> errors = new HashMap<>();
        
        // Get existing entity to check for email/username changes
        Optional<Entity> existingEntity = entityRepository.getProfile(entity.getId());
        if (existingEntity.isEmpty()) {
            errors.put("general", "Entity not found");
            return errors;
        }
        
        Entity current = existingEntity.get();
        
        // Only validate email uniqueness if it changed
        if (!current.getEmail().equals(entity.getEmail()) && 
            userRepository.findByEmail(entity.getEmail()).isPresent()) {
            errors.put("email", "Email already exists");
        }
        
        // Only validate username uniqueness if it changed
        if (!current.getUsername().equals(entity.getUsername()) && 
            userRepository.findByUsername(entity.getUsername()).isPresent()) {
            errors.put("username", "Username already taken");
        }
        
        if (!errors.isEmpty()) {
            return errors;
        }
        
        try {
            // Handle profile picture if provided
            if (picture != null && picture.getSize() > 0) {
                String picturePath = imageService.storeImage(picture, entity.getUsername());
                entity.setPicture(picturePath);
            } else {
                // Keep existing picture if no new one provided
                entity.setPicture(current.getPicture());
            }
            
            // Update entity
            entityRepository.update(entity);
        } catch (IOException e) {
            errors.put("picture", "Failed to save profile picture");
            e.printStackTrace();
        }
        
        return errors;
    }
}
