package com.webdev.cheeper.controller;

import com.webdev.cheeper.util.DBManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/health")
public class DatabaseHealthCheckServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = resp.getWriter(); DBManager db = new DBManager()) {
            String host = System.getenv("DB_HOST");
            String database = System.getenv("MYSQL_DATABASE");
            
            out.println("<html><body style=\"font-family:sans-serif;text-align:center;margin-top:2em;\">");
            out.printf(
                "<h1 style=\"color:green;\">OK: Connected to \"%s\" at \"%s\"</h1>",
                database, host
            );
            out.println("<p>Connection pool is working correctly</p>");
            out.println("</body></html>");
            
        } catch (SQLException | IllegalStateException e) {
            resp.setStatus(500);
            resp.getWriter().println(
                "<html><body><h1 style=\"color:red;\">DB connection failed: " +
                e.getMessage() +
                "</h1></body></html>"
            );
        }
    }
}
