package com.webdev.cheeper.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import com.webdev.cheeper.model.Message;
import com.webdev.cheeper.repository.RoomRepository;
import com.webdev.cheeper.service.MessageService;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/chat/{roomId}/{userId}")
public class ChatWebSocketServer {

    // Store active sessions, mapping roomId to a set of sessions
    private static final Map<Integer, Set<Session>> rooms = new ConcurrentHashMap<>();

    // Services for database interaction
    private static RoomRepository roomRepository;
    private static MessageService messageService;

    // Static initializer to set up services (or use dependency injection if available)
    static {
        roomRepository = new RoomRepository();
        messageService = new MessageService();
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") Integer roomId, @PathParam("userId") Integer userId) throws IOException {
        // Validate if the user is a participant in the room
        if (!roomRepository.isUserParticipant(roomId, userId)) {
            System.out.println("Unauthorized access attempt: User " + userId + " for room " + roomId);
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Unauthorized access to room"));
            return;
        }

        rooms.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(session);
        System.out.println("User " + userId + " joined room " + roomId + ". Session ID: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("roomId") Integer roomId, @PathParam("userId") Integer userId) {
        System.out.println("Received message from user " + userId + " in room " + roomId + ": " + message);
        try {
            JSONObject jsonMessage = new JSONObject(message);
            String content = jsonMessage.getString("content");

            // Persist the message
            Message savedMessage = messageService.saveMessage(roomId, userId, content);

            // Prepare the message to broadcast (same format as API response)
            JSONObject broadcastJson = new JSONObject();
            broadcastJson.put("id", savedMessage.getId());
            broadcastJson.put("roomId", savedMessage.getRoomId());
            broadcastJson.put("senderId", savedMessage.getSenderId());
            broadcastJson.put("content", savedMessage.getContent());
            broadcastJson.put("createdAt", savedMessage.getCreatedAt().getTime()); // Convert Date to timestamp

            // Broadcast the message to all users in the room
            broadcastMessage(roomId, broadcastJson.toString());

        } catch (Exception e) {
            System.err.println("Error processing message from user " + userId + " in room " + roomId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("roomId") Integer roomId, @PathParam("userId") Integer userId) {
        Set<Session> roomSessions = rooms.get(roomId);
        if (roomSessions != null) {
            roomSessions.remove(session);
            if (roomSessions.isEmpty()) {
                rooms.remove(roomId);
            }
        }
        System.out.println("User " + userId + " left room " + roomId + ". Session ID: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable, @PathParam("roomId") Integer roomId, @PathParam("userId") Integer userId) {
        System.err.println("Error for user " + userId + " in room " + roomId + ": " + throwable.getMessage());
        throwable.printStackTrace();
    }

    // Method to broadcast messages to all sessions in a specific room
    public static void broadcastMessage(Integer roomId, String message) {
        Set<Session> roomSessions = rooms.get(roomId);
        if (roomSessions != null) {
            for (Session session : roomSessions) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    System.err.println("Error broadcasting message to session " + session.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
