package com.webdev.cheeper.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import jakarta.servlet.http.Part;

import com.webdev.cheeper.model.Student;
import com.webdev.cheeper.model.User;
import com.webdev.cheeper.repository.StudentRepository;
import com.webdev.cheeper.repository.UserRepository;

public class StudentService extends UserService {
    private final StudentRepository studentRepository;

    public StudentService(UserRepository userRepository, StudentRepository studentRepository) {
        super(userRepository);
        this.studentRepository = studentRepository;
    }

    
    public Map<String, String> validate(Student student) {
        Map<String, String> errors = super.validate(student);

        // Validate birthdate
        if (student.getBirthdate() == null) {
            errors.put("birthdate", "Birthdate is required");
        } else {
            LocalDate birthDate = student.getBirthdate().toLocalDate();
            LocalDate minAgeDate = LocalDate.now().minusYears(13);
            if (birthDate.isAfter(minAgeDate)) {
                errors.put("birthdate", "You must be at least 13 years old");
            }
        }

        // Validate social links
        if (student.getSocialLinks() != null) {
            student.getSocialLinks().forEach((platform, url) -> {
                if (platform == null || platform.trim().isEmpty()) {
                    errors.put("socialLinks", "Platform name is required");
                } else if (!platform.matches("^[a-z0-9]+$")) {
                    errors.put("socialLinks", "Platform name must contain only lowercase letters and numbers");
                }
                
                if (url == null || url.trim().isEmpty()) {
                    errors.put("socialLinks", "URL is required");
                } else if (!url.startsWith("https://www.")) {
                    errors.put("socialLinks", "URL must start with https://www.");
                }
            });
        }

        // Validate degrees
        if (student.getDegrees() != null) {
            student.getDegrees().forEach((degreeType, field) -> {
                if (degreeType == null || degreeType.trim().isEmpty()) {
                    errors.put("degrees", "Degree type is required");
                } else if (degreeType.length() > 10) {
                    errors.put("degrees", "Degree type should be abbreviated (e.g., BSc, PhD)");
                }
                
                if (field == null || field.trim().isEmpty()) {
                    errors.put("degrees", "Field of study is required");
                } else if (field.length() > 100) {
                    errors.put("degrees", "Field of study cannot exceed 100 characters");
                }
            });
        }

        // Validate enrolled subjects
        if (student.getEnrolledSubjects() != null) {
            student.getEnrolledSubjects().forEach((code, name) -> {
                if (code == null || code.trim().isEmpty()) {
                    errors.put("subjects", "Subject code is required");
                } else if (!code.matches("^\\d{5}$")) {
                    errors.put("subjects", "Subject code must be exactly 5 digits");
                }
                
                if (name == null || name.trim().isEmpty()) {
                    errors.put("subjects", "Subject name is required");
                } else if (name.length() > 100) {
                    errors.put("subjects", "Subject name cannot exceed 100 characters");
                }
            });
        }

        return errors;
    }

    public Map<String, String> register(Student student, Part filePart) throws IOException {
        Map<String, String> errors = validate(student);
        
        if (errors.isEmpty()) {
            try {
                savePicture(student, filePart);
                studentRepository.save(student);
            } catch (Exception e) {
                errors.put("system", "Registration failed: " + e.getMessage());
                e.printStackTrace(); 
            }
        }
        return errors;
    }
    
    public Optional<Student> getProfile(int userId) {
        return studentRepository.getProfile(userId);
    }

}