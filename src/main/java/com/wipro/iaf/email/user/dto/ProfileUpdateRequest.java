package com.wipro.iaf.email.user.dto;

/**
 * DTO for profile update request
 */
public class ProfileUpdateRequest {
    
    private String displayName;
    private String signature;

    public ProfileUpdateRequest() {}

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
