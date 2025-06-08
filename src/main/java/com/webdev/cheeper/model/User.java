package com.webdev.cheeper.model;

import java.io.Serializable;

public class User implements Serializable {
	
	private int id;
    private String fullName;
    private String email;
    private String username;
    private java.sql.Date birthdate;
    private String biography;

    // We are doing data validation in the service layer, so we don't need to validate here.
    // This is a simple POJO (Plain Old Java Object) class.
    // Any client can't register a user in the database without going through the service layer, that 
    // takes care of the validation of a populated User object.

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

    // LocalDate getter/setter (recommended for Java 8+)
    public java.sql.Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(java.sql.Date date) {
        this.birthdate = date;
    }


    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    @Override
    public String toString() {
        return "User {" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", birthdate=" + birthdate +
                ", biography='" + biography + '\'' +
                '}';
    }

	
}