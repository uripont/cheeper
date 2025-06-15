package com.webdev.cheeper.controller;

import com.webdev.cheeper.model.Post;
import com.webdev.cheeper.model.User;
import com.webdev.cheeper.repository.PostRepository;
import com.webdev.cheeper.repository.UserRepository;
import java.util.Optional;
import com.webdev.cheeper.service.PostService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;


import java.io.IOException;
import java.sql.Timestamp;

@MultipartConfig
@WebServlet("/post")
public class PostServlet extends HttpServlet {
    private PostService postService;

    @Override
    public void init() throws ServletException {
        try {
            this.postService = new PostService(new PostRepository());
        } catch (Exception e) {
            throw new ServletException("Failed to initialize PostService", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String content = request.getParameter("content");
        System.out.println("Content received: " + content);

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = (String) session.getAttribute("email");
        System.out.println("Email: " + email);

        int userId;
        try (UserRepository userRepository = new UserRepository()) {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print("User not found");
                return;
            }
            userId = userOpt.get().getId();
        }

        System.out.println("User ID: " + userId);

        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setImage(null); // manejar imagen si decides activarlo
        //post.setSourceId();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        try {
            postService.createPost(post);
            response.sendRedirect(request.getContextPath() + "/app/home");
        } catch (Exception e) {
            request.setAttribute("error", "Post creation failed: " + e.getMessage());
            request.setAttribute("content", content);
            request.getRequestDispatcher("/WEB-INF/views/create-post.jsp").forward(request, response);
        }
    }
}
