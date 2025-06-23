package com.webdev.cheeper.model;

import java.sql.Timestamp;

public class Association extends User {
    private static final long serialVersionUID = 1L;
    private VerificationStatus verificationStatus;
    private Timestamp verificationDate;

    public Association() {
        super();
    }

    @Override
    public RoleType getUserType() {
        return RoleType.ASSOCIATION;
    }

    // Getters and Setters
    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public Timestamp getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(Timestamp verificationDate) {
        this.verificationDate = verificationDate;
    }
}