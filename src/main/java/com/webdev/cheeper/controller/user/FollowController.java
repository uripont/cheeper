package com.webdev.cheeper.controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import jakarta.servlet.http.*;

import com.webdev.cheeper.model.*;
import com.webdev.cheeper.repository.FollowRepository;
import com.webdev.cheeper.repository.UserRepository;
import com.webdev.cheeper.service.FollowService;
import com.webdev.cheeper.service.UserService;
import com.webdev.cheeper.service.ImageService;

import java.util.List;
import java.util.Set;

@WebServlet({"/follow", "/unfollow", "/followers", "/following", "/profile-counts"})
public class FollowController extends HttpServlet {

    private FollowRepository followRepository;
    private FollowService followService;
    private ImageService imageService;

    @Override
    public void init() throws ServletException {
        followRepository = new FollowRepository();
        followService = new FollowService(followRepository);
        imageService = new ImageService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        String userIdStr = req.getParameter("userId");
        if (userIdStr == null || userIdStr.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or empty userId parameter");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid userId parameter");
            return;
        }

        switch (path) {
            case "/followers":
                showFollowers(req, resp, userId);
                break;
            case "/following":
                showFollowing(req, resp, userId);
                break;
            case "/profile-counts":
                sendProfileCounts(resp, userId);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        try (UserRepository userRepo = new UserRepository()) {
            // Get current user ID
            Integer followerId = getCurrentUserId(req, userRepo);
            if (followerId == null) {
                sendJsonResponse(resp, false, "User not authenticated", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // Get target user ID
            String followingIdStr = req.getParameter("followingId");
            if (followingIdStr == null || followingIdStr.isEmpty()) {
                sendJsonResponse(resp, false, "Missing followingId parameter", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int followingId;
            try {
                followingId = Integer.parseInt(followingIdStr);
            } catch (NumberFormatException e) {
                sendJsonResponse(resp, false, "Invalid followingId format", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Prevent self-following
            if (followerId == followingId) {
                sendJsonResponse(resp, false, "Cannot follow/unfollow yourself", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            boolean success;
            String message;

            // Handle follow/unfollow based on path
            if (req.getServletPath().equals("/unfollow")) {
                success = followService.unfollow(followerId, followingId);
                message = success ? "Successfully unfollowed user" : "Failed to unfollow user";
            } else {
                success = followService.follow(followerId, followingId);
                message = success ? "Successfully followed user" : "Failed to follow user";
            }

            sendJsonResponse(resp, success, message, success ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(resp, false, "An internal error occurred", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendJsonResponse(HttpServletResponse resp, boolean success, String message, int status) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(status);
        String jsonResponse = String.format("{\"success\": %b, \"message\": \"%s\"}", success, message);
        resp.getWriter().write(jsonResponse);
    }

    
    // Auxiliar function
    private Integer getCurrentUserId(HttpServletRequest request, UserRepository userRepo) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;

        String email = (String) session.getAttribute("email");
        if (email == null) return null;
        UserService userServ = new UserService(userRepo);
        return userServ.getUserIdByEmail(email);
    }
    
    private void showFollowers(HttpServletRequest req, HttpServletResponse resp, int profileUserId) throws IOException {
    	List<User> followers = followService.getFollowers(profileUserId);
        resp.setContentType("text/html");
        
        try (UserRepository userRepo = new UserRepository()) {
            int currentUserId = getCurrentUserId(req, userRepo);
            Set<Integer> currentUserFollowingIds = followService.getFollowingIds(currentUserId);
            System.out.println("Current user is following these IDs: " + currentUserFollowingIds);
            StringBuilder html = new StringBuilder();
            html.append("<div class='profiles-list'>");

            for (User follower : followers) {
                boolean isFollowing = currentUserFollowingIds.contains(follower.getId());
            
                html.append(buildProfileHtml(follower, isFollowing));
            }
            
            html.append("</div>");
            resp.getWriter().write(html.toString());
        }
    }

    private void showFollowing(HttpServletRequest req, HttpServletResponse resp, int profileUserId) throws IOException {
        List<User> following = followService.getFollowing(profileUserId);
        resp.setContentType("text/html");

        try (UserRepository userRepo = new UserRepository()) {
            int currentUserId = getCurrentUserId(req, userRepo);
            System.out.println("Current" + currentUserId);
            Set<Integer> currentUserFollowingIds = followService.getFollowingIds(currentUserId);
            System.out.println("Current user is following these IDs: " + currentUserFollowingIds);
            StringBuilder html = new StringBuilder();
            html.append("<div class='profiles-list'>");

            for (User followedUser : following) {
                boolean isFollowing = currentUserFollowingIds.contains(followedUser.getId());
                html.append(buildProfileHtml(followedUser, isFollowing));
            }
            
            html.append("</div>");
            resp.getWriter().write(html.toString());
        }
    }


    private void sendProfileCounts(HttpServletResponse resp, int userId) throws IOException {
        int followers = followService.countFollowers(userId);
        int following = followService.countFollowing(userId);
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(String.format("{\"followers\":%d,\"following\":%d}", followers, following));
    }

    private String buildProfileHtml(User user, boolean isFollowing) {
        String imagePath = imageService.getImagePath(user.getPicture());
        String role = user.getRoleType() != null ? user.getRoleType().toString() : "";
        String followButtonHtml = "";

        if (isFollowing) {
            followButtonHtml = "<button class='follow-standard-btn'>Unfollow</button>";
        } else {
            followButtonHtml = "<button class='follow-standard-btn'>Follow</button>";
        }

        // The div with clickable-profile class on image and info div, with data-username attribute
        return String.format(
            "<div class='suggested-profile' data-user-id='%d'>" +
                "<img src='%s' alt='%s' class='clickable-profile' data-username='%s'>" +
                "<div class='suggested-profile-info clickable-profile' data-username='%s'>" +
                    "<div class='suggested-profile-name'>%s</div>" +
                    "<div class='suggested-profile-username'>@%s</div>" +
                    "<div class='suggested-profile-role'>%s</div>" +
                "</div>" +
                "%s" +
            "</div>",
            user.getId(),
            imagePath,
            user.getFullName(),
            user.getUsername(),
            user.getUsername(),
            user.getFullName(),
            user.getUsername(),
            role,
            followButtonHtml
        );
    }




}
