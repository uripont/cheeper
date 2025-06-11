package com.webdev.cheeper.model;

import java.sql.Timestamp;

public class Association extends User {
    private static final long serialVersionUID = 1L;
    private VerfStatus verificationStatus;
    private Timestamp verificationDate;

    public Association() {
        super();
    }

    @Override
    public RoleType getUserType() {
        return RoleType.ASSOCIATION;
    }

    // Getters and Setters
    public VerfStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerfStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public Timestamp getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(Timestamp verificationDate) {
        this.verificationDate = verificationDate;
    }
}