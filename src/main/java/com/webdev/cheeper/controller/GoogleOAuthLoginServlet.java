package com.webdev.cheeper.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/auth/google-login")
public class GoogleOAuthLoginServlet extends HttpServlet {
    private static final String CLIENT_ID = System.getenv("GOOGLE_CLIENT_ID");
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Dynamically build the redirect URI based on the current request
        String redirectUri = getRedirectUri(request);
        
        String url = "https://accounts.google.com/o/oauth2/auth" // Link to be redirected to Google OAuth login screen
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&response_type=code"
                + "&scope=email%20profile"
                + "&access_type=online"
                + "&prompt=login"; // Forces fresh login

        response.sendRedirect(url);
    }

    private String getRedirectUri(HttpServletRequest request) {
        // Get the base URL (protocol, server name, port)
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        
        // Build the base URL
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);
        
        // Append port if it's not the default port for the protocol
        if ((scheme.equals("http") && serverPort != 80) || 
            (scheme.equals("https") && serverPort != 443)) {
            url.append(":").append(serverPort);
        }
        
        // Append context path and callback servlet path
        url.append(contextPath).append("/auth/google-callback");
        
        return url.toString();
    }
}