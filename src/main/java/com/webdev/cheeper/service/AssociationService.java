package com.webdev.cheeper.service;

import jakarta.servlet.http.Part;
import com.webdev.cheeper.model.Association;
import com.webdev.cheeper.repository.AssociationRepository;
import com.webdev.cheeper.repository.UserRepository;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class AssociationService extends UserService {
    private final AssociationRepository associationRepository;

    public AssociationService(UserRepository userRepository, AssociationRepository associationRepository) {
        super(userRepository);
        this.associationRepository = associationRepository;
    }

    public Map<String, String> validate(Association association, String mode) {
        Map<String, String> errors = super.validate(association, mode);

        // Validate verification status
        if (association.getVerificationStatus() == null) {
            errors.put("verificationStatus", "Verification status is required.");
        }

        return errors;
    }

    public Map<String, String> register(Association association, Part filePart) throws IOException {
        Map<String, String> errors = validate(association, "register");

        if (errors.isEmpty()) {
            try {
                associationRepository.save(association); // This calls super.save(association) and then saves association details
                savePicture(association, filePart); // Now user.getId() is available
                associationRepository.update(association); // Update association with the picture filename and other association-specific data
            } catch (Exception e) {
                errors.put("system", "Registration failed: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return errors;
    }

    public Map<String,String> update(Association association, Part filePart) {
        Map<String,String> errors = validate(association, "edit");
        
        if (errors.isEmpty()) {
        	try {
                updatePicture(association, filePart); // Use the new updatePicture method
                associationRepository.update(association);
            } catch (Exception e) {
                errors.put("system", "Update failed: " + e.getMessage());
                e.printStackTrace(); 
            }
        }
        return errors;
    }
    
    public Optional<Association> getProfile(int userId) {
        return associationRepository.getProfile(userId);
    }
}
