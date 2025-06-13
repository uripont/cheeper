package com.webdev.cheeper.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/views/*")
public class ViewsController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Remove leading slash
        String view = pathInfo.substring(1);
        String jspPath;

        switch (view) {
            case "feed":
                jspPath = "/WEB-INF/views/components/feed-view.jsp";
                break;
            case "timeline":
                jspPath = "/WEB-INF/views/components/timeline-view.jsp";
                break;
            case "users":
                jspPath = "/WEB-INF/views/components/users-list-view.jsp";
                break;
            case "create":
                jspPath = "/WEB-INF/views/components/create-post-view.jsp";
                break;
            case "post":
                jspPath = "/WEB-INF/views/components/post-view.jsp";
                break;
            case "profile":
                jspPath = "/WEB-INF/views/components/profile-view.jsp";
                break;
            case "chats":
                jspPath = "/WEB-INF/views/components/chats-view.jsp";
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
        }

        // Set content type for all view responses
        resp.setContentType("text/html;charset=UTF-8");
        
        // Forward to appropriate JSP
        req.getRequestDispatcher(jspPath).forward(req, resp);
    }
}
