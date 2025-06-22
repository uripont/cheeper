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
        
        String otherUserIdStr = req.getParameter("otherUserId");
        if (otherUserIdStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No user ID provided");
            return;
        }

        Integer otherUserId = Integer.parseInt(otherUserIdStr);
        Optional<User> otherUserOpt = userRepository.findById(otherUserId);
        if (otherUserOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }
        User otherUser = otherUserOpt.get();

        // Get or create private room between the users
        Optional<Room> roomOpt = roomRepository.findPrivateRoomBetweenUsers(currentUser.getId(), otherUserId);
        Room room;
        if (roomOpt.isEmpty()) {
            // Create new room
            room = new Room();
            room.setName("Chat between " + currentUser.getUsername() + " and " + otherUser.getUsername());
            room.setPrivate(true);
            room.setCreatedAt(new Date());
            roomRepository.save(room);

            // Add participants
            roomRepository.addParticipant(room.getId(), currentUser.getId());
            roomRepository.addParticipant(room.getId(), otherUserId);
        } else {
            room = roomOpt.get();
        }

        // Load messages
        List<Message> messages = messageRepository.findByRoomId(room.getId());

        // Set attributes for JSP
        req.setAttribute("room", room);
        req.setAttribute("messages", messages);
        req.setAttribute("otherUser", otherUser);
        
        req.getRequestDispatcher("/WEB-INF/views/components/chats-view.jsp").forward(req, resp);
    }
}
