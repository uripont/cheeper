package com.webdev.cheeper.controller.views;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.webdev.cheeper.model.*;
import com.webdev.cheeper.service.*;
import com.webdev.cheeper.repository.*;

import java.io.*;
import java.util.Optional;

@WebServlet("/views/post")
public class PostViewController extends HttpServlet {
    
    private UserRepository userRepository;
    
    @Override
    public void init() throws ServletException {
        this.userRepository = new UserRepository();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = null;
        String postId = req.getParameter("id");

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

        // Require post ID
        if (postId == null || postId.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Post ID is required");
            return;
        }

        // TODO: Fetch post details when post model and repository are implemented
        
        // Set attributes for JSP
        req.setAttribute("currentUser", currentUser);
        req.setAttribute("postId", postId);

        // Forward to post view
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher("/WEB-INF/views/components/post-view.jsp").forward(req, resp);
    }
}
