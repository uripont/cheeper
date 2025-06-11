package com.webdev.cheeper.controller;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.Gson;

import com.webdev.cheeper.repository.UserRepository;
import com.webdev.cheeper.service.UserService;
import com.webdev.cheeper.model.User;


@WebServlet("/suggested-profiles")
public class SuggestedProfilesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int numRandUsers = 3;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

    	System.out.println("I am inside");
        Integer currentUserId = getCurrentUserId(request);
        if (currentUserId == null) {
        	System.out.println("aborting...");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try (UserRepository userRepo = new UserRepository()) {
        	UserService userServ = new UserService(userRepo);
        	System.out.println("Suggesting users");
            List<User> suggestedProfiles = userServ.getRandomUsers(numRandUsers, currentUserId);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new Gson().toJson(suggestedProfiles));
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading suggested profiles");
        }
    }

  
    private Integer getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
        	System.out.println("No session");
        	return null;
        }

        String email = (String) session.getAttribute("email");
        if (email == null) return null;

        try (UserRepository userRepo = new UserRepository()) {
        	UserService userServ = new UserService(userRepo);
        	System.out.println("Id" + userServ.getUserIdByEmail(email));
            return userServ.getUserIdByEmail(email);
        }
    }

}
