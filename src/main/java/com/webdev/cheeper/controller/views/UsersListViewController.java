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

@WebServlet("/views/users")
public class UsersListViewController extends HttpServlet {
    
    private UserRepository userRepository;
    private FollowRepository followRepository;
    private ImageService imageService;
    
    @Override
    public void init() throws ServletException {
        this.userRepository = new UserRepository();
        this.followRepository = new FollowRepository();
        this.imageService = new ImageService();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String context = req.getParameter("context");
        String searchQuery = req.getParameter("q");
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

        // Default to suggestions if no context provided
        if (context == null) {
            context = "suggestions";
        }

        // Convert to lowercase for case-insensitive comparison
        String contextLower = context.toLowerCase();

        // Require authentication only for suggestions and chats
        if (currentUser == null && 
            (contextLower.equals("suggestions") || 
             contextLower.equals("suggested") || 
             contextLower.equals("chats"))) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        List<User> users;
        String contextTitle;
        String userIdStr = req.getParameter("userId");
        
        if ((contextLower.equals("followers") || contextLower.equals("following")) && userIdStr != null) {
            int userId = Integer.parseInt(userIdStr);
            if (contextLower.equals("followers")) {
                users = followRepository.getFollowers(userId);
                contextTitle = "Followers";
            } else {
                users = followRepository.getFollowing(userId);
                contextTitle = "Following";
            }
        } else if (contextLower.equals("suggestions") || contextLower.equals("suggested") || contextLower.equals("chats")) {
            users = userRepository.findRandomUsers(10, currentUser.getId());
            contextTitle = contextLower.equals("chats") ? "Chat Users" : "Suggested Users";
        } else if (contextLower.equals("search")) {
            // For now, show all users if no query, later implement search
            users = userRepository.findAll();
            contextTitle = "Search Users";
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid context");
            return;
        }

        // Process users list to add follow status and profile pictures
        for (User user : users) {
            // Set profile picture path
            String picturePath = user.getPicture();
            if (picturePath == null || picturePath.trim().isEmpty()) {
                picturePath = imageService.getImagePath(null); // get default image path
                user.setPicture(picturePath);
            }

            // Set following status if user is logged in
            if (currentUser != null) {
                boolean isFollowing = followRepository.isFollowing(currentUser.getId(), user.getId());
                user.setFollowed(isFollowing);
            }
        }

        // Set attributes for JSP
        req.setAttribute("users", users);
        req.setAttribute("context", contextTitle);
        req.setAttribute("searchQuery", searchQuery);
        req.setAttribute("currentUser", currentUser);

        // Forward to users list view
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher("/WEB-INF/views/components/users-list-view.jsp").forward(req, resp);
    }
}
