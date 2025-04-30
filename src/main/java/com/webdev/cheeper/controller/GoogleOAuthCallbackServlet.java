package com.webdev.cheeper.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

// Google OAuth 2.0 client library
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

// Java standard library for HTTP requests to Google endpoint
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.webdev.cheeper.repository.UserRepository; // Used to verify if the user already exists on db
import com.webdev.cheeper.util.OAuthUtils; // Utility class to build redirect URI

import org.json.JSONObject; 


@WebServlet("/auth/google-callback")
public class GoogleOAuthCallbackServlet extends HttpServlet {
    private static final String CLIENT_ID = System.getenv("GOOGLE_CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("GOOGLE_CLIENT_SECRET");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String code = request.getParameter("code");
        String redirectUri = OAuthUtils.getRedirectUri(request, "/auth/google-callback");

        try {
            HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport(); 
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            // Exchange code for access token
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    transport, jsonFactory,
                    CLIENT_ID, CLIENT_SECRET, code, redirectUri)
                    .execute();

            String accessToken = tokenResponse.getAccessToken();

            // Use HttpClient to get user info from Google API using the access token
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest requestUserInfo = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.googleapis.com/oauth2/v2/userinfo"))
                    .header("Authorization", "Bearer " + accessToken)
                    .build();
            HttpResponse<String> responseUserInfo = client.send(requestUserInfo, HttpResponse.BodyHandlers.ofString());

            // Parse the response
            JSONObject userInfo = new JSONObject(responseUserInfo.body());
            String name = userInfo.getString("name");
            String email = userInfo.getString("email");

            // Check if the user data is missing
            if (name == null || email == null) {
                response.sendRedirect("error.jsp");
                return;
            }
            
            // Store user info in the session
            request.getSession().setAttribute("name", name);
            request.getSession().setAttribute("email", email);
            
            // Redirect to appropriate page based on user existence
            try (UserRepository userRepository = new UserRepository()) {
                if(!userRepository.emailExists(email)) {
                    response.sendRedirect("Register.jsp");
                } else {
                    response.sendRedirect("Welcome.jsp");
                }
            }
            
        } catch (GeneralSecurityException | IOException | InterruptedException e ) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
  
		}
    }
}

