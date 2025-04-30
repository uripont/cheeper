package com.webdev.cheeper.util;

import jakarta.servlet.http.HttpServletRequest;

public class OAuthUtils {
    
    /**
     * Dynamically builds a redirect URI based on the current request
     * @param request The HTTP request
     * @param path The path to redirect to
     * @return The complete redirect URI
     */
    public static String getRedirectUri(HttpServletRequest request, String path) {
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
        
        // Append context path and specified path
        url.append(contextPath).append(path);
        
        return url.toString();
    }
}