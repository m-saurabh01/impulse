package com.wipro.iaf.email.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Listens for authentication events to integrate with rate limiting.
 */
@Component
public class AuthenticationEventListener {

    private final LoginRateLimiter rateLimiter;

    public AuthenticationEventListener(LoginRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        String ipAddress = getClientIP();
        if (ipAddress != null) {
            rateLimiter.recordFailedAttempt(ipAddress);
        }
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String ipAddress = getClientIP();
        if (ipAddress != null) {
            rateLimiter.clearAttempts(ipAddress);
        }
    }

    private String getClientIP() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
}
