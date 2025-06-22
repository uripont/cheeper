package com.webdev.cheeper.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import com.webdev.cheeper.model.Association;
import com.webdev.cheeper.model.RoleType;
import com.webdev.cheeper.model.User;
import com.webdev.cheeper.model.VerfStatus;
import com.webdev.cheeper.repository.AssociationRepository;
import com.webdev.cheeper.repository.UserRepository;
import com.webdev.cheeper.service.AssociationService;
import com.webdev.cheeper.service.UserService;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Servlet implementation class AssociationForm
 */

@MultipartConfig
@WebServlet("/auth/association-form")
public class AssociationForm extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserRepository userRepository;
    private UserService userService;
    private AssociationRepository associationRepository;
    private AssociationService associationService;

    @Override
    public void init() throws ServletException {
        this.userRepository = new UserRepository();
        this.userService = new UserService(userRepository);
        this.associationRepository = new AssociationRepository();
        this.associationService = new AssociationService(userRepository, associationRepository);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String mode = request.getParameter("mode");
        
        if ("edit".equals(mode)) {
            // Get current user from session
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("email") == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            
            String email = (String) session.getAttribute("email");
            Optional<User> userOpt = userService.getUserByEmail(email);
            
            if (userOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }
            
            // Get full entity profile
            Optional<Association> associationOpt = associationService.getProfile(userOpt.get().getId());
            
            if (associationOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Association profile not found");
                return;
            }
            
            // Pre-populate form with entity data
            Association association = associationOpt.get();
            request.setAttribute("association", association);
            request.setAttribute("mode", "edit");
        }
        
        // Forward to form view
        request.getRequestDispatcher("/WEB-INF/views/onboarding/association-form.jsp").forward(request, response);
    }
    
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String mode = request.getParameter("mode");
		Association association = new Association();

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("email") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = (String) session.getAttribute("email");

        if ("edit".equals(mode)) {
            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            User user = userOpt.get();
            int userId = user.getId();
            association.setId(userId);
        }

        association.setFullName(request.getParameter("fullName"));
        association.setEmail(request.getParameter("email"));
        association.setUsername(request.getParameter("username"));
        association.setBiography(request.getParameter("biography"));
        association.setRoleType(RoleType.ASSOCIATION);

        // Set default verification status (PENDING)
        association.setVerificationStatus(VerfStatus.PENDING);
        association.setVerificationDate(new java.sql.Timestamp(System.currentTimeMillis()));

        Part filePart = request.getPart("picture");

        Map<String, String> validationErrors;
        
        if ("edit".equals(mode)) {
            validationErrors = associationService.update(association, filePart);
        } else {
            validationErrors = associationService.register(association, filePart);
        }

        if (validationErrors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
        } else {
            request.setAttribute("association", association);
            request.setAttribute("errors", validationErrors);
            request.getRequestDispatcher("/WEB-INF/views/onboarding/association-form.jsp").forward(request, response);
        }
 
    }
}
