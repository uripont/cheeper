package com.webdev.cheeper.model;

import java.sql.Timestamp;

public class Like {
    private int userId;
    private int postId;
    private Timestamp createdAt;

    public Like() {}

    public Like(int userId, int postId, Timestamp createdAt) {
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}