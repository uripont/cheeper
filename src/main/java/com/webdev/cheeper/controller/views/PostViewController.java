package com.webdev.cheeper.controller.views;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.webdev.cheeper.model.*;
import com.webdev.cheeper.repository.*;

import java.io.*;
import java.util.*;

@WebServlet("/views/post")
public class PostViewController extends HttpServlet {

    private UserRepository userRepository;
    private PostRepository postRepository;

    @Override
    public void init() throws ServletException {
        this.userRepository = new UserRepository();
        this.postRepository = new PostRepository();
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
            Optional<User> currentUserOpt = userRepository.findByEmail(email);
            if (currentUserOpt.isPresent()) {
                currentUser = currentUserOpt.get();
                System.out.println("[PostView] Authenticated as user ID: " + currentUser.getId());
            }
        }

        if (currentUser == null) {
            System.err.println("[PostView] Unauthorized access attempt.");
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Post ID required
        if (postIdParam == null || postIdParam.trim().isEmpty()) {
            System.err.println("[PostView] No post ID provided.");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Post ID is required");
            return;
        }

        int postId;
        try {
            postId = Integer.parseInt(postIdParam);
        } catch (NumberFormatException e) {
            System.err.println("[PostView] Invalid post ID format: " + postIdParam);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid post ID");
            return;
        }

        // Fetch main post
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            System.err.println("[PostView] Post not found with ID: " + postId);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Post not found");
            return;
        }
        Post post = postOpt.get();

        System.out.println("[PostView] Loaded post ID: " + post.getId() +
                           ", User ID: " + post.getUserId() +
                           ", Content: " + post.getContent());

        // Fetch replies (source_id = current post's ID)
        List<Post> replies = postRepository.findBySourceId(postId);
        System.out.println("[PostView] Replies found: " + replies.size());

        for (Post reply : replies) {
            System.out.println("- Reply ID: " + reply.getId() +
                               ", User ID: " + reply.getUserId() +
                               ", Content: " + reply.getContent() +
                               ", Created: " + reply.getCreatedAt());
        }

        // Set attributes for JSP
        req.setAttribute("currentUser", currentUser);
        req.setAttribute("post", post);
        req.setAttribute("replies", replies);

        // Forward to JSP
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher("/WEB-INF/views/components/post-view.jsp").forward(req, resp);
    }
}
