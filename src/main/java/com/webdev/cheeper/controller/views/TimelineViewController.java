package com.webdev.cheeper.controller.views;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.webdev.cheeper.model.*;
import com.webdev.cheeper.service.*;
import com.webdev.cheeper.repository.*;

import java.io.*;
import java.util.*;

@WebServlet("/views/timeline")
public class TimelineViewController extends HttpServlet {

    private UserRepository userRepository;
    private PostRepository postRepository;
    private LikeService likeService;

    @Override
    public void init() throws ServletException {
        this.userRepository = new UserRepository();
        this.postRepository = new PostRepository();
        LikeRepository likeRepository = new LikeRepository();
        this.likeService = new LikeService(likeRepository);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = null;
        String username = req.getParameter("u");

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("email") != null) {
            String email = (String) session.getAttribute("email");
            Optional<User> currentUserOpt = userRepository.findByEmail(email);
            if (currentUserOpt.isPresent()) {
                currentUser = currentUserOpt.get();
            }
        }

        if (currentUser == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String timeline_type = req.getParameter("type");
        if (timeline_type == null) {
            timeline_type = (String) session.getAttribute("type");
        }
        if (timeline_type == null) {
            timeline_type = "for-you";
        }

        List<Post> posts = Collections.emptyList();

        try {
            switch (timeline_type) {
                case "for-you":
                    posts = postRepository.findAllButYou(currentUser.getId());
                    break;

                case "following":
                    posts = postRepository.findByFollowedUsers(currentUser.getId());
                    break;

                case "profile":
                    posts = postRepository.findByUserId(currentUser.getId());
                    break;

                case "comments":
                    String postIdParam = req.getParameter("postId");
                    if (postIdParam != null) {
                        try {
                            int postId = Integer.parseInt(postIdParam);
                            posts = postRepository.findBySourceId(postId);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid postId: " + postIdParam);
                        }
                    }
                    break;
                    
                default:
                    posts = postRepository.findAll();
                    break;
            }

            System.out.println("Timeline type: " + timeline_type);
            System.out.println("Current user ID: " + currentUser.getId());
            System.out.println("Posts found: " + posts.size());

            // Create maps for post data
            Map<Integer, Boolean> userLikes = new HashMap<>();
            Map<Integer, Integer> likeCounts = new HashMap<>();
            Map<Integer, User> postAuthors = new HashMap<>();
            
            for (Post post : posts) {
                boolean isLiked = likeService.isLikedByUser(post.getId(), currentUser.getId());
                int likeCount = likeService.getLikesForPost(post.getId()).size();
                
                userLikes.put(post.getId(), isLiked);
                likeCounts.put(post.getId(), likeCount);
                
                // Get post author information
                Optional<User> authorOpt = userRepository.findById(post.getUserId());
                if (authorOpt.isPresent()) {
                    postAuthors.put(post.getId(), authorOpt.get());
                }
            }

            req.setAttribute("userLikes", userLikes);
            req.setAttribute("likeCounts", likeCounts);
            req.setAttribute("postAuthors", postAuthors);

        } catch (Exception e) {
            System.err.println("Error loading posts: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading posts");
            return;
        }

        req.setAttribute("currentUser", currentUser);
        req.setAttribute("username", username);
        req.setAttribute("posts", posts);
        req.setAttribute("timeline_type", timeline_type);

        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher("/WEB-INF/views/components/timeline-view.jsp").forward(req, resp);
    }
}
