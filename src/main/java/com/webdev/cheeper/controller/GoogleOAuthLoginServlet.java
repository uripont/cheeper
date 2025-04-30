package com.webdev.cheeper.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import io.github.cdimascio.dotenv.Dotenv;

import com.webdev.cheeper.util.OAuthUtils;


@WebServlet("/auth/google-login")
public class GoogleOAuthLoginServlet extends HttpServlet {

    private static final Dotenv dotenv = Dotenv.load(); 
    private static final String CLIENT_ID = dotenv.get("GOOGLE_CLIENT_ID");
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirectUri = OAuthUtils.getRedirectUri(request, "/auth/google-callback");
        
        String url = "https://accounts.google.com/o/oauth2/auth" // Link to be redirected to Google OAuth login screen
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&response_type=code"
                + "&scope=email%20profile"
                + "&access_type=online"
                + "&prompt=login"; // Forces fresh login

        response.sendRedirect(url);
    }
}