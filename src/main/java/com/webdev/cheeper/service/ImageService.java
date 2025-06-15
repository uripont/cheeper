package com.webdev.cheeper.service;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.HashSet;


public class ImageService {
    private final String storageBasePath;
    private final String serveBasePath;
    private final long maxFileSize;
    private final Set<String> allowedTypes;
    private static final String DEFAULT_IMAGE = "default.png";
    
    public ImageService() {
        // Read from environment variables with defaults
        this.storageBasePath = System.getenv("IMAGE_STORAGE_PATH") != null ? 
            System.getenv("IMAGE_STORAGE_PATH") : "/var/lib/cheeper/images";
        this.serveBasePath = System.getenv("IMAGE_SERVE_PATH") != null ?
            System.getenv("IMAGE_SERVE_PATH") : "/local-images";
        this.maxFileSize = System.getenv("IMAGE_MAX_SIZE") != null ?
            Long.parseLong(System.getenv("IMAGE_MAX_SIZE")) : 5242880L; // 5MB default
        
        // Initialize allowed types
        this.allowedTypes = new HashSet<>();
        String configuredTypes = System.getenv("IMAGE_ALLOWED_TYPES");
        if (configuredTypes != null) {
            for (String type : configuredTypes.split(",")) {
                allowedTypes.add(type.trim());
            }
        } else {
            // Default allowed types
            allowedTypes.add("image/jpeg");
            allowedTypes.add("image/png");
            allowedTypes.add("image/gif");
        }
        ensureStorageExists();
    }
    
    public String storeImage(Part file, String username) throws IOException {
        if (file == null || file.getSize() == 0) {
            return DEFAULT_IMAGE;
        }
        
        if (!validateImage(file)) {
            throw new IllegalArgumentException("Invalid image file");
        }
        
        String originalName = Paths.get(file.getSubmittedFileName()).getFileName().toString();
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalName.substring(dotIndex);
        }
        
        String newFileName = username + extension;
        Path targetPath = Paths.get(storageBasePath, "profiles", newFileName);
        
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            return newFileName;
        }
    }
    
    public void deleteImage(String filename) {
        if (filename == null || filename.equals(DEFAULT_IMAGE)) {
            return;
        }
        
        try {
            Path imagePath = Paths.get(storageBasePath, "profiles", filename);
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getImagePath(String filename) {
        if (filename == null || filename.trim().isEmpty() || filename.equals(DEFAULT_IMAGE)) {
            return serveBasePath + "/default.png";
        }
        return serveBasePath + "/profile/" + filename;
    }
    
    public boolean validateImage(Part file) {
        if (file == null) return false;
        if (file.getSize() > maxFileSize) return false;
        
        // Check type
        String contentType = file.getContentType();
        return contentType != null && allowedTypes.contains(contentType.toLowerCase());
    }
    
    public void ensureStorageExists() {
        createDirectoryIfNotExists(storageBasePath);
        createDirectoryIfNotExists(storageBasePath + "/profiles");
    }
    
    private void createDirectoryIfNotExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
