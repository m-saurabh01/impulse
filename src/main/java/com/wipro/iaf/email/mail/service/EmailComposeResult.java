package com.wipro.iaf.email.mail.service;

import java.util.List;

/**
 * Result of email compose operation with validation status
 */
public class EmailComposeResult {
    
    private final boolean success;
    private final List<String> errors;

    public EmailComposeResult(boolean success, List<String> errors) {
        this.success = success;
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getErrorMessage() {
        if (errors == null || errors.isEmpty()) {
            return null;
        }
        return String.join("; ", errors);
    }
}
