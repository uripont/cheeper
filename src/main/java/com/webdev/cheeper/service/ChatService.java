package com.webdev.cheeper.service;

import com.webdev.cheeper.model.Room;
import com.webdev.cheeper.repository.RoomRepository;
import java.util.Date;

public class ChatService {
    private RoomRepository roomRepository;
    
    public ChatService() {
        this.roomRepository = new RoomRepository();
    }
    
    public Room createPrivateRoom(Integer user1Id, Integer user2Id) {
        try {
            // Create new room
            Room room = new Room();
            room.setName("Private Chat");
            room.setDescription("Private chat between users");
            room.setCreatedBy(user1Id); // Set the creator as user1
            room.setCreatedAt(new Date());
            room.setActive(true);
            // No expiration for private chats
            room.setExpiresAt(null);
            
            System.out.println("Creating new room with creator: " + user1Id);
            
            // Save room to get ID
            roomRepository.save(room);
            System.out.println("Room created with ID: " + room.getId());
            
            // Add both users as participants
            roomRepository.addParticipant(room.getId(), user1Id);
            System.out.println("Added user1 (" + user1Id + ") to room");
            
            roomRepository.addParticipant(room.getId(), user2Id);
            System.out.println("Added user2 (" + user2Id + ") to room");
            
            return room;
            
        } catch (Exception e) {
            System.out.println("Error creating private room: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
