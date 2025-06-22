package com.webdev.cheeper.model;

import java.util.Date;

public class Message {
    private Integer id;
    private Integer roomId;
    private Integer senderId;
    private String content;
    private Date createdAt;

    public Message() {}

    public Message(Integer id, Integer roomId, Integer senderId, String content, Date createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.senderId = senderId;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
