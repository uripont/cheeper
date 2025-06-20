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

        String contextLower = context.toLowerCase();
        
        // Initialize UserService
        UserService userService = new UserService(userRepository);
        


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

        try {
            switch (contextLower) {
                case "search":
                    if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                        Integer excludeId = currentUser != null ? currentUser.getId() : null;
                        users = userService.searchUsers(searchQuery, 20, excludeId);
                        contextTitle = "Search Results for \"" + searchQuery + "\"";
                    } else {
                        // Show popular/suggested users when no search query
                        users = currentUser != null ? 
                            userService.getRecommendedUsers(10, currentUser.getId()) :
                            userRepository.findAll();
                        contextTitle = "Search Users";
                    }
                    break;
                    
                case "suggestions": //delete this
                case "suggested":
                    if (currentUser == null) {
                        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                    users = userService.getRecommendedUsers(10, currentUser.getId());
                    contextTitle = "Suggested Users";
                    break;
                    
                case "chats":
                    if (currentUser == null) {
                        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                    // TODO: Implement chat users (following users)
                    users = userService.getRecommendedUsers(10, currentUser.getId());
                    contextTitle = "Chat Users";
                    break;

                case "followers":
                case "following":
                    if (userIdStr == null) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing userId for followers/following context");
                        return;
                    }

                    int userId = Integer.parseInt(userIdStr);
                    if (contextLower.equals("followers")) {
                        users = followRepository.getFollowers(userId);
                        contextTitle = "Followers";
                    } else {
                        users = followRepository.getFollowing(userId);
                        contextTitle = "Following";
                    }
                    break;
                
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid context");
                    return;
            }

            // Process users list to add follow status and profile pictures
            for (User user : users) {
                // Set profile picture path
                String picturePath = user.getPicture();
                if (picturePath == null || picturePath.trim().isEmpty()) {
                    picturePath = imageService.getImagePath(null);
                    user.setPicture(picturePath);
                } else {
                    user.setPicture(imageService.getImagePath(picturePath));
                }

                // Set following status if user is logged in
                if (currentUser != null) {
                    boolean isFollowing = followRepository.isFollowing(currentUser.getId(), user.getId());
                    user.setFollowed(isFollowing);
                }
            }

        } catch (Exception e) {
            System.err.println("Error searching users: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error searching users");
            return;
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
