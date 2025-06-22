package com.webdev.cheeper.model;

import java.util.Date;

public class Room {
    private Integer id;
    private String name;
    private String description;
    private Integer createdBy;
    private Date createdAt;
    private Date expiresAt;
    private boolean isActive;

    public Room() {
        this.isActive = true; // Default value matching schema
    }

    public Room(Integer id, String name, String description, Integer createdBy, 
                Date createdAt, Date expiresAt, boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.isActive = isActive;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public boolean isActive() {
        return isActive;
    }

    // Setters 
    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
