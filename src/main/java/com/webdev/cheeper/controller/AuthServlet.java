package com.webdev.cheeper.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.webdev.cheeper.model.User; // JavaBean we populate with the form data (no validation here)
import com.webdev.cheeper.service.UserService; // Service layer that handles business logic and validation
import com.webdev.cheeper.repository.UserRepository; // Repository layer that interacts with the database to store the user

import java.io.IOException;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;


@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L; // Serial version UID for serialization
	   
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("WEB-INF/views/auth/login-with-google.jsp").forward(request, response);
	}

	// TODO: Move onboarding/registration forms after OAuth login to separate logic according to detected user type
	/* 
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    User user = new User();
	    
	    try {
	        // First populate all simple fields (no validation here, just a POJO)
	        BeanUtils.populate(user, request.getParameterMap());
	       
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    try (UserRepository userRepository = new UserRepository()) {
	        UserService userService = new UserService(userRepository);

			// Validate and register the user
	        Map<String, String> errors = userService.register(user); 
	        
			// TODO: Handle the errors and forward to the appropriate JSP page (on errors, sign up or sign in)
	        /* if (errors.isEmpty()) {
	            request.setAttribute("user", user);
	            request.getRequestDispatcher("Login.jsp").forward(request, response);
	        } else {
	            request.setAttribute("user", user);
	            request.setAttribute("errors", errors);
	            request.getRequestDispatcher("Register.jsp").forward(request, response);
	        } 
	   } 
	    
	 }*/
}
