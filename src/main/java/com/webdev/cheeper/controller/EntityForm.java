package com.webdev.cheeper.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import com.webdev.cheeper.model.RoleType;
import com.webdev.cheeper.model.User;
import com.webdev.cheeper.model.Entity;
import com.webdev.cheeper.repository.EntityRepository;
import com.webdev.cheeper.repository.UserRepository;
import com.webdev.cheeper.service.EntityService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@MultipartConfig
@WebServlet("/auth/entity-form")
public class EntityForm extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserRepository userRepository;
    private EntityRepository entityRepository;
    private EntityService entityService;

    @Override
    public void init() throws ServletException {
        this.userRepository = new UserRepository();
        this.entityRepository = new EntityRepository();
        this.entityService = new EntityService(userRepository, entityRepository);
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
            Optional<User> userOpt = userRepository.findByEmail(email);
            
            if (userOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }
            
            // Get full entity profile
            Optional<Entity> entityOpt = entityService.getProfile(userOpt.get().getId());
            
            if (entityOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Entity profile not found");
                return;
            }
            
            // Pre-populate form with entity data
            Entity entity = entityOpt.get();
            request.setAttribute("entity", entity);
            request.setAttribute("mode", "edit");
        }
        
        // Forward to form view
        request.getRequestDispatcher("/WEB-INF/views/onboarding/entity-form.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String mode = request.getParameter("mode");
        Entity entity;
        
        if ("edit".equals(mode)) {
            // Get existing entity for update
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("email") == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            
            String email = (String) session.getAttribute("email");
            Optional<Entity> existingEntity = entityService.getProfile(
                userRepository.findByEmail(email).get().getId()
            );
            
            if (existingEntity.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Entity profile not found");
                return;
            }
            
            entity = existingEntity.get();
        } else {
            // Create new entity
            entity = new Entity();
        }
        
        Map<String, String> errors = new HashMap<>();
   
        try {
            // Manually decode and populate fields
            entity.setFullName(request.getParameter("fullName"));
            entity.setEmail(request.getParameter("email"));
            entity.setUsername(request.getParameter("username"));
            entity.setBiography(request.getParameter("biography"));
            entity.setDepartment(request.getParameter("department"));
            entity.setRoleType(RoleType.ENTITY);
            
            Part filePart = request.getPart("picture");
            Map<String, String> validationErrors;
            
            if ("edit".equals(mode)) {
                validationErrors = entityService.update(entity, filePart);
            } else {
                validationErrors = entityService.register(entity, filePart);
            }
            
            if (validationErrors.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/app/home");
            } else {
                request.setAttribute("entity", entity);
                request.setAttribute("errors", validationErrors);
                request.setAttribute("mode", mode);
                request.getRequestDispatcher("/WEB-INF/views/onboarding/entity-form.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            request.setAttribute("entity", entity);
            request.setAttribute("error", "Registration failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/onboarding/entity-form.jsp").forward(request, response);
        }
    }
}
