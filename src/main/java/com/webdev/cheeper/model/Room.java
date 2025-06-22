package com.webdev.cheeper.model;

import java.util.Date;

public class Room {
    private Long id;
    private String name;
    private boolean isPrivate;
    private Date createdAt;

    public Room() {}

    public Room(Long id, String name, boolean isPrivate, Date createdAt) {
        this.id = id;
        this.name = name;
        this.isPrivate = isPrivate;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    // Setters 
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
