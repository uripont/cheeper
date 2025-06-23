package com.webdev.cheeper.controller.post;

import com.webdev.cheeper.model.User;
import com.webdev.cheeper.repository.LikeRepository;
import com.webdev.cheeper.repository.UserRepository;
import com.webdev.cheeper.service.LikeService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/like")
public class LikeController extends HttpServlet {
    private LikeService likeService;
    private UserRepository userRepository;

    @Override
    public void init() throws ServletException {
        this.userRepository = new UserRepository();
        LikeRepository likeRepository = new LikeRepository();
        this.likeService = new LikeService(likeRepository);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String postIdParam = req.getParameter("postId");
        HttpSession session = req.getSession(false);

        // Check authentication
        if (session == null || session.getAttribute("email") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Validate postId parameter
        if (postIdParam == null || postIdParam.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Post ID is required");
            return;
        }

        try {
            int postId = Integer.parseInt(postIdParam);
            String userEmail = (String) session.getAttribute("email");

            // Get user ID from email
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }

            int userId = userOpt.get().getId();

            // Toggle like using the service
            likeService.toggleLike(userId, postId);
            
            // Return success response with like status
            boolean isLiked = likeService.isLikedByUser(postId, userId);
            int likeCount = likeService.getLikesForPost(postId).size();
            
            resp.setContentType("application/json");
            resp.getWriter().write(String.format(
                "{\"success\": true, \"liked\": %b, \"likeCount\": %d}", 
                isLiked, likeCount
            ));
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid post ID format");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while processing like");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String postIdParam = req.getParameter("postId");
        HttpSession session = req.getSession(false);

        // Check authentication
        if (session == null || session.getAttribute("email") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Validate postId parameter
        if (postIdParam == null || postIdParam.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Post ID is required");
            return;
        }

        try {
            int postId = Integer.parseInt(postIdParam);
            String userEmail = (String) session.getAttribute("email");

            // Get user ID from email
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }

            int userId = userOpt.get().getId();

            // Get like status
            boolean isLiked = likeService.isLikedByUser(postId, userId);
            int likeCount = likeService.getLikesForPost(postId).size();
            
            resp.setContentType("application/json");
            resp.getWriter().write(String.format(
                "{\"liked\": %b, \"likeCount\": %d}", 
                isLiked, likeCount
            ));
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid post ID format");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while getting like status");
        }
    }
}