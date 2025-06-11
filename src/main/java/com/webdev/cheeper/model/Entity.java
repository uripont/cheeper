package com.webdev.cheeper.model;


public class Entity extends User {
    private String department;

    public Entity() {
        super();
    }

    @Override
    public RoleType getUserType() {
        return RoleType.ENTITY;
    }

    // Getters and Setters
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}