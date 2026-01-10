package com.wipro.iaf.email.mail.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ComposeEmailRequest {

    private String subject;
    private String bodyHtml;

    private List<String> to;
    private List<String> cc;
    private List<String> bcc;

    private boolean draft;
    private Long threadId;

    private List<MultipartFile> attachments;
  
}
