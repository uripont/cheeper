package com.webdev.cheeper.controller.views;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.webdev.cheeper.model.*;
import com.webdev.cheeper.service.*;
import com.webdev.cheeper.repository.*;

import java.io.*;
import java.util.List;
import java.util.Optional;

@WebServlet("/views/timeline")
public class TimelineViewController extends HttpServlet {
    
    private UserRepository userRepository;
    private PostRepository postRepository;
    
    @Override
    public void init() throws ServletException {
        this.userRepository = new UserRepository();
        this.postRepository = new PostRepository(); // Add this line
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = null;
        String username = req.getParameter("u"); // Optional username parameter

        // Get current user from session if available
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("email") != null) {
            String email = (String) session.getAttribute("email");
            Optional<User> currentUserOpt = userRepository.findByEmail(email);
            if (currentUserOpt.isPresent()) {
                currentUser = currentUserOpt.get();
            }
        }

        // Require authentication
        if (currentUser == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Initialize PostRepository if not done in init()
        if (postRepository == null) {
            postRepository = new PostRepository();
        }

        String timeline_type = req.getParameter("type"); // Get from request parameter
        if (timeline_type == null) {
            timeline_type = (String) session.getAttribute("type");
        }
        if (timeline_type == null) {
            timeline_type = "for-you"; // Default
        }

        List<Post> posts;
        
        try {
            // Fix string comparison - use .equals() instead of ==
            if ("for-you".equals(timeline_type)) {
                posts = postRepository.findAll();
            }
            else if ("following".equals(timeline_type)) {
                posts = postRepository.findByFollowedUsers(currentUser.getId());
            }  
            else if ("profile".equals(timeline_type)) {
                posts = postRepository.findByUserId(currentUser.getId());
            }
            else {
                posts = postRepository.findAll(); // Default to all posts
            }
            
            System.out.println("Timeline type: " + timeline_type);
            System.out.println("Current user ID: " + currentUser.getId());
            System.out.println("Posts found: " + posts.size());
            
            // Print the list of posts
            System.out.println("Posts list:");
            for (Post post : posts) {
                System.out.println("- Post ID: " + post.getId() + 
                                 ", User ID: " + post.getUserId() + 
                                 ", Content: " + post.getContent() + 
                                 ", Created: " + post.getCreatedAt());
            }
            
        } catch (Exception e) {
            System.err.println("Error loading posts: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading posts");
            return;
        }
        
        // Set attributes for JSP
        req.setAttribute("currentUser", currentUser);
        req.setAttribute("username", username);
        req.setAttribute("posts", posts);
        req.setAttribute("timeline_type", timeline_type);

        // Forward to timeline view
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher("/WEB-INF/views/components/timeline-view.jsp").forward(req, resp);
    }
}
