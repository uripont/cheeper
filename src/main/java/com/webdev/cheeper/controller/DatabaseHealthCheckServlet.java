package com.webdev.cheeper.controller;

import com.mysql.cj.jdbc.MysqlDataSource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/health")
public class DatabaseHealthCheckServlet extends HttpServlet {

    /** 
     * Fetches an env var or throws if it’s missing/empty. 
     */
    private String requireEnv(String name) throws ServletException {
        String val = System.getenv(name);
        if (val == null || val.isEmpty()) {
            throw new ServletException(
                "Required environment variable '" + name + "' is not set"
            );
        }
        return val;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 1) Grab all required vars or error out
        String host     = requireEnv("DB_HOST");
        int    port     = 3306; // Default MySQL port
        String database = requireEnv("MYSQL_DATABASE");
        String user     = requireEnv("DB_SERVER_USER");
        String pass     = requireEnv("DB_SERVER_PASS");

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            // 2) Configure the DataSource
            MysqlDataSource ds = new MysqlDataSource();
            ds.setServerName(host);
            ds.setPort(port);
            ds.setDatabaseName(database);
            ds.setUser(user);
            ds.setPassword(pass);

            // 3) Attempt to get a Connection
            try (Connection conn = ds.getConnection()) {
                out.println("<html><body style=\"font-family:sans-serif;text-align:center;margin-top:2em;\">");
                out.printf(
                  "<h1 style=\"color:green;\">OK: Connected to “%s” at “%s:%d”</h1>",
                  database, host, port
                );
                out.println("</body></html>");
            }
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().println(
              "<html><body><h1 style=\"color:red;\">DB connection failed: " +
              e.getMessage() +
              "</h1></body></html>"
            );
        }
    }
}