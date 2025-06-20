package com.webdev.cheeper.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import com.webdev.cheeper.service.ImageService;

@WebServlet(urlPatterns = {"/local-images/*"})
public class ImageServlet extends HttpServlet {
    private ImageService imageService;

    @Override
    public void init() throws ServletException {
        this.imageService = new ImageService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String imagePath = pathInfo.substring(1);
        File file;

        // Handle default image requests (both direct and via profile path)
        if (imagePath.equals("default.png") || imagePath.equals("profile/default.png")) {
            String defaultImagePath = getServletContext().getRealPath("/static/images/default.png");
            file = new File(defaultImagePath);

        } else if (imagePath.startsWith("posts/")) {
            // Get post images from the storage directory
            String storageBasePath = System.getenv("IMAGE_STORAGE_PATH") != null ? 
                System.getenv("IMAGE_STORAGE_PATH") : "/var/lib/cheeper/images";
            file = new File(storageBasePath, imagePath);
            
            // Security check for post images

        }
        // Handle profile pictures
        else if (imagePath.startsWith("profile/")) {
            String filename = imagePath.substring("profile/".length());
            // Get user uploaded images from the storage directory
            String storageBasePath = System.getenv("IMAGE_STORAGE_PATH") != null ? 
                System.getenv("IMAGE_STORAGE_PATH") : "/var/lib/cheeper/images";
            file = new File(new File(storageBasePath, "profiles"), filename);
            
            // Security check for user uploaded images

            if (!file.getCanonicalPath().startsWith(new File(storageBasePath).getCanonicalPath())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

        } else {
            // Get user uploaded images from the storage directory
            String storageBasePath = System.getenv("IMAGE_STORAGE_PATH") != null ? 
                System.getenv("IMAGE_STORAGE_PATH") : "/var/lib/cheeper/images";
            file = new File(storageBasePath, imagePath);
            
            // Security check for user uploaded images
            if (!file.getCanonicalPath().startsWith(new File(storageBasePath).getCanonicalPath())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        if (!file.exists() || !file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Set content type based on file extension
        String contentType = getServletContext().getMimeType(file.getName());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        response.setContentType(contentType);

        // Stream the file content
        Files.copy(file.toPath(), response.getOutputStream());
    }
}
