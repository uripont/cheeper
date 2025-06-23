package com.webdev.cheeper.controller.onboarding;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.webdev.cheeper.repository.UserRepository;
import com.webdev.cheeper.service.UserService;

import java.io.IOException;

@WebServlet("/check-username")
public class UsernameCheckServlet extends HttpServlet {
    private UserRepository userRepository = new UserRepository();
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        this.userRepository = new UserRepository();
        this.userService = new UserService(userRepository);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String userId = request.getParameter("userId");
        
        boolean exists;
        if (userId != null && !userId.isEmpty()) {
            exists = userService.usernameExists(username, Integer.parseInt(userId));
        } else {
            exists = userService.usernameExists(username);
        }
        
        response.setContentType("application/json");
        response.getWriter().write("{\"exists\": " + exists + "}");
    }
}
