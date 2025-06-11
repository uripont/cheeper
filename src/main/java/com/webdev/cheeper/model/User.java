package com.webdev.cheeper.model;

import java.io.Serializable;


public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String fullName;
    private String email;
    private String username;
    private String biography;
    private String picture;
    private RoleType role;

    public User() {
        super();
    }

    public RoleType getUserType() {
        return RoleType.GENERIC;
    }

    // Getters and Setters
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

    public RoleType getRoleType() {
        return role;
    }

    public void setRoleType(RoleType role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", biography='" + biography + '\'' +
                ", role=" + role +
                '}';
    }
}