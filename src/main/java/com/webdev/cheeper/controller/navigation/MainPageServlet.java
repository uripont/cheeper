package com.webdev.cheeper.controller.navigation;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {
    "/", "/home", "/explore", "/create", "/chats", "/profile"
})
public class MainPageServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // Check if user is authenticated
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            // No valid session, redirect to auth
            resp.sendRedirect(req.getContextPath() + "/auth");
            return;
        }
        
        // Get the path and remove leading slash
        String path = req.getServletPath();
        String view = path.equals("/") ? "home" : path.substring(1);
        
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
