package com.webdev.cheeper.controller.views;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.webdev.cheeper.model.*;
import com.webdev.cheeper.service.*;
import com.webdev.cheeper.repository.*;

import java.io.*;
import java.util.Optional;

@WebServlet("/views/profile")
public class ProfileViewController extends HttpServlet {
    
    private StudentService studentService;
    private EntityService entityService;
    private AssociationService associationService;
    private ImageService imageService;
    private UserRepository userRepository;
    private FollowRepository followRepository;
    
    @Override
    public void init() throws ServletException {
        this.userRepository = new UserRepository();
        this.followRepository = new FollowRepository();
        this.studentService = new StudentService(userRepository, new StudentRepository());
        this.entityService = new EntityService(userRepository, new EntityRepository());
        this.associationService = new AssociationService(userRepository, new AssociationRepository());
        this.imageService = new ImageService();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userIdStr = req.getParameter("userId");
        String username = req.getParameter("username");
        User targetUser;
        User currentUser = null;
        boolean isReadOnly = false;

        // Get current user from session if available
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("email") != null) {
            String email = (String) session.getAttribute("email");
            Optional<User> currentUserOpt = userRepository.findByEmail(email);
            if (currentUserOpt.isPresent()) {
                currentUser = currentUserOpt.get();
            }
        }
        
        // Try to find user by ID first
        if (userIdStr != null && !userIdStr.isEmpty()) {
            try {
                int userId = Integer.parseInt(userIdStr);
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    return;
                }
                targetUser = userOpt.get();
                isReadOnly = currentUser == null || !targetUser.getId().equals(currentUser.getId()); // Only editable if it's your own profile
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
                return;
            }
        }
        // Then try by username
        else if (username != null && !username.isEmpty()) {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }
            targetUser = userOpt.get();
            isReadOnly = currentUser == null || !targetUser.getId().equals(currentUser.getId()); // Only editable if it's your own profile
        }
        // Finally fall back to current user's profile
        else {
            // No userId or username provided, must be logged in to view own profile
            if (currentUser == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            targetUser = currentUser;
        }

        // Get appropriate profile based on role
        Optional<? extends User> profile;
        switch (targetUser.getRoleType()) {
            case STUDENT:
                profile = studentService.getProfile(targetUser.getId());
                break;
            case ENTITY:
                profile = entityService.getProfile(targetUser.getId());
                break;
            case ASSOCIATION:
                profile = associationService.getProfile(targetUser.getId());
                break;
            default:
                profile = Optional.empty();
        }

        if (profile.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Profile not found");
            return;
        }

        User profileUser = profile.get();

        // Handle profile picture path
        String picturePath = profileUser.getPicture();
        if (picturePath == null || picturePath.trim().isEmpty()) {
            picturePath = imageService.getImagePath(null); // return default image path
            profileUser.setPicture(picturePath);
        }

        // Get follower counts
        int followersCount = followRepository.countFollowers(profileUser.getId());
        int followingCount = followRepository.countFollowing(profileUser.getId());

        // Check if current user follows this profile
        boolean isFollowing = false;
        if (currentUser != null && isReadOnly) {
            isFollowing = followRepository.isFollowing(currentUser.getId(), profileUser.getId());
        }

        // Set attributes for JSP
        req.setAttribute("profile", profileUser);
        req.setAttribute("followersCount", followersCount);
        req.setAttribute("followingCount", followingCount);
        req.setAttribute("isFollowing", isFollowing);
        req.setAttribute("readOnly", isReadOnly);
        req.setAttribute("currentUser", currentUser); // To determine if admin

        // Forward to profile view (to render the profile page)
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher("/WEB-INF/views/components/profile-view.jsp").forward(req, resp);
    }
}
