package com.webdev.cheeper.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import com.webdev.cheeper.model.Entity;
import com.webdev.cheeper.model.RoleType;
import com.webdev.cheeper.repository.EntityRepository;
import com.webdev.cheeper.repository.UserRepository;
import com.webdev.cheeper.service.EntityService;

import java.io.IOException;
import java.util.Map;


@MultipartConfig
@WebServlet("/auth/entity-form")
public class EntityForm extends HttpServlet {
 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
     // Create new Entity from form data
     Entity entity = new Entity();
     
     entity.setFullName(request.getParameter("fullName"));
     entity.setEmail(request.getParameter("email"));
     entity.setUsername(request.getParameter("username"));
     entity.setBiography(request.getParameter("biography"));
     entity.setRoleType(RoleType.ENTITY);	
     entity.setDepartment(request.getParameter("department"));
     
     Part filePart = request.getPart("picture");
     
     try (EntityRepository entityRepository = new EntityRepository();
	         UserRepository userRepository = new UserRepository()) {
	        
        EntityService studentService = new EntityService(userRepository, entityRepository);
        Map<String, String> validationErrors = studentService.register(entity, filePart);
        
        if (validationErrors.isEmpty()) {
            response.sendRedirect("main-page.html");
        } else {
            request.setAttribute("entity", entity);
            request.setAttribute("errors", validationErrors);
            request.getRequestDispatcher("entity-form.jsp").forward(request, response);
        }
	 }
 }
}