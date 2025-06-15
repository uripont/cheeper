package com.webdev.cheeper.controller.views;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.webdev.cheeper.model.*;
import com.webdev.cheeper.repository.*;
import com.webdev.cheeper.service.*;

import java.io.*;
import java.util.*;

@WebServlet("/views/post")
public class PostViewController extends HttpServlet {

    private UserRepository userRepository;
    private PostService postService;
    private LikeService likeService;

    @Override
    public void init() throws ServletException {
        this.userRepository = new UserRepository();
        PostRepository postRepository = new PostRepository();
        this.postService = new PostService(postRepository);
        LikeRepository likeRepository = new LikeRepository();
        this.likeService = new LikeService(likeRepository);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = null;
        String postIdParam = req.getParameter("id");

        System.out.println("[PostView] Requested post ID: " + postIdParam);

        // Get current user from session
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("email") != null) {
            String email = (String) session.getAttribute("email");
            try {
                Optional<User> currentUserOpt = userRepository.findByEmail(email);
                if (currentUserOpt.isPresent()) {
                    currentUser = currentUserOpt.get();
                }
            } catch (Exception e) {
                System.err.println("Error finding user: " + e.getMessage());
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        }

        if (currentUser == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Post ID required
        if (postIdParam == null || postIdParam.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Post ID is required");
            return;
        }

        int postId;
        try {
            postId = Integer.parseInt(postIdParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid post ID");
            return;
        }

        try {
            // Fetch main post using service
            Post post = postService.getPostById(postId);
            if (post == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Post not found");
                return;
            }
            
            // Get post author information
            Optional<User> postAuthorOpt = userRepository.findById(post.getUserId());
            User postAuthor = postAuthorOpt.orElse(null);
            
            // Get like information for this post
            boolean isLikedByUser = likeService.isLikedByUser(postId, currentUser.getId());
            int likeCount = likeService.getLikesForPost(postId).size();

            // Set attributes
            req.setAttribute("post", post);
            req.setAttribute("postAuthor", postAuthor);
            req.setAttribute("postId", postId);
            req.setAttribute("currentUser", currentUser);
            req.setAttribute("isLikedByUser", isLikedByUser);
            req.setAttribute("likeCount", likeCount);

            resp.setContentType("text/html;charset=UTF-8");
            req.getRequestDispatcher("/WEB-INF/views/components/post-view.jsp").forward(req, resp);
            
        } catch (Exception e) {
            System.err.println("Error loading post: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading post");
        }
    }
}