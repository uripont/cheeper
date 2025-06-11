package com.webdev.cheeper.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import jakarta.servlet.http.*;

import com.webdev.cheeper.model.*;
import com.webdev.cheeper.repository.FollowRepository;
import com.webdev.cheeper.repository.UserRepository;
import com.webdev.cheeper.service.FollowService;
import com.webdev.cheeper.service.UserService;

import java.util.List;
import java.util.Set;

@WebServlet({"/follow", "/unfollow", "/followers", "/following", "/profile-counts"})
public class FollowController extends HttpServlet {

    private FollowRepository followRepository;
    private FollowService followService;

    @Override
    public void init() throws ServletException {
        followRepository = new FollowRepository();
        followService = new FollowService(followRepository);
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
        if (req.getServletPath().equals("/unfollow")) {
            unfollowUser(req,resp);
        } else if (req.getServletPath().equals("/follow")) {
        	followUser(req,resp);
        }
    }

    private void followUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	try (UserRepository userRepo = new UserRepository()) {
                Integer followerId = getCurrentUserId(req, userRepo);
                if (followerId == null) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                int followingId = Integer.parseInt(req.getParameter("followingId"));

                if (followService.follow(followerId, followingId)) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("Followed successfully");
                } else {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to follow");
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
            }
    }
    
    private void unfollowUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (UserRepository userRepo = new UserRepository()) {

            Integer followerId = getCurrentUserId(req, userRepo);
            if (followerId == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            int followingId = Integer.parseInt(req.getParameter("followingId"));

            if (followService.unfollow(followerId, followingId)) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Unfollowed successfully");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to unfollow");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
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
        resp.getWriter().write(String.format("{\"followers\":%d,\"following\":%d}", followers, following));
    }

    private String buildProfileHtml(User user, boolean isFollowing) {
        String imagePath = user.getPicture() != null && !user.getPicture().isEmpty()
            ? "/local-images/" + user.getPicture()
            : "/local-images/default.png";

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
                "<img src='%s' alt='%s' class='clickable-profile' data-username='%s' " +
                "onerror=\"this.onerror=null;this.src='/local-images/default.png';\">" +
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
