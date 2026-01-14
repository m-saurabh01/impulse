package com.wipro.iaf.email.mail.dto;

/**
 * DTO for WebSocket email notifications
 */
public class EmailNotification {
    
    private Long emailId;
    private String senderEmail;
    private String subject;
    private String preview;
    private String timestamp;

    public EmailNotification() {}

    public EmailNotification(Long emailId, String senderEmail, String subject, String preview, String timestamp) {
        this.emailId = emailId;
        this.senderEmail = senderEmail;
        this.subject = subject;
        this.preview = preview;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getEmailId() {
        return emailId;
    }

    public void setEmailId(Long emailId) {
        this.emailId = emailId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
