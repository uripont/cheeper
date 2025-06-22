package com.webdev.cheeper.model;

import java.util.Date;

public class Room {
    private Integer id;
    private String name;
    private boolean isPrivate;
    private Date createdAt;

    public Room() {}

    public Room(Integer id, String name, boolean isPrivate, Date createdAt) {
        this.id = id;
        this.name = name;
        this.isPrivate = isPrivate;
        this.createdAt = createdAt;
    }

    // Getters
    public Integer getId() {
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
    public void setId(Integer id) {
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
