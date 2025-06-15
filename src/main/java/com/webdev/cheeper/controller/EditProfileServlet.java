package com.webdev.cheeper.controller;

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
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }
        
        User user = userOpt.get();
        String formPath;
        
        // Determine which form to use based on role type
        switch (user.getRoleType()) {
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
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid role type");
                return;
        }
        
        // Redirect to appropriate form with edit mode parameter
        response.sendRedirect(request.getContextPath() + formPath + "?mode=edit");
    }
}
