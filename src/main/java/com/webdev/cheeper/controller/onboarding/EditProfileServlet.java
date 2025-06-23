package com.webdev.cheeper.controller.onboarding;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.webdev.cheeper.model.User;
import com.webdev.cheeper.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/edit-profile")
public class EditProfileServlet extends HttpServlet {
    
    private UserRepository userRepository;
    
    @Override
    public void init() throws ServletException {
        this.userRepository = new UserRepository();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get current user from session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        String email = (String) session.getAttribute("email");
        Optional<User> currentUserOpt = userRepository.findByEmail(email);
        
        if (currentUserOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Current user not found");
            return;
        }
        
        User currentUser = currentUserOpt.get();
        User targetUser = currentUser; // Default to current user's profile
        
        String userIdParam = request.getParameter("userId");
        if (userIdParam != null && !userIdParam.isEmpty()) {
            try {
                int userId = Integer.parseInt(userIdParam);
                // If current user is an ENTITY, they can edit any profile
                if (currentUser.getRoleType() == com.webdev.cheeper.model.RoleType.ENTITY) {
                    Optional<User> targetUserOpt = userRepository.findById(userId);
                    if (targetUserOpt.isEmpty()) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Target user not found");
                        return;
                    }
                    targetUser = targetUserOpt.get();
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format");
                return;
            }
        }
        
        String formPath;
        // Determine which form to use based on the target user's role type
        switch (targetUser.getRoleType()) {
            case STUDENT:
                formPath = "/auth/student-form";
                break;
            case ENTITY:
                formPath = "/auth/entity-form";
                break;
            case ASSOCIATION:
                formPath = "/auth/association-form";
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid role type for target user");
                return;
        }
        
        // Redirect to appropriate form with edit mode and target user ID parameters
        response.sendRedirect(request.getContextPath() + formPath + "?mode=edit&userId=" + targetUser.getId());
    }
}
