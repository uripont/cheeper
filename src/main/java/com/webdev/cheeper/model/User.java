package com.webdev.cheeper.model;

import java.io.Serializable;
import java.sql.Date;

public class User implements Serializable {
    private int id;
    private String fullName;
    private String email;
    private String username;
    private Date birthdate;
    private String biography;
    private String picture;
    private RoleType roleType;

    public User() {
        super();
    }    
    
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

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date date) {
        this.birthdate = date;
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
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    @Override
    public String toString() {
        return "User {" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", birthdate=" + birthdate +
                ", biography='" + biography + '\'' +
                ", picture='" + picture + '\'' +
                ", roleType=" + roleType +
                '}';
    }
}
