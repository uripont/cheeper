package com.webdev.cheeper.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

import com.webdev.cheeper.model.RoleType; // Enum to define user roles based on email domain
import com.webdev.cheeper.repository.UserRepository; // Used to verify if the user already exists on db
import com.webdev.cheeper.service.UserService;
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
            String name = userInfo.optString("name");
            String email = userInfo.optString("email");

            if (name == null || email == null || name.isEmpty() || email.isEmpty()) {
                request.getRequestDispatcher("/WEB-INF/views/auth/auth-error.jsp").forward(request, response);
                return;
            }

            // Assign role
            RoleType role = assignRoleFromEmail(email);
            
            // Store user info in session
            HttpSession session = request.getSession();
            session.setAttribute("name", name);
            session.setAttribute("email", email);
            session.setAttribute("role", role);
            
            // Redirect to the appropriate form based on the role
            try (UserRepository userRepository = new UserRepository();) {
            	UserService userService = new UserService(userRepository);
                if (!userService.emailExists(email, null)) {
                    if (role == RoleType.STUDENT) {
                        request.getRequestDispatcher("/WEB-INF/views/onboarding/student-form.jsp").forward(request, response);
                    } else if (role == RoleType.ENTITY) {
                        request.getRequestDispatcher("/WEB-INF/views/onboarding/entity-form.jsp").forward(request, response);
                    } else {
                        request.getRequestDispatcher("/WEB-INF/views/onboarding/association-form.jsp").forward(request, response);
                    }
                } else {
                    // User exists, retrieve full user object and store in session
                    com.webdev.cheeper.model.User user = userService.getUserByEmail(email).orElse(null);
                    if (user != null) {
                        session.setAttribute("currentUser", user);
                    }
                    response.sendRedirect(request.getContextPath() + "/home");
                }
            }

        } catch (GeneralSecurityException | IOException | InterruptedException e) {
            e.printStackTrace();
            request.getRequestDispatcher("/WEB-INF/views/auth/auth-error.jsp").forward(request, response);
        }
    }

    public RoleType assignRoleFromEmail(String email) {
        if (email.endsWith("@estudiant.upf.edu")) {
            return RoleType.STUDENT;
        } else if (email.endsWith("@upf.edu")) { //Can be changed for testing
        	return RoleType.ENTITY;
        } else {
        	return RoleType.ENTITY;
        	//return RoleType.ASSOCIATION;
        }
    }
}
