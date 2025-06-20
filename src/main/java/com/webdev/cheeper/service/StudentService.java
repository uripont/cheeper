package com.webdev.cheeper.service;

import jakarta.servlet.http.Part;

import com.webdev.cheeper.model.Student;
import com.webdev.cheeper.model.User;
import com.webdev.cheeper.repository.StudentRepository;
import com.webdev.cheeper.repository.UserRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StudentService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ImageService imageService;

    public StudentService(UserRepository userRepository, StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.imageService = new ImageService();
    }
    
    public Optional<Student> getProfile(int id) {
        return studentRepository.getProfile(id);
    }
    
    public Map<String, String> register(Student student, Part picture) {
        Map<String, String> errors = new HashMap<>();
        
        try {
        
        // Validate email uniqueness for new users
        if (userRepository.findByEmail(student.getEmail()).isPresent()) {
            errors.put("email", "Email already exists");
        }
        
        // Validate username uniqueness for new users
        if (userRepository.findByUsername(student.getUsername()).isPresent()) {
            errors.put("username", "Username already taken");
        }
        
        if (!errors.isEmpty()) {
            return errors;
        }
        
            // Handle profile picture if provided
            if (picture != null && picture.getSize() > 0) {
                String picturePath = imageService.storeImage(picture, student.getUsername());
                student.setPicture(picturePath);
            }
            
            // Save student
            studentRepository.save(student);
        } catch (IOException e) {
            errors.put("picture", "Failed to save profile picture");
            e.printStackTrace();
        }
        return errors;
    }
    
    public Map<String, String> update(Student student, Part picture) {
        Map<String, String> errors = new HashMap<>();
        
        // Get existing student to check for email/username changes
        Optional<Student> existingStudent = studentRepository.getProfile(student.getId());
        if (existingStudent.isEmpty()) {
            errors.put("general", "Student not found");
            return errors;
        }
        
        Student current = existingStudent.get();
        
        // Only validate email uniqueness if it changed
        if (!current.getEmail().equals(student.getEmail()) && 
            userRepository.findByEmail(student.getEmail()).isPresent()) {
            errors.put("email", "Email already exists");
        }
        
        // Only validate username uniqueness if it changed
        if (!current.getUsername().equals(student.getUsername()) && 
            userRepository.findByUsername(student.getUsername()).isPresent()) {
            errors.put("username", "Username already taken");
        }
        
        if (!errors.isEmpty()) {
            return errors;
        }
        
        try {
            // Handle profile picture if provided
            if (picture != null && picture.getSize() > 0) {
                String picturePath = imageService.storeImage(picture, student.getUsername());
                student.setPicture(picturePath);
            } else {
                // Keep existing picture if no new one provided
                student.setPicture(current.getPicture());
            }
            
            // Update student
            studentRepository.update(student);
        } catch (IOException e) {
            errors.put("picture", "Failed to save profile picture");
            e.printStackTrace();
        }
        
        return errors;
    }
}
