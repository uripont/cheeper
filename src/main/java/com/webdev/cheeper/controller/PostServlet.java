package com.webdev.cheeper.controller;

import com.webdev.cheeper.model.Post;
import com.webdev.cheeper.model.User;
import com.webdev.cheeper.repository.PostRepository;
import com.webdev.cheeper.repository.UserRepository;
import com.webdev.cheeper.service.PostService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Optional;
import java.io.File;

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
        String sourceIdParam = request.getParameter("source_id");
        Integer sourceId = null;

        System.out.println("[PostServlet] Content received: " + content);

        if (sourceIdParam != null && !sourceIdParam.trim().isEmpty()) {
            try {
                sourceId = Integer.parseInt(sourceIdParam);
                System.out.println("[PostServlet] This is a reply to post ID: " + sourceId);
            } catch (NumberFormatException e) {
                System.err.println("[PostServlet] Invalid source_id: " + sourceIdParam);
            }
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = (String) session.getAttribute("email");
        System.out.println("[PostServlet] Email: " + email);

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

        System.out.println("[PostServlet] User ID: " + userId);

        Part imagePart = request.getPart("image");
        String imagePath = null;

        if (imagePart != null && imagePart.getSize() > 0) {
            String fileName = System.currentTimeMillis() + "_" + imagePart.getSubmittedFileName();

            // Obtiene ruta física absoluta dentro del servidor
            String uploadDir = getServletContext().getRealPath("/static/images/uploads");
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            File imageFile = new File(uploadDirFile, fileName);
            imagePart.write(imageFile.getAbsolutePath());

            // Ruta que se guarda en DB (relativa a /static/images/)
            imagePath = "uploads/" + fileName;
        }

        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setImage(imagePath);
        post.setSourceId(sourceId); 
        Timestamp now = new Timestamp(System.currentTimeMillis());
        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        try {
            postService.createPost(post);

            // Not needed since using ajax calls to create posts
            /* if (sourceId == null) {
                response.sendRedirect(request.getContextPath() + "/app/home");
            } */

        } catch (Exception e) {
            request.setAttribute("error", "Post creation failed: " + e.getMessage());
            request.setAttribute("content", content);
            request.getRequestDispatcher("/WEB-INF/views/create-post.jsp").forward(request, response);
        }
    }
}


