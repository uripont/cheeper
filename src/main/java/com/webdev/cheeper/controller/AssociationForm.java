package com.webdev.cheeper.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import com.webdev.cheeper.model.Association;
import com.webdev.cheeper.model.RoleType;
import com.webdev.cheeper.model.VerfStatus;
import com.webdev.cheeper.repository.AssociationRepository;
import com.webdev.cheeper.repository.UserRepository;
import com.webdev.cheeper.service.AssociationService;

import java.io.IOException;
import java.util.Map;

/**
 * Servlet implementation class AssociationForm
 */

@MultipartConfig
@WebServlet("/auth/association-form")
public class AssociationForm extends HttpServlet {
    private static final long serialVersionUID = 1L;

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

		Association association = new Association();

        association.setFullName(request.getParameter("fullName"));
        association.setEmail(request.getParameter("email"));
        association.setUsername(request.getParameter("username"));
        association.setBiography(request.getParameter("biography"));
        association.setRoleType(RoleType.ASSOCIATION);

        // Set default verification status (PENDING)
        association.setVerificationStatus(VerfStatus.PENDING);
        association.setVerificationDate(new java.sql.Timestamp(System.currentTimeMillis()));

        Part filePart = request.getPart("picture");

        try (AssociationRepository associationRepository = new AssociationRepository();
             UserRepository userRepository = new UserRepository()) {

            AssociationService associationService = new AssociationService(userRepository, associationRepository);
            Map<String, String> validationErrors = associationService.register(association, filePart);

            if (validationErrors.isEmpty()) {
                response.sendRedirect("main-page.html");
            } else {
                request.setAttribute("association", association);
                request.setAttribute("errors", validationErrors);
                request.getRequestDispatcher("association-form.jsp").forward(request, response);
            }
        }
    }
}
