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

        String action = request.getParameter("action");
        if ("edit".equals(action)) {
            handleEditPost(request, response);
            return;
        }

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



    //Function to handle editing a post
    private void handleEditPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[PostServlet EDIT] Request received");
        request.setCharacterEncoding("UTF-8");
    
        String postIdParam = request.getParameter("postId");
        String content = request.getParameter("content");
        String removeImage = request.getParameter("removeImage");
        
        System.out.println("[PostServlet EDIT] PostId param: " + postIdParam);
        System.out.println("[PostServlet EDIT] Content: " + (content != null ? content.substring(0, Math.min(content.length(), 50)) + "..." : "null"));
        System.out.println("[PostServlet EDIT] Remove image flag: " + removeImage);
        
        if (postIdParam == null || content == null || content.trim().isEmpty()) {
            System.out.println("[PostServlet EDIT] ERROR: Missing required fields - postId: " + (postIdParam != null) + ", content: " + (content != null && !content.trim().isEmpty()));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().print("{\"success\": false, \"message\": \"Missing required fields\"}");
            return;
        }
        
        int postId;
        try {
            postId = Integer.parseInt(postIdParam);
            System.out.println("[PostServlet EDIT] Parsed Post ID: " + postId);
        } catch (NumberFormatException e) {
            System.out.println("[PostServlet EDIT] ERROR: Invalid post ID format: " + postIdParam);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().print("{\"success\": false, \"message\": \"Invalid post ID\"}");
            return;
        }
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            System.out.println("[PostServlet EDIT] ERROR: No valid session found");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().print("{\"success\": false, \"message\": \"Unauthorized\"}");
            return;
        }
        
        String email = (String) session.getAttribute("email");
        System.out.println("[PostServlet EDIT] Email from session: " + email);
        
        try {
            // Check for image part
            Part imagePart = null;
            try {
                imagePart = request.getPart("image");
                System.out.println("[PostServlet EDIT] Image part: " + (imagePart != null ? "present, size: " + imagePart.getSize() : "null"));
            } catch (Exception e) {
                System.out.println("[PostServlet EDIT] No image part found or error reading: " + e.getMessage());
            }
            
            try (UserRepository userRepository = new UserRepository()) {
                Optional<User> userOpt = userRepository.findByEmail(email);
                if (userOpt.isEmpty()) {
                    System.out.println("[PostServlet EDIT] ERROR: User not found for email: " + email);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setContentType("application/json");
                    response.getWriter().print("{\"success\": false, \"message\": \"User not found\"}");
                    return;
                }
                
                int userId = userOpt.get().getId();
                System.out.println("[PostServlet EDIT] Current user ID: " + userId);
                
                // Get existing post and verify ownership
                Post existingPost = postService.getPostById(postId);
                if (existingPost == null) {
                    System.out.println("[PostServlet EDIT] ERROR: Post not found with ID: " + postId);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setContentType("application/json");
                    response.getWriter().print("{\"success\": false, \"message\": \"Post not found\"}");
                    return;
                }
                
                System.out.println("[PostServlet EDIT] Post found - Post User ID: " + existingPost.getUserId() + ", Current User ID: " + userId);
                
                if (existingPost.getUserId() != userId) {
                    System.out.println("[PostServlet EDIT] ERROR: User " + userId + " not authorized to edit post owned by " + existingPost.getUserId());
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().print("{\"success\": false, \"message\": \"Not authorized to edit this post\"}");
                    return;
                }
                
                System.out.println("[PostServlet EDIT] Authorization passed. Updating post content...");
                
                // Update post content
                existingPost.setContent(content);
                System.out.println("[PostServlet EDIT] Content updated");
                
                // Handle image update
                if ("true".equals(removeImage)) {
                    System.out.println("[PostServlet EDIT] Removing current image: " + existingPost.getImage());
                    existingPost.setImage(null);
                } else if (imagePart != null && imagePart.getSize() > 0) {
                    System.out.println("[PostServlet EDIT] Processing new image upload...");
                    try {
                        // Generate unique filename for the post
                        String baseName = "post_" + postId + "_" + System.currentTimeMillis();
                        String fileName = imageService.storePostImage(imagePart, baseName); // ✅ Método correcto
                        System.out.println("[PostServlet EDIT] New image saved as: " + fileName);
                        existingPost.setImage(fileName);
                    } catch (Exception e) {
                        System.err.println("[PostServlet EDIT] ERROR saving image: " + e.getMessage());
                        e.printStackTrace();
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.setContentType("application/json");
                        response.getWriter().print("{\"success\": false, \"message\": \"Error saving image\"}");
                        return;
                    }
                } else {
                    System.out.println("[PostServlet EDIT] Keeping existing image: " + existingPost.getImage());
                }
                
                System.out.println("[PostServlet EDIT] Calling postService.updatePost()...");
                postService.updatePost(existingPost);
                System.out.println("[PostServlet EDIT] Post updated successfully");
                
                response.setContentType("application/json");
                response.getWriter().print("{\"success\": true}");
                
            } catch (Exception e) {
                System.err.println("[PostServlet EDIT] ERROR: Exception occurred - " + e.getMessage());
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.getWriter().print("{\"success\": false, \"message\": \"Server error: " + e.getMessage() + "\"}");
            }
        } catch (Exception e) {
            System.err.println("[PostServlet EDIT] ERROR: Outer exception - " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().print("{\"success\": false, \"message\": \"Server error\"}");
        }
    }    
    
}

