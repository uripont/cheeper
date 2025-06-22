package com.webdev.cheeper.controller.views;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.webdev.cheeper.model.*;
import com.webdev.cheeper.repository.*;
import com.webdev.cheeper.service.*;

import java.io.*;
import java.util.*;

@WebServlet("/views/edit-post")
public class EditPostViewController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String postIdParam = req.getParameter("postId");
        
        if (postIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Post ID required");
            return;
        }
        
        int postId;
        try {
            postId = Integer.parseInt(postIdParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid post ID");
            return;
        }
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        try (PostRepository postRepository = new PostRepository();
             UserRepository userRepository = new UserRepository()) {
            
            PostService postService = new PostService(postRepository);
            
            Post post = postService.getPostById(postId);
            if (post == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Post not found");
                return;
            }
            
            // Verify ownership
            String email = (String) session.getAttribute("email");
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty() || userOpt.get().getId() != post.getUserId()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized to edit this post");
                return;
            }
            
            req.setAttribute("post", post);
            resp.setContentType("text/html;charset=UTF-8");
            req.getRequestDispatcher("/WEB-INF/views/components/edit-post-view.jsp").forward(req, resp);
            
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}