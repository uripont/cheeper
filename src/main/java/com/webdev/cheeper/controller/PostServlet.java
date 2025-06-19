package com.webdev.cheeper.controller;

import com.webdev.cheeper.model.Post;
import com.webdev.cheeper.model.User;
import com.webdev.cheeper.repository.PostRepository;
import com.webdev.cheeper.repository.UserRepository;
import com.webdev.cheeper.service.ImageService;
import com.webdev.cheeper.service.PostService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Optional;

@MultipartConfig
@WebServlet("/post")
public class PostServlet extends HttpServlet {
    private PostService postService;
    private ImageService imageService;

    @Override
    public void init() throws ServletException {
        try {
            this.postService = new PostService(new PostRepository());
            this.imageService = new ImageService();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize services", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String content = request.getParameter("content");
        String sourceIdParam = request.getParameter("source_id");
        Integer sourceId = null;

        System.out.println("[PostServlet] Content received: " + content);

        if (sourceIdParam != null && !sourceIdParam.trim().isEmpty()) {
            try {
                sourceId = Integer.parseInt(sourceIdParam);
                System.out.println("[PostServlet] This is a reply to post ID: " + sourceId);
            } catch (NumberFormatException e) {
                System.err.println("[PostServlet] Invalid source_id: " + sourceIdParam);
            }
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = (String) session.getAttribute("email");
        System.out.println("[PostServlet] Email: " + email);

        int userId;
        String username;
        try (UserRepository userRepository = new UserRepository()) {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print("User not found");
                return;
            }
            userId = userOpt.get().getId();
            username = userOpt.get().getUsername();
        }

        System.out.println("[PostServlet] User ID: " + userId);
        
        // Handle image upload using ImageService
        Part imagePart = request.getPart("image");
        String imagePath = null;

        System.out.println("[PostServlet] Image part received: " + (imagePart != null ? "YES" : "NO"));
        if (imagePart != null) {
            System.out.println("[PostServlet] Image part size: " + imagePart.getSize() + " bytes");
            System.out.println("[PostServlet] Image part filename: " + imagePart.getSubmittedFileName());
            System.out.println("[PostServlet] Image part content type: " + imagePart.getContentType());
        }

        if (imagePart != null && imagePart.getSize() > 0) {
            try {
                // Validate image first
                if (!imageService.validateImage(imagePart)) {
                    System.err.println("[PostServlet] Image validation failed");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().print("Invalid image file");
                    return;
                }

                System.out.println("[PostServlet] Image validation passed");

                // Store image using ImageService with unique identifier for posts
                String postImageName = username + "_post_" + System.currentTimeMillis();
                System.out.println("[PostServlet] Storing image with name: " + postImageName);
                
                imagePath = imageService.storePostImage(imagePart, postImageName);
                System.out.println("[PostServlet] Image stored successfully: " + imagePath);
                
            } catch (IOException e) {
                System.err.println("[PostServlet] Error storing image: " + e.getMessage());
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print("Failed to store image");
                return;
            }
        } else {
            System.out.println("[PostServlet] No image to process");
        }

        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setImage(imagePath);
        post.setSourceId(sourceId); 
        Timestamp now = new Timestamp(System.currentTimeMillis());
        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        System.out.println("[PostServlet] Post created with image: " + (imagePath != null ? imagePath : "NO IMAGE"));

        try {
            postService.createPost(post);
            System.out.println("[PostServlet] Post saved successfully to database");

            // Return success response for AJAX
            response.setContentType("application/json");
            response.getWriter().print("{\"success\": true, \"postId\": " + post.getId() + "}");

        } catch (Exception e) {
            System.err.println("[PostServlet] Error creating post: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().print("{\"error\": \"Post creation failed: " + e.getMessage() + "\"}");
        }
    }
}