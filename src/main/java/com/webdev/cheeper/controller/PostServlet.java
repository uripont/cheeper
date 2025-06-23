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



    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[PostServlet DELETE] Request received");
        
        String postIdParam = request.getParameter("postId");
        System.out.println("[PostServlet DELETE] PostId param: " + postIdParam);
        
        if (postIdParam == null || postIdParam.trim().isEmpty()) {
            System.out.println("[PostServlet DELETE] ERROR: Post ID is null or empty");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().print("{\"success\": false, \"message\": \"Post ID required\"}");
            return;
        }
        
        int postId;
        try {
            postId = Integer.parseInt(postIdParam);
            System.out.println("[PostServlet DELETE] Parsed Post ID: " + postId);
        } catch (NumberFormatException e) {
            System.out.println("[PostServlet DELETE] ERROR: Invalid post ID format: " + postIdParam);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().print("{\"success\": false, \"message\": \"Invalid post ID\"}");
            return;
        }
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            System.out.println("[PostServlet DELETE] ERROR: No valid session found");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().print("{\"success\": false, \"message\": \"Unauthorized\"}");
            return;
        }
        
        String email = (String) session.getAttribute("email");
        System.out.println("[PostServlet DELETE] Email from session: " + email);
        
        try (UserRepository userRepository = new UserRepository()) {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                System.out.println("[PostServlet DELETE] ERROR: User not found for email: " + email);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("application/json");
                response.getWriter().print("{\"success\": false, \"message\": \"User not found\"}");
                return;
            }
            
            int userId = userOpt.get().getId();
            System.out.println("[PostServlet DELETE] Current user ID: " + userId);
            
            // Check if user owns the post
            Post post = postService.getPostById(postId);
            if (post == null) {
                System.out.println("[PostServlet DELETE] ERROR: Post not found with ID: " + postId);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("application/json");
                response.getWriter().print("{\"success\": false, \"message\": \"Post not found\"}");
                return;
            }
            
            System.out.println("[PostServlet DELETE] Post found - Post User ID: " + post.getUserId() + ", Current User ID: " + userId + ", Current User Role: " + userOpt.get().getRoleType());
            
            // Allow deletion if current user is the post owner OR if current user is an ENTITY
            if (post.getUserId() != userId && userOpt.get().getRoleType() != com.webdev.cheeper.model.RoleType.ENTITY) {
                System.out.println("[PostServlet DELETE] ERROR: User " + userId + " not authorized to delete post owned by " + post.getUserId() + " and is not an ENTITY.");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().print("{\"success\": false, \"message\": \"Not authorized to delete this post\"}");
                return;
            }
            
            System.out.println("[PostServlet DELETE] Authorization passed. Attempting to delete post ID: " + postId);
            
            // Delete the post
            postService.deletePost(postId);
            System.out.println("[PostServlet DELETE] Post deleted successfully");
            
            response.setContentType("application/json");
            response.getWriter().print("{\"success\": true}");
            
        } catch (Exception e) {
            System.err.println("[PostServlet DELETE] ERROR: Exception occurred - " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().print("{\"success\": false, \"message\": \"Server error\"}");
        }
    }
}
