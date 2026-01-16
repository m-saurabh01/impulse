package com.wipro.iaf.email.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for read receipt notifications sent via WebSocket
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadReceiptNotification {
    
    private Long emailId;
    private String readerEmail;
    private String subject;
    private String readAt;
    
}
