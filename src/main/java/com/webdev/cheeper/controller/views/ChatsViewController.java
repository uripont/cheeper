package com.webdev.cheeper.controller.views;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.webdev.cheeper.model.*;
import com.webdev.cheeper.service.*;
import com.webdev.cheeper.repository.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Date;

@WebServlet("/views/chats")
public class ChatsViewController extends HttpServlet {
    
    private UserRepository userRepository;
    private FollowRepository followRepository;
    private RoomRepository roomRepository;
    private MessageRepository messageRepository;
    
    @Override
    public void init() throws ServletException {
        this.userRepository = new UserRepository();
        this.followRepository = new FollowRepository();
        this.roomRepository = new RoomRepository();
        this.messageRepository = new MessageRepository();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = null;

        // Get current user from session if available
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("email") != null) {
            String email = (String) session.getAttribute("email");
            Optional<User> currentUserOpt = userRepository.findByEmail(email);
            if (currentUserOpt.isPresent()) {
                currentUser = currentUserOpt.get();
            }
        }

        // Require authentication
        if (currentUser == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Set attributes for JSP
        req.setAttribute("currentUser", currentUser);

        // Forward to appropriate view
        resp.setContentType("text/html;charset=UTF-8");
        String action = req.getParameter("action");
        if (action != null && action.equals("load-conversation")) {
            handleLoadConversation(req, resp, currentUser);
        }
        else if (req.getParameter("component") != null && req.getParameter("component").equals("private-chat-users")) {
            // Get mutual followers (users who follow each other)
            List<User> mutualUsers = followRepository.getMutualFollowers(currentUser.getId());
            
            // Handle search query if present
            String searchQuery = req.getParameter("q");
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String searchLower = searchQuery.toLowerCase();
                mutualUsers = mutualUsers.stream()
                    .filter(user -> user.getUsername().toLowerCase().contains(searchLower) || 
                                  user.getFullName().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
                req.setAttribute("searchQuery", searchQuery);
            }
            
            // Set users list for JSP
            req.setAttribute("users", mutualUsers);
            
            req.getRequestDispatcher("/WEB-INF/views/components/private-chat-users-view.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/WEB-INF/views/components/chats-view.jsp").forward(req, resp);
        }
    }

    private void handleLoadConversation(HttpServletRequest req, HttpServletResponse resp, User currentUser) 
            throws ServletException, IOException {
        
        System.out.println("Loading conversation...");
        System.out.println("Current user: " + currentUser.getId() + " (" + currentUser.getUsername() + ")");
        
        String otherUserIdStr = req.getParameter("otherUserId");
        System.out.println("Requested other user ID: " + otherUserIdStr);
        
        if (otherUserIdStr == null) {
            System.out.println("Error: No otherUserId provided");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No user ID provided");
            return;
        }

        try {
            Integer otherUserId = Integer.parseInt(otherUserIdStr);
            Optional<User> otherUserOpt = userRepository.findById(otherUserId);
            
            if (otherUserOpt.isEmpty()) {
                System.out.println("Error: User not found with ID: " + otherUserId);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }
            
            User otherUser = otherUserOpt.get();
            System.out.println("Found other user: " + otherUser.getId() + " (" + otherUser.getUsername() + ")");

            // Try to find existing room
            Optional<Room> roomOpt = roomRepository.findPrivateRoomBetweenUsers(currentUser.getId(), otherUserId);
            System.out.println("Checking for existing room...");
            
            if (roomOpt.isPresent()) {
                Room room = roomOpt.get();
                System.out.println("Found room: " + room.getId());
                
                // Get latest messages for preview
                List<Message> messages = messageRepository.findLatestByRoomId(room.getId(), 5);
                int totalMessages = messageRepository.findByRoomId(room.getId()).size();
                
                System.out.println("Room has " + totalMessages + " messages total");
                System.out.println("Retrieved " + messages.size() + " latest messages for preview");
                
                req.setAttribute("room", room);
                req.setAttribute("messages", messages);
                req.setAttribute("totalMessages", totalMessages);
            } else {
                System.out.println("No existing room found between users");
            }

            // Set attributes for JSP
            req.setAttribute("otherUser", otherUser);
            
            System.out.println("Forwarding to chat view...");
            req.getRequestDispatcher("/WEB-INF/views/components/chats-view.jsp").forward(req, resp);
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid user ID format: " + otherUserIdStr);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format");
        }
    }
}
