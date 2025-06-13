package com.webdev.cheeper.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/app/*")
public class MainPageServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // Get the path after /app/
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            path = "/home"; // Default route
        }
        
        // Remove leading slash
        String view = path.substring(1);
        
        // Set the current view for the layout
        req.setAttribute("view", view);
        
        // Forward to the main layout
        req.getRequestDispatcher("/WEB-INF/views/layouts/main-layout.jsp")
           .forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // For now, redirect POST requests to GET
        resp.sendRedirect(req.getRequestURI());
    }
}
