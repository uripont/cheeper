package com.webdev.cheeper.controller.onboarding;

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
import com.webdev.cheeper.model.VerificationStatus;
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
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
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

            // Get full association profile for the target user
            Optional<Association> associationOpt = associationService.getProfile(targetUser.getId());
            
            if (associationOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Association profile not found for target user");
                return;
            }
            
            // Pre-populate form with association data
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
        String name = (String) session.getAttribute("name");

        if ("edit".equals(mode)) {
            String userIdParam = request.getParameter("userId");
            if (userIdParam != null && !userIdParam.isEmpty()) {
                try {
                    int userId = Integer.parseInt(userIdParam);
                    association.setId(userId);
                    // Fetch existing user to get current picture
                    Optional<User> existingUserOpt = userRepository.findById(userId);
                    if (existingUserOpt.isPresent()) {
                        association.setPicture(existingUserOpt.get().getPicture());
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Target user not found for update");
                        return;
                    }
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format");
                    return;
                }
            } else {
                // Fallback to current user if no userId is provided in edit mode
                Optional<User> userOpt = userService.getUserByEmail(email);
                if (userOpt.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                association.setId(userOpt.get().getId());
                association.setPicture(userOpt.get().getPicture()); // Set existing picture for current user
            }
        } else { // Register mode
            // For registration mode, ensure picture is not null if no file is uploaded
            association.setPicture("default.png"); 
            // For new registrations, get email and full name from session
            association.setFullName(name);
            association.setEmail(email);
        }

        // FullName and Email are now handled above for register mode
        if (!"register".equals(mode)) { // Only get from parameter if not register mode
            association.setFullName(request.getParameter("fullName"));
            association.setEmail(request.getParameter("email"));
        }
        association.setUsername(request.getParameter("username"));
        association.setBiography(request.getParameter("biography"));
        association.setRoleType(RoleType.ASSOCIATION);

        // Set default verification status
        association.setVerificationStatus(VerificationStatus.PENDING);
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
