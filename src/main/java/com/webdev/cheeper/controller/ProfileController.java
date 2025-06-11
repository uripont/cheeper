package com.webdev.cheeper.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.webdev.cheeper.model.*;
import com.webdev.cheeper.service.*;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.webdev.cheeper.repository.AssociationRepository;
import com.webdev.cheeper.repository.EntityRepository;
import com.webdev.cheeper.repository.FollowRepository;
import com.webdev.cheeper.repository.StudentRepository;
import com.webdev.cheeper.repository.UserRepository;

@WebServlet({"/profile", "/suggested-profile"})
public class ProfileController extends HttpServlet {

    private StudentService studentService;
    private EntityService entityService;
    private AssociationService associationService;

    // Configuration for images
    //TODO: Change across files and centralize on a file
    private static final String IMAGE_DIRECTORY = "/Users/martapuigmolina/eclipse-workspace/images"; 
    private static final String DEFAULT_IMAGE = "default.png";

    @Override
    public void init() throws ServletException {
        UserRepository userRepository = new UserRepository();
        studentService = new StudentService(userRepository, new StudentRepository());
        entityService = new EntityService(userRepository, new EntityRepository());
        associationService = new AssociationService(userRepository, new AssociationRepository());

        ensureImageDirectoryExists();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	if (req.getServletPath().equals("/profile")) {
            getProfile(req,resp);
        } else if (req.getServletPath().equals("/suggested-profile")) {
        	getSuggestedProfile(req,resp);
        }
        
    }
    
    private void getProfile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = (String) session.getAttribute("email");
        System.out.println("email" + email);

        try (UserRepository userRepository = new UserRepository()) {
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().print("User not found");
                return;
            }

            User user = userOpt.get();
            int userId = user.getId();
            RoleType role = user.getRoleType();

            System.out.println("User ID: " + userId + ", Role: " + role);
            
            Optional<? extends User> profile;
            if (role == RoleType.STUDENT) {
                profile = studentService.getProfile(userId);
            } else if (role == RoleType.ENTITY) {
                profile = entityService.getProfile(userId);
            } else if (role == RoleType.ASSOCIATION) {
                profile = associationService.getProfile(userId);
            } else {
                profile = Optional.empty();
            }
            System.out.println("the profile" + profile);

            if (profile.isPresent()) {
                User p = profile.get();
                
                // Ensure picture is set or fallback to default
                if (p.getPicture() == null || p.getPicture().trim().isEmpty()) {
                    p.setPicture(DEFAULT_IMAGE);
                }
                
                // Get follower counts
                FollowRepository followRepo = new FollowRepository();
                int followersCount = followRepo.countFollowers(p.getId());
                int followingCount = followRepo.countFollowing(p.getId());
                
                // Set attributes
                req.setAttribute("profile", p);
                req.setAttribute("followersCount", followersCount);
                req.setAttribute("followingCount", followingCount);
                
                // Forward request to JSP
                req.getRequestDispatcher("WEB-INF/profile.jsp").forward(req, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().print("User profile not found");
            }
        }
    }
    
    private void getSuggestedProfile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        if (username == null || username.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try (UserRepository userRepository = new UserRepository()) {
            Optional<User> userOpt = userRepository.findByUsername(username); 

            if (userOpt.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("User not found");
                return;
            }

            User user = userOpt.get();
            RoleType role = user.getRoleType();

            Optional<? extends User> profile;
            if (role == RoleType.STUDENT) {
                profile = studentService.getProfile(user.getId());
            } else if (role == RoleType.ENTITY) {
                profile = entityService.getProfile(user.getId());
            } else if (role == RoleType.ASSOCIATION) {
                profile = associationService.getProfile(user.getId());
            } else {
                profile = Optional.empty();
            }

            if (profile.isPresent()) {
                User p = profile.get();

                if (p.getPicture() == null || p.getPicture().trim().isEmpty()) {
                    p.setPicture(DEFAULT_IMAGE);
                }

                FollowRepository followRepo = new FollowRepository();
                int followersCount = followRepo.countFollowers(p.getId());
                int followingCount = followRepo.countFollowing(p.getId());

                req.setAttribute("profile", p);
                req.setAttribute("followersCount", followersCount);
                req.setAttribute("followingCount", followingCount);
                req.setAttribute("readOnly", true); // <- Important flag

                req.getRequestDispatcher("WEB-INF/suggested-profile.jsp").forward(req, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    private void ensureImageDirectoryExists() {
        File imageDir = new File(IMAGE_DIRECTORY);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
    }
}
