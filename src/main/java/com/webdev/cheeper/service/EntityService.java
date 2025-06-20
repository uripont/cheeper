package com.webdev.cheeper.service;

import java.util.Map;
import java.util.Optional;
import jakarta.servlet.http.Part;
import java.io.IOException;

import com.webdev.cheeper.model.Entity;
import com.webdev.cheeper.model.Student;
import com.webdev.cheeper.repository.EntityRepository;
import com.webdev.cheeper.repository.UserRepository;

public class EntityService extends UserService {
    private final EntityRepository entityRepository;

    public EntityService(UserRepository userRepository, EntityRepository entityRepository) {
        super(userRepository);
        this.entityRepository = entityRepository;
    }

    public Map<String, String> validate(Entity entity, String mode) {
        Map<String, String> errors = super.validate(entity, mode);

        // Validate department
        if (entity.getDepartment() == null || entity.getDepartment().trim().isEmpty()) {
            errors.put("department", "Department is required");
        } else if (entity.getDepartment().length() > 100) {
            errors.put("department", "Department name cannot exceed 100 characters");
        }
    
        return errors;
    }

    public Map<String, String> register(Entity entity, Part filePart) throws IOException {
        Map<String, String> errors = validate(entity, "register");
        
        if (errors.isEmpty()) {
            try {
                savePicture(entity, filePart);
                entityRepository.save(entity);
            } catch (Exception e) {
                errors.put("system", "Registration failed: " + e.getMessage());
                e.printStackTrace(); 
            }
        }
        return errors;
    }

    public Map<String,String> update(Entity entity, Part filePart) {
        Map<String,String> errors = validate(entity, "edit");
        
        if (errors.isEmpty()) {
        	try {
                savePicture(entity, filePart);
                entityRepository.update(entity);
            } catch (Exception e) {
                errors.put("system", "Update failed: " + e.getMessage());
                e.printStackTrace(); 
            }
        }
        return errors;
    }
    
    public Optional<Entity> getProfile(int userId) {
        return entityRepository.getProfile(userId);
    }
}