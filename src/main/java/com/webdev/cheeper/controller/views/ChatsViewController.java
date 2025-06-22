package com.webdev.cheeper.controller.views;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.webdev.cheeper.model.*;
import com.webdev.cheeper.service.*;
import com.webdev.cheeper.repository.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/views/chats")
public class ChatsViewController extends HttpServlet {
    
    private UserRepository userRepository;
    private FollowRepository followRepository;
    
    @Override
    public void init() throws ServletException {
        this.userRepository = new UserRepository();
        this.followRepository = new FollowRepository();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = null;

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

        // Set attributes for JSP
        req.setAttribute("currentUser", currentUser);

        // Forward to appropriate view
        resp.setContentType("text/html;charset=UTF-8");
        if (req.getParameter("component") != null && req.getParameter("component").equals("private-chat-users")) {
            // Get mutual followers (users who follow each other)
            List<User> mutualUsers = followRepository.getMutualFollowers(currentUser.getId());
            
            // Handle search query if present
            String searchQuery = req.getParameter("q");
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String searchLower = searchQuery.toLowerCase();
                mutualUsers = mutualUsers.stream()
                    .filter(user -> user.getUsername().toLowerCase().contains(searchLower) || 
                                  user.getFullName().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
                req.setAttribute("searchQuery", searchQuery);
            }
            
            // Set users list for JSP
            req.setAttribute("users", mutualUsers);
            
            req.getRequestDispatcher("/WEB-INF/views/components/private-chat-users-view.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/WEB-INF/views/components/chats-view.jsp").forward(req, resp);
        }
    }
}
