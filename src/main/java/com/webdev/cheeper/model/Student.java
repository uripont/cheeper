package com.webdev.cheeper.model;

import java.sql.Date;
import java.util.Map;

public class Student extends User {
    private Date birthdate;
    private Map<String, String> socialLinks;
    private Map<String, String> degrees;
    private Map<String, String> enrolledSubjects;

    public Student() {
        super();
    }

    @Override
    public RoleType getUserType() {
        return RoleType.STUDENT;
    }

    // Getters and Setters
    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Map<String, String> getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(Map<String, String> socialLinks) {
        this.socialLinks = socialLinks;
    }

    public Map<String, String> getDegrees() {
        return degrees;
    }

    public void setDegrees(Map<String, String> degrees) {
        this.degrees = degrees;
    }

    public Map<String, String> getEnrolledSubjects() {
        return enrolledSubjects;
    }

    public void setEnrolledSubjects(Map<String, String> enrolledSubjects) {
        this.enrolledSubjects = enrolledSubjects;
    }
}