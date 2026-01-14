package com.wipro.iaf.email.mail.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.wipro.iaf.email.mail.dto.EmailNotification;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Send a new email notification to a specific user
     * 
     * @param userEmail The recipient's email address (used as principal name)
     * @param notification The notification payload
     */
    public void notifyNewEmail(String userEmail, EmailNotification notification) {
        // Send to user-specific queue: /user/{email}/queue/inbox
        messagingTemplate.convertAndSendToUser(
            userEmail, 
            "/queue/inbox", 
            notification
        );
    }

    /**
     * Send notification to multiple recipients
     */
    public void notifyNewEmailToAll(java.util.List<String> recipients, EmailNotification notification) {
        for (String recipient : recipients) {
            notifyNewEmail(recipient, notification);
        }
    }
}
