package com.webdev.cheeper.model;

import java.sql.Timestamp;

public class Post {
    private int postId;
    private Integer sourceId;  // ✅ Cambiado de int a Integer
    private int userId;
    private String content;
    private String image;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Post() {}

    public Post(int postId, Integer sourceId, int userId, String content, String image, Timestamp createdAt, Timestamp updatedAt) {
        this.postId = postId;
        this.sourceId = sourceId;
        this.userId = userId;
        this.content = content;
        this.image = image;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Post(int postId, int userId, String content, String image, Timestamp createdAt, Timestamp updatedAt) {
        this(postId, null, userId, content, image, createdAt, updatedAt);
    }

    public int getId() {
        return postId;
    }

    public void setId(int id) {
        this.postId = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + postId +
                ", sourceId=" + sourceId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", image='" + image + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
